package cn.bmob.imdemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.widget.LoadDialog;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by JK on 2017/7/28.
 * <p>
 * 密码修改
 */

public class UpdatePasswordActivity extends BaseActivity {

    @Bind(R.id.old_password)
    EditText oldPassword;
    @Bind(R.id.new_password)
    EditText newPassword;
    @Bind(R.id.new_password2)
    EditText newPassword2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pswd);
        ButterKnife.bind(this);
        setTitle("密码修改");
    }

    @OnClick(R.id.update_pswd_confirm)
    public void onViewClicked() {
        String old = oldPassword.getText().toString().trim();
        String new1 = newPassword.getText().toString().trim();
        String new2 = newPassword2.getText().toString().trim();
        if (TextUtils.isEmpty(old)) {
            Toast.makeText(mContext, R.string.original_password, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(new1)) {
            Toast.makeText(mContext, R.string.new_password_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(new2)) {
            Toast.makeText(mContext, R.string.confirm_password_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (new1.length() < 6 || new1.length() > 16) {
            Toast.makeText(mContext, R.string.passwords_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!new1.equals(new2)) {
            Toast.makeText(mContext, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        if (new1.equals(old)) {
            Toast.makeText(mContext, R.string.new_and_old_password, Toast.LENGTH_SHORT).show();
            return;
        }

        LoadDialog.show(mContext);

        User.updateCurrentUserPassword(old, new1, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                LoadDialog.dismiss(mContext);
                if (e == null) {
                    Toast.makeText(mContext, R.string.update_password_succeed, Toast.LENGTH_SHORT).show();
                    startActivity(LoginActivity.class, null, true);
                } else {
                    Toast.makeText(mContext, R.string.update_password_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
