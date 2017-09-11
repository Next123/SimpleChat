package cn.bmob.imdemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import cn.bmob.imdemo.util.Const;
import cn.bmob.imdemo.util.SPUtils;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by JK on 2017/7/24.
 * 更改昵称
 */

public class UpdateNameActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_update_name)
    EditText updateName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);
        ButterKnife.bind(this);
        setTitle("昵称更改");
        setHeadRightButtonVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText("确定");
        mHeadRightText.setOnClickListener(this);
        initView();
    }

    @Override
    protected void initView() {
        String currentName = getCurrentUser().getUsername();
        updateName.setText(currentName);
        updateName.setSelection(currentName.length());
    }

    @Override
    public void onClick(View view) {
        LoadDialog.show(mContext);
        final String updateNickName = updateName.getText().toString();
        if (!TextUtils.isEmpty(updateNickName)) {
            if (CommonUtils.isNickName(updateNickName)) {
                User newUser = new User();
                newUser.setUsername(updateNickName);
                User _user = getCurrentUser();
                newUser.update(_user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        LoadDialog.dismiss(mContext);
                        if (e == null) {
                            //更新成功
                            SPUtils.putString(Const.USER_NAME,updateNickName);
                            finish();
                        } else {
                            //更新失败
                            Toast.makeText(mContext, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                LoadDialog.dismiss(mContext);
                Toast.makeText(mContext, "请输入正确的昵称格式", Toast.LENGTH_SHORT).show();
            }
        }else {
            LoadDialog.dismiss(mContext);
            Toast.makeText(mContext, "昵称不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
