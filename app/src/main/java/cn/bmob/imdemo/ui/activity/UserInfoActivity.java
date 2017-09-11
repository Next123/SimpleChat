package cn.bmob.imdemo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.AddFriendMessage;
import cn.bmob.imdemo.bean.Friend;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.ui.widget.LoadDialog;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户资料
 */
public class UserInfoActivity extends BaseActivity {

    @Bind(R.id.iv_avatar)
    ImageView iv_avatar;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_motto)
    TextView tv_motto;

    @Bind(R.id.btn_add_friend)
    Button btn_add_friend;
    @Bind(R.id.btn_delete_friend)
    Button btn_delete_friend;

    @Bind(R.id.tv_gender)
    TextView tv_gender;
    @Bind(R.id.tv_birthday)
    TextView tv_birthday;
    @Bind(R.id.tv_email)
    TextView tv_email;

    User user;
    BmobIMUserInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        setTitle("用户资料");
        btn_add_friend.setEnabled(true);
        user = (User) getBundle().getSerializable("u");
        boolean isConversation = getBundle().getBoolean("isConversation");
        if (user.getObjectId().equals(getCurrentUid())) {
            //如果是当前用户  隐藏添加好友view
            btn_add_friend.setVisibility(View.GONE);
            btn_delete_friend.setVisibility(View.GONE);
        } else {
            if (isConversation) {
                btn_add_friend.setVisibility(View.GONE);
                btn_delete_friend.setVisibility(View.VISIBLE);
            } else {
                btn_add_friend.setVisibility(View.VISIBLE);
                btn_delete_friend.setVisibility(View.GONE);
            }
        }
        //构造聊天方的用户信息:传入用户id、用户名和用户头像三个参数
        info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar, user.getAvatar(), R.mipmap.head);
        tv_name.setText(user.getUsername());
        if (user.getMotto() != null) {
            tv_motto.setText(user.getMotto());
            tv_birthday.setText(user.getBirthday());
            tv_email.setText(user.getEmail());
            tv_gender.setText(user.isGender() ? "男" : "女");
        } else {
            tv_motto.setText("这个人很懒，什么都没留下");
            tv_birthday.setText("未设置");
            tv_email.setText("未设置");
            tv_gender.setText("未设置");
        }

    }

    @OnClick(R.id.btn_add_friend)
    public void onAddClick(View view) {
        sendAddFriendMessage();
        btn_add_friend.setEnabled(false);
    }

    /**
     * 发送添加好友的请求
     */
    private void sendAddFriendMessage() {
        //启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
        //设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
        map.put("avatar", currentUser.getAvatar());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    toast("好友请求发送成功，等待验证");
                } else {//发送失败
                    toast("发送失败:" + e.getMessage());
                }
            }
        });
    }

    //删除好友
    @OnClick(R.id.btn_delete_friend)
    public void onChatClick(View view) {
        LoadDialog.show(mContext);
        deleteFriend();
        LoadDialog.dismiss(mContext);
        ChatActivity.chatActivity.finish();
        startActivity(MainActivity.class, null, true);
    }

    private void deleteFriend() {
        BmobQuery<Friend> query1 = new BmobQuery<Friend>();
        query1.addWhereEqualTo("user", user);
        query1.addWhereEqualTo("friendUser", getCurrentUser());
        query1.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).delete(list.get(i).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    LoadDialog.dismiss(UserInfoActivity.this);
                                    toast("删除好友失败：" + e);
                                }
                            }
                        });
                    }
                } else {
                    LoadDialog.dismiss(UserInfoActivity.this);
                    toast("查找好友失败：" + e);
                }

            }
        });

        BmobQuery<Friend> query2 = new BmobQuery<Friend>();
        query2.addWhereEqualTo("user", getCurrentUser());
        query2.addWhereEqualTo("friendUser", user);
        query2.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).delete(list.get(i).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    LoadDialog.dismiss(UserInfoActivity.this);
                                    toast("删除好友失败：" + e);
                                }
                            }
                        });
                    }
                } else {
                    LoadDialog.dismiss(UserInfoActivity.this);
                    toast("查找好友失败：" + e);
                }

            }
        });
    }

}
