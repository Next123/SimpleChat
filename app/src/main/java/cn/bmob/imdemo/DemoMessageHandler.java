package cn.bmob.imdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.bmob.imdemo.bean.AddFriendMessage;
import cn.bmob.imdemo.bean.AgreeAddFriendMessage;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.callback.BitmapCallback;
import cn.bmob.imdemo.db.NewFriend;
import cn.bmob.imdemo.db.NewFriendManager;
import cn.bmob.imdemo.event.RefreshEvent;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.model.i.UpdateCacheListener;
import cn.bmob.imdemo.ui.activity.MainActivity;
import cn.bmob.imdemo.util.ImageDownload;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 消息接收器
 *
 * @author smile
 * @project DemoMessageHandler
 * @date 2016-03-08-17:37
 */
class DemoMessageHandler extends BmobIMMessageHandler {

    private Context context;

    DemoMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceive(final MessageEvent event) {
        excuteMessage(event);
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
        Map<String, List<MessageEvent>> map = event.getEventMap();
        Logger.i("离线消息属于" + map.size() + "个用户");
        //挨个检测下离线消息所属的用户的信息是否需要更新
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                excuteMessage(list.get(i));
            }
        }
    }

    /**
     * 处理消息
     *
     * @param event
     */
    private void excuteMessage(final MessageEvent event) {
        //检测用户信息是否需要更新
        UserModel.getInstance().updateUserInfo(event, new UpdateCacheListener() {
            @Override
            public void done(BmobException e) {
                final BmobIMMessage msg = event.getMessage();
                if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {//用户自定义的消息类型，其类型值均为0
                    processCustomMessage(msg, event.getFromUserInfo());
                } else {//SDK内部内部支持的消息类型
                    if (BmobNotificationManager.getInstance(context).isShowNotification()) {//如果需要显示通知栏，SDK提供以下两种显示方式：
                        final Intent pendingIntent = new Intent(context, MainActivity.class);
                        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //final PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        //1、多个用户的多条消息合并成一条通知：有XX个联系人发来了XX条消息
                        //BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent);
                        //2、自定义通知消息：始终只有一条通知，新消息覆盖旧消息
                        final BmobIMUserInfo info = event.getFromUserInfo();
                        //这里可以是应用图标，也可以将聊天头像转成bitmap
                        new ImageDownload().getBitmap(info.getAvatar(), new BitmapCallback() {
                            @Override
                            public void getDownloadBitmap(Bitmap bitmap) {
                                BmobNotificationManager.getInstance(context).showNotification(bitmap,
                                        info.getName(), msg.getContent(), "您有一条新消息", pendingIntent);
//                                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                                Notification.Builder builder = new Notification.Builder(context)
//                                        .setLargeIcon(bitmap)
//                                        .setAutoCancel(true)
//                                        .setContentTitle(info.getName())
//                                        .setContentText(msg.getContent())
//                                        .setDefaults(Notification.DEFAULT_ALL)
//                                        .setContentIntent(mainPendingIntent)
//                                        .setSound(RingtoneUtils.getRingtoneUriPath(RingtoneManager.TYPE_NOTIFICATION, SPUtils.getInt(Const.NOTICE_SOUND, 0)));
//                                manager.notify(1, builder.build());
                            }
                        });
                    } else {//直接发送消息事件
                        Logger.i("当前处于应用内，发送event");
                        EventBus.getDefault().post(event);
                    }
                }
            }
        });
    }

    /**
     * 处理自定义消息类型
     *
     * @param msg
     */
    private void processCustomMessage(BmobIMMessage msg, BmobIMUserInfo info) {
        //自行处理自定义消息类型
        Logger.i(msg.getMsgType() + "," + msg.getContent() + "," + msg.getExtra());
        String type = msg.getMsgType();
        //发送页面刷新的广播
        EventBus.getDefault().post(new RefreshEvent());
        //处理消息
        switch (type) {
            case "add": //接收到的添加好友的请求
                NewFriend friend = AddFriendMessage.convert(msg);
                //本地好友请求表做下校验，本地没有的才允许显示通知栏--有可能离线消息会有些重复
                long id = NewFriendManager.getInstance(context).insertOrUpdateNewFriend(friend);
                if (id > 0) {
                    showAddNotify(friend);
                }
                break;
            case "agree": //接收到的对方同意添加自己为好友,此时需要做的事情：1、添加对方为好友，2、显示通知
                AgreeAddFriendMessage agree = AgreeAddFriendMessage.convert(msg);
                addFriend(agree.getFromId());//添加消息的发送方为好友

                //这里应该也需要做下校验--来检测下是否已经同意过该好友请求，我这里省略了
                showAgreeNotify(info, agree);
                break;
            default:
                Toast.makeText(context, "接收到的自定义消息：" + msg.getMsgType() + "," + msg.getContent() + "," + msg.getExtra(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 显示对方添加自己为好友的通知
     *
     * @param friend
     */
    private void showAddNotify(final NewFriend friend) {
        final Intent pendingIntent = new Intent(context, MainActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //这里可以是应用图标，也可以将聊天头像转成bitmap
        new ImageDownload().getBitmap(friend.getAvatar(), new BitmapCallback() {
            @Override
            public void getDownloadBitmap(Bitmap bitmap) {
                BmobNotificationManager.getInstance(context).showNotification(bitmap,
                        friend.getName(), friend.getMsg(), friend.getName() + "请求添加你为朋友", pendingIntent);
            }
        });
    }

    /**
     * 显示对方同意添加自己为好友的通知
     *
     * @param info
     * @param agree
     */
    private void showAgreeNotify(final BmobIMUserInfo info, final AgreeAddFriendMessage agree) {
        final Intent pendingIntent = new Intent(context, MainActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        new ImageDownload().getBitmap(info.getAvatar(), new BitmapCallback() {
            @Override
            public void getDownloadBitmap(Bitmap bitmap) {
                BmobNotificationManager.getInstance(context).showNotification(bitmap, info.getName(), agree.getMsg(), agree.getMsg(), pendingIntent);
            }
        });
    }

    /**
     * 添加对方为自己的好友
     *
     * @param uid
     */
    private void addFriend(String uid) {
        User user = new User();
        user.setObjectId(uid);
        UserModel.getInstance().agreeAddFriend(user, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Logger.e("success");
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }
}
