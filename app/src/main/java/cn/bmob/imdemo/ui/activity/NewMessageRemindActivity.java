package cn.bmob.imdemo.ui.activity;

import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.ui.widget.SwitchButton;
import cn.bmob.imdemo.util.Const;
import cn.bmob.imdemo.util.RingtoneUtils;
import cn.bmob.imdemo.util.SPUtils;

/**
 * Created by JK on 2017/7/28.
 * <p>
 * 新消息通知
 */

public class NewMessageRemindActivity extends BaseActivity {

    @Bind(R.id.sb_accept_notice)
    SwitchButton sbAcceptNotice;
    @Bind(R.id.sb_sound)
    SwitchButton sbSound;
    @Bind(R.id.tv_select_sound)
    TextView tvSelectSound;
    @Bind(R.id.rl_select_sound)
    RelativeLayout rlSelectSound;
    @Bind(R.id.sb_shake)
    SwitchButton sbShake;
    @Bind(R.id.ll_message_notice)
    LinearLayout llMessageNotice;

    private ArrayList<String> soundTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message_remind);
        ButterKnife.bind(this);
        setTitle("新消息通知");
        initView();
    }

    @Override
    protected void initView() {
        soundTitles = RingtoneUtils.getRingtoneTitleList(RingtoneManager.TYPE_NOTIFICATION);
        boolean isNotice = SPUtils.getBoolean(Const.IS_NOTICE, true);
        boolean isSoundNotice = SPUtils.getBoolean(Const.IS_SOUND_NOTICE, true);
        boolean isShakeNotice = SPUtils.getBoolean(Const.IS_SHAKE_NOTICE, true);
        tvSelectSound.setText(soundTitles.get(SPUtils.getInt(Const.NOTICE_SOUND,0)));
        if (isNotice) {
            llMessageNotice.setVisibility(View.VISIBLE);
            sbAcceptNotice.setChecked(true);
            if (isShakeNotice) {
                sbShake.setChecked(true);
            } else {
                sbShake.setChecked(false);
            }
            if (isSoundNotice) {
                sbSound.setChecked(true);
            } else {
                sbSound.setChecked(false);
            }
        } else {
            llMessageNotice.setVisibility(View.GONE);
            sbAcceptNotice.setChecked(false);
        }

        sbAcceptNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtils.putBoolean(Const.IS_NOTICE,b);
                if (b){
                    llMessageNotice.setVisibility(View.VISIBLE);
                    SPUtils.putBoolean(Const.IS_SOUND_NOTICE, true);
                    SPUtils.putBoolean(Const.IS_SHAKE_NOTICE, true);
                }else {
                    llMessageNotice.setVisibility(View.GONE);
                    SPUtils.putBoolean(Const.IS_SOUND_NOTICE,false);
                    SPUtils.putBoolean(Const.IS_SHAKE_NOTICE,false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvSelectSound.setText(soundTitles.get(SPUtils.getInt(Const.NOTICE_SOUND,0)));
    }

    @OnClick(R.id.rl_select_sound)
    public void onViewClicked() {
        startActivity(SelectNoticeSoundActivity.class,null,false);
    }
}
