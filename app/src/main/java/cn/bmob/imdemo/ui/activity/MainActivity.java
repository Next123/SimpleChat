package cn.bmob.imdemo.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.db.NewFriendManager;
import cn.bmob.imdemo.event.RefreshEvent;
import cn.bmob.imdemo.ui.fragment.ContactFragment;
import cn.bmob.imdemo.ui.fragment.ConversationFragment;
import cn.bmob.imdemo.ui.fragment.MineFragment;
import cn.bmob.imdemo.util.IMMLeaks;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * @author :smile
 * @project:MainActivity
 * @date :2016-01-15-18:23
 */
public class MainActivity extends FragmentActivity implements ObseverListener, ViewPager.OnPageChangeListener {

    @Bind(R.id.tab_img_chats)
    ImageView mImageChats;
    @Bind(R.id.tab_text_chats)
    TextView mTextChats;
    @Bind(R.id.tab_img_contact)
    ImageView mImageContact;
    @Bind(R.id.tab_text_contact)
    TextView mTextContact;
    @Bind(R.id.tab_img_me)
    ImageView mImageMe;
    @Bind(R.id.tab_text_me)
    TextView mTextMe;

    //底部小红点
    @Bind(R.id.chats_red)
    ImageView chatsRed;
    @Bind(R.id.contact_red)
    ImageView contactRed;
    @Bind(R.id.mine_red)
    ImageView mineRed;


    @Bind(R.id.main_pager)
    ViewPager mainPager;


    @Bind(R.id.seal_chat)
    RelativeLayout chatRLayout;
    @Bind(R.id.seal_contact_list)
    RelativeLayout contactRLayout;
    @Bind(R.id.seal_me)
    RelativeLayout mineRLayout;
    @Bind(R.id.top_iv_search)
    ImageView top_search;
    @Bind(R.id.top_iv_add)
    ImageView top_add;

    private List<Fragment> fragments;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        changeTextViewColor();
        changeSelectedTabState(0);
        initViewPager();

        //connect server
        final User user = BmobUser.getCurrentUser(User.class);
        /**
         * 连接前先判断uid是否为空
         */
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        Logger.i("connect success");
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        EventBus.getDefault().post(new RefreshEvent());
                        /**
                         *  连接成功后再进行修改本地用户信息的操作，并查询本地用户信息
                         */
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.getAvatar()));
                        BmobIMUserInfo bmobIMUserInfo = BmobIM.getInstance().
                                getUserInfo(BmobUser.getCurrentUser().getObjectId());
                        Logger.i(bmobIMUserInfo.getUserId() + "\n" + bmobIMUserInfo.getName());
                    } else {
                        Logger.e(e);
                    }
                }
            });
            //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    Toast.makeText(MainActivity.this, "在线状态：" + status.getMsg(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());

    }

    private void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new ConversationFragment());
        fragments.add(new ContactFragment());
        fragments.add(new MineFragment());
        //设置适配器
        mainPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        //预加载
        mainPager.setOffscreenPageLimit(fragments.size());
        mainPager.setOnPageChangeListener(this);

    }
//    初始化底部导航
//    private void initTab() {
//        conversationFragment = new ConversationFragment();
//        setFragment = new MineFragment();
//        contactFragment = new ContactFragment();
//        fragments = new Fragment[]{conversationFragment, contactFragment, setFragment};
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, conversationFragment).
//                add(R.id.fragment_container, contactFragment)
//                .add(R.id.fragment_container, setFragment)
//                .hide(setFragment).hide(contactFragment)
//                .show(conversationFragment).commit();
//    }
//
//    public void onTabSelect(View view) {
//        switch (view.getId()) {
//            case R.id.btn_conversation:
//                index = 0;
//                break;
//            case R.id.btn_contact:
//                index = 1;
//                break;
//            case R.id.btn_set:
//                index = 2;
//                break;
//        }
//        onTabIndex(index);
//    }

//    private void onTabIndex(int index) {
//        if (currentTabIndex != index) {
//            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
//            trx.hide(fragments[currentTabIndex]);
//            if (!fragments[index].isAdded()) {
//                trx.add(R.id.fragment_container, fragments[index]);
//            }
//            trx.show(fragments[index]).commit();
//        }
//        mTabs[currentTabIndex].setSelected(false);
//        mTabs[index].setSelected(true);
//        currentTabIndex = index;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示小红点
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        checkRedPoint();
    }

    //在底部导航栏显示小红点
    private void checkRedPoint() {
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            chatsRed.setVisibility(View.VISIBLE);
        } else {
            chatsRed.setVisibility(View.GONE);
        }
        //是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            contactRed.setVisibility(View.VISIBLE);
        } else {
            contactRed.setVisibility(View.GONE);
        }
    }

    //底部导航栏
    private void changeSelectedTabState(int position) {
        switch (position) {
            case 0:
                mTextChats.setTextColor(Color.parseColor("#0099ff"));
                mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_chat_hover));
                break;
            case 1:
                mTextContact.setTextColor(Color.parseColor("#0099ff"));
                mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_contacts_hover));
                break;
            case 2:
                mTextMe.setTextColor(Color.parseColor("#0099ff"));
                mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_me_hover));
                break;
        }
    }

    //底部文字颜色
    private void changeTextViewColor() {
        mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_chat));
        mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_contacts));
        mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_me));
        mTextChats.setTextColor(Color.parseColor("#abadbb"));
        mTextContact.setTextColor(Color.parseColor("#abadbb"));
        mTextMe.setTextColor(Color.parseColor("#abadbb"));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTextViewColor();
        changeSelectedTabState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.seal_chat, R.id.seal_contact_list, R.id.seal_me,R.id.top_iv_search, R.id.top_iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.seal_chat:
                //是否平滑过渡  false
                mainPager.setCurrentItem(0, false);
                break;
            case R.id.seal_contact_list:
                mainPager.setCurrentItem(1, false);
                break;
            case R.id.seal_me:
                mainPager.setCurrentItem(2, false);
                break;
            case R.id.top_iv_search://搜索
                break;
            case R.id.top_iv_add://添加
                startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
                break;
        }
    }

}
