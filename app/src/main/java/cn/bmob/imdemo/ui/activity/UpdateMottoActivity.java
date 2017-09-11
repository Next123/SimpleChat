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
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by JK on 2017/7/27.
 * <p>
 * 个性签名
 */

public class UpdateMottoActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_update_motto)
    EditText updateMotto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_motto);
        ButterKnife.bind(this);
        setTitle("个性签名");
        setHeadRightButtonVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText("确定");
        mHeadRightText.setOnClickListener(this);
        initView();
    }

    @Override
    protected void initView() {
        String currentMotto = getCurrentUser().getMotto();
        if (currentMotto != null) {
            updateMotto.setText(currentMotto);
            updateMotto.setSelection(currentMotto.length());
        }
    }

    @Override
    public void onClick(View view) {
        LoadDialog.show(mContext);
        String currentMotto = updateMotto.getText().toString();
        if (!TextUtils.isEmpty(currentMotto)) {
            User newUser = new User();
            newUser.setMotto(currentMotto);
            User _user = getCurrentUser();
            newUser.update(_user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    LoadDialog.dismiss(mContext);
                    if (e == null) {
                        finish();
                    } else {
                        Toast.makeText(mContext, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
