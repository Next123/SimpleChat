package cn.bmob.imdemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;

/**
 * Created by JK on 2017/8/1.
 *
 * 关于
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about_simple_chat);
    }

}
