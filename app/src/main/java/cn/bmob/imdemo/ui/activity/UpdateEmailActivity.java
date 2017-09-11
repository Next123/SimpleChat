package cn.bmob.imdemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.widget.LoadDialog;
import cn.bmob.imdemo.util.CommonUtils;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by JK on 2017/7/27.
 * 更新邮箱
 */

public class UpdateEmailActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_update_email)
    EditText updateEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        ButterKnife.bind(this);
        setTitle("邮箱更改");
        setHeadRightButtonVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText("确定");
        mHeadRightText.setOnClickListener(this);
        initView();
    }

    @Override
    protected void initView() {
        String currentEmail = getCurrentUser().getEmail();
        if (currentEmail != null) {
            updateEmail.setText(currentEmail);
            updateEmail.setSelection(currentEmail.length());
        }

    }

    @Override
    public void onClick(View view) {
        LoadDialog.show(mContext);
        String email = updateEmail.getText().toString();
        if (CommonUtils.isEmail(email)) {
            User user = getCurrentUser();
            user.setEmail(email);
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    LoadDialog.dismiss(mContext);
                    if (e == null) {
                        //更新成功
                        finish();
                    } else {
                        //更新失败
                        Toast.makeText(mContext, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            LoadDialog.dismiss(mContext);
            Toast.makeText(mContext, "请输入正确的邮箱格式", Toast.LENGTH_SHORT).show();
        }
    }
}
