package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.BmobIMApplication;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseFragment;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.ui.activity.AboutActivity;
import cn.bmob.imdemo.ui.activity.AccountSettingActivity;
import cn.bmob.imdemo.ui.activity.HelperActivity;
import cn.bmob.imdemo.ui.activity.MyAccountActivity;
import cn.bmob.imdemo.ui.activity.NewMessageRemindActivity;

/**
 * 设置
 *
 * @author :smile
 * @project:SetFragment
 * @date :2016-01-25-18:23
 */
public class MineFragment extends BaseFragment {

    @Bind(R.id.mine_header)
    ImageView mineHeader;
    @Bind(R.id.mine_name)
    TextView mineName;
    @Bind(R.id.start_user_profile)
    LinearLayout startUserProfile;
    @Bind(R.id.mine_setting)
    LinearLayout mineSetting;
    @Bind(R.id.mine_message)
    LinearLayout mineMessage;
    @Bind(R.id.mine_feedback)
    LinearLayout mineFeedback;
    @Bind(R.id.new_version_icon)
    ImageView newVersionIcon;
    @Bind(R.id.mine_about)
    LinearLayout mineAbout;

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, rootView);
        User user = UserModel.getInstance().getCurrentUser();
        String username = user.getUsername();
        mineName.setText(TextUtils.isEmpty(username) ? "" : username);
        ImageLoaderFactory.getLoader().loadAvator(mineHeader, user.getAvatar(),R.mipmap.head);
        return rootView;
    }

    @Override
    public void onResume() {//回到当前activity时调用
        super.onResume();
        //刷新头像
        ImageLoaderFactory.getLoader().loadAvator(mineHeader,getCurrentUser().getAvatar(),R.mipmap.head);
        mineName.setText(getCurrentUser().getUsername());
    }

    @Override
    public void onStop() {//跳转到其它activity时调用
        super.onStop();

    }


    @Override
    public void onDestroyView() {//退出应用时调用
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.start_user_profile, R.id.mine_setting, R.id.mine_message, R.id.mine_feedback,R.id.mine_helper, R.id.mine_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //用户信息
            case R.id.start_user_profile:
                startActivity(MyAccountActivity.class,null,false);
                break;
            //账号设置
            case R.id.mine_setting:
                startActivity(AccountSettingActivity.class,null,false);
                break;
            //消息通知
            case R.id.mine_message:
                startActivity(NewMessageRemindActivity.class,null,false);
                break;
            //反馈
            case R.id.mine_feedback:
                Toast.makeText(getActivity(), "什么？你有意见？", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_helper:
                startActivity(HelperActivity.class,null,false);
                break;
            //关于
            case R.id.mine_about:
                startActivity(AboutActivity.class,null,false);
                break;
        }
    }

}
