package cn.bmob.imdemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.event.FinishEvent;
import cn.bmob.imdemo.model.BaseModel;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.ui.widget.LoadDialog;
import cn.bmob.imdemo.util.CommonUtils;
import cn.bmob.imdemo.util.Const;
import cn.bmob.imdemo.util.SPUtils;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * 注册界面
 */
public class RegisterActivity extends Activity {


    @Bind(R.id.reg_name)
    EditText regName;
    @Bind(R.id.reg_password)
    EditText regPassword;
    @Bind(R.id.reg_enter_password)
    EditText regEnterPassword;
    @Bind(R.id.btn_reg)
    AppCompatButton btnReg;
    @Bind(R.id.reg_login)
    TextView regLogin;
    @Bind(R.id.reg_img_backgroud)
    ImageView regImgBackgroud;

    private String usernameStr;
    private String passwordStr;
    private String passwordStr2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.translate_anim);
                regImgBackgroud.startAnimation(animation);
            }
        }, 200);
    }

    @OnClick({R.id.btn_reg, R.id.reg_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_reg:
                usernameStr = regName.getText().toString().trim();
                passwordStr = regPassword.getText().toString().trim();
                passwordStr2 = regEnterPassword.getText().toString().trim();
                if (CommonUtils.isNetworkConnected(RegisterActivity.this)) {
                    if (validate()) {
                        LoadDialog.show(RegisterActivity.this);
                        UserModel.getInstance().register(usernameStr, passwordStr, passwordStr2, new LogInListener() {
                            @Override
                            public void done(Object o, BmobException e) {
                                LoadDialog.dismiss(RegisterActivity.this);
                                if (e == null) {
                                    EventBus.getDefault().post(new FinishEvent());
                                    goToLogin(true);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reg_login:
                goToLogin(false);
                break;
        }
    }

    /**
     * @return 输入格式校验
     */
    public boolean validate() {
        boolean valid = true;
        if (TextUtils.isEmpty(usernameStr)) {
            regName.setError("请输入用户名");
            valid = false;
        } else {
            if (!CommonUtils.isNickName(usernameStr)) {
                regName.setError("用户名长度为2~8之间");
                valid = false;
            } else {
                regName.setError(null);
            }
        }
        if (TextUtils.isEmpty(passwordStr)) {
            regPassword.setError("请输入密码");
            valid = false;
        } else {
            if (!CommonUtils.isPassword(passwordStr)) {
                regPassword.setError("密码长度为6~20之间");
                valid = false;
            } else {
                regPassword.setError(null);
            }
        }
        if (TextUtils.isEmpty(passwordStr2)) {
            regEnterPassword.setError("请输入密码");
            valid = false;
        } else {
            if (!passwordStr2.equals(passwordStr)) {
                regEnterPassword.setError("密码不一致");
                valid = false;
            } else {
                regEnterPassword.setError(null);
            }
        }

        return valid;
    }

    private void goToLogin(boolean isSave) {
        if (isSave) {
            SPUtils.putString(Const.USER_NAME, usernameStr);
            SPUtils.putString(Const.PASSWORD, passwordStr);
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @Subscribe
    public void onEventMainThread(FinishEvent event) {
        finish();
    }

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
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onEvent(Boolean empty) {
    }
}
