package cn.bmob.imdemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.newim.BmobIM;

/**
 * Created by JK on 2017/7/27.
 * <p>
 * 账户设置
 */

public class AccountSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        ButterKnife.bind(this);
        setTitle("账户设置");

    }


    @OnClick({R.id.ac_set_change_pswd, R.id.ac_set_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_set_change_pswd://修改密码
                startActivity(UpdatePasswordActivity.class,null,false);
                break;
            case R.id.ac_set_exit://退出登录
                UserModel.getInstance().logout();
                //断开连接
                BmobIM.getInstance().disConnect();
                startActivity(LoginActivity.class,null,true);
                break;
        }
    }
}
