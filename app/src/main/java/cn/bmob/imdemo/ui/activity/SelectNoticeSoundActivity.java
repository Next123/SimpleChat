package cn.bmob.imdemo.ui.activity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.util.Const;
import cn.bmob.imdemo.util.RingtoneUtils;
import cn.bmob.imdemo.util.SPUtils;


/**
 * Created by JK on 2017/7/29.
 * <p>
 * 选择提示音
 */

public class SelectNoticeSoundActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.lv_sound_list)
    ListView lvSoundList;

    private List<String> data = null;

    private String title;
    private int pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_notice_sound);
        ButterKnife.bind(this);
        setTitle("选择提示音");
        setHeadRightButtonVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText("保存");
        mHeadRightText.setOnClickListener(this);
        initView();
    }

    @Override
    protected void initView() {
        pos = SPUtils.getInt(Const.NOTICE_SOUND, 0);
        data = RingtoneUtils.getRingtoneTitleList(RingtoneManager.TYPE_NOTIFICATION);
        final SoundAdapter adapter = new SoundAdapter();
        lvSoundList.setAdapter(adapter);
        //item监听
        lvSoundList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //取消选中
                if (pos != i) {
                    View currentView = adapter.getView(pos, null, null);
                    CheckBox currentBox = (CheckBox) currentView.findViewById(R.id.cb_sound_item);
                    if (currentBox != null) {
                        currentBox.setChecked(false);
                    }
                }
                //2.选中
                CheckBox box = (CheckBox) view.findViewById(R.id.cb_sound_item);
                box.setChecked(true);
                //3.播放
                Ringtone ringtone = RingtoneUtils.getRingtone(RingtoneManager.TYPE_NOTIFICATION, i);
                ringtone.play();
                //4.当前选择
                pos = i;
                title = data.get(i);

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        //点击确定
        SPUtils.putInt(Const.NOTICE_SOUND, pos);
        SPUtils.putString(Const.SOUND_TITLE, title);
        finish();
    }

    //适配器
    private class SoundAdapter extends BaseAdapter {

        SoundAdapter() {

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LinearLayout layout = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.item_select_sound, null);
            TextView title = (TextView) layout.findViewById(R.id.tv_sound_item_name);
            title.setText(data.get(position));
            if (pos == position) {
                CheckBox currentBox = (CheckBox) layout.findViewById(R.id.cb_sound_item);
                if (currentBox != null) {
                    currentBox.setChecked(true);
                }
            }

            return layout;
        }
    }

}
