package cn.bmob.imdemo.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.ui.widget.LoadDialog;
import cn.bmob.imdemo.util.CommonUtils;
import cn.bmob.imdemo.util.Const;
import cn.bmob.imdemo.util.SPUtils;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * 登陆界面
 *
 * @author :smile
 * @project:LoginActivity
 * @date :2016-01-15-18:23
 */
public class LoginActivity extends Activity {

    private static final int STORAGE_PERMISSIONS = 4;
    @Bind(R.id.de_img_backgroud)
    ImageView deImgBackgroud;
    @Bind(R.id.de_login_name)
    EditText deLoginName;
    @Bind(R.id.de_login_password)
    EditText deLoginPassword;
    @Bind(R.id.de_login_sign)
    Button deLoginSign;
    @Bind(R.id.de_login_new_user)
    TextView deLoginNewUser;
    private String usernameStr;
    private String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getPermission();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                deImgBackgroud.startAnimation(animation);
            }
        }, 200);
        deLoginName.setText(SPUtils.getString(Const.USER_NAME, ""));
        deLoginPassword.setText(SPUtils.getString(Const.PASSWORD, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        deLoginName.setText(SPUtils.getString(Const.USER_NAME, ""));
        deLoginPassword.setText(SPUtils.getString(Const.PASSWORD, ""));
    }

    @OnClick({R.id.de_login_sign, R.id.de_login_new_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.de_login_sign:
                usernameStr = deLoginName.getText().toString().trim();
                passwordStr = deLoginPassword.getText().toString().trim();
                if (CommonUtils.isNetworkConnected(LoginActivity.this)){
                    if (validate()) {
                        LoadDialog.show(LoginActivity.this);
                        UserModel.getInstance().login(usernameStr, passwordStr, new LogInListener() {
                            @Override
                            public void done(Object o, BmobException e) {
                                LoadDialog.dismiss(LoginActivity.this);
                                if (e == null) {
                                    goToMain();
                                } else {
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    Toast.makeText(this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.de_login_new_user:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }


    /**
     * @return 输入格式校验
     */
    public boolean validate() {
        boolean valid = true;
        if (TextUtils.isEmpty(usernameStr)) {
            deLoginName.setError("请输入用户名");
            valid = false;
        } else {
            if (!CommonUtils.isNickName(usernameStr)) {
                deLoginName.setError("用户名长度为2~8之间");
                valid = false;
            } else {
                deLoginName.setError(null);
            }
        }
        if (TextUtils.isEmpty(passwordStr)) {
            deLoginPassword.setError("请输入密码");
            valid = false;
        } else {
            if (!CommonUtils.isPassword(passwordStr)) {
                deLoginPassword.setError("密码长度为6~20之间");
                valid = false;
            } else {
                deLoginPassword.setError(null);
            }
        }

        return valid;
    }


    @Subscribe
    public void onEventMainThread(FinishEvent event) {
        finish();
    }

    /**
     * 获取SD卡权限
     * 录音权限
     * 相机权限
     */
    private void getPermission() {
        String[] permissions = new String[3];
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions[1] = Manifest.permission.RECORD_AUDIO;
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissions[2] = Manifest.permission.CAMERA;
        }
        ActivityCompat.requestPermissions(LoginActivity.this, permissions, STORAGE_PERMISSIONS);
    }

    //跳转到主界面
    private void goToMain() {
        SPUtils.putString(Const.USER_NAME, usernameStr);
        SPUtils.putString(Const.PASSWORD, passwordStr);
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
