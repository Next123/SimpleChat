package cn.bmob.imdemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.imdemo.BmobIMApplication;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.HelperChatAdapter;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.bean.ChatBean;
import cn.bmob.imdemo.util.CommonUtils;
import cn.bmob.imdemo.util.Const;
import cn.bmob.imdemo.util.ParseJSON;

/**
 * Created by JK on 2017/10/14.
 * 在线助手
 */

public class HelperActivity extends BaseActivity implements View.OnClickListener {

    private ListView listChat;
    private CheckBox boxSwitch;
    private EditText editText;
    private Button btnSend;
    private LinearLayout layoutInput;
    private Button btnSpeak;
    private List<ChatBean> list = new ArrayList<>();
    private HelperChatAdapter adapter;
    private String mResult = null;
    //语音合成
    private SpeechSynthesizer mTts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        listChat = (ListView) findViewById(R.id.list_chat);
        boxSwitch = (CheckBox) findViewById(R.id.box_switch);
        editText = (EditText) findViewById(R.id.edit_text);
        btnSend = (Button) findViewById(R.id.btn_send);
        layoutInput = (LinearLayout) findViewById(R.id.layout_input);
        btnSpeak = (Button) findViewById(R.id.helper_btn_speak);

        btnSend.setOnClickListener(this);
        btnSpeak.setOnClickListener(this);
        setTitle(R.string.helper);

        initData();
        if (!CommonUtils.isNetworkConnected(BmobIMApplication.INSTANCE())) {
            Toast.makeText(this, "请检查网络连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(HelperActivity.this, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //初始状态  未选中
        boxSwitch.setChecked(false);
        adapter = new HelperChatAdapter(BmobIMApplication.INSTANCE(), list);
        //设置适配器
        listChat.setAdapter(adapter);
        //输入状态切换
        boxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnSpeak.setVisibility(View.GONE);
                    layoutInput.setVisibility(View.VISIBLE);
                } else {
                    btnSpeak.setVisibility(View.VISIBLE);
                    layoutInput.setVisibility(View.GONE);
                }
            }
        });
        addItem("你好，我是小优。", Const.VALUE_LEFT_TEXT);
    }

    //添加item
    private void addItem(String text, int type) {
        ChatBean data = new ChatBean();
        data.setText(text);
        data.setType(type);
        list.add(data);
        //通知listview更新
        adapter.notifyDataSetChanged();
        //滚动到底端
        listChat.setSelection(listChat.getBottom());
    }

    //获取结果
    private void addLeftItem(String text) {
        //6.发送给机器人请求返回内容
        String url = "http://op.juhe.cn/robot/index?info=" + text
                + "&key=" + Const.CHAT_KEY;
        Log.d("----------url:-----", url);
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                addItem(ParseJSON.parse(t, Const.JSON_RESULT), Const.VALUE_LEFT_TEXT);
                startSpeak(ParseJSON.parse(t, Const.JSON_RESULT));
            }
        });
    }


    //开始说话
    private void startSpeak(String text) {
        //3.开始合成
        mTts.startSpeaking(text, mSynListener);
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                //1.获取输入框的内容
                String text = editText.getText().toString();
                //2.判断是否为空
                if (!TextUtils.isEmpty(text)) {
                    //3.判断长度不能大于30
                    if (text.length() > 30) {
                        Toast.makeText(HelperActivity.this, "不能超过30个字符", Toast.LENGTH_SHORT).show();
                    } else {
                        //4.清空当前的输入框
                        editText.setText("");
                        //5.添加你输入的内容到right item
                        addItem(text, Const.VALUE_RIGHT_TEXT);
                        addLeftItem(text);
                    }
                } else {
                    Toast.makeText(HelperActivity.this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.helper_btn_speak:
                final StringBuffer mBuffer = new StringBuffer();
                //1.创建RecognizerDialog对象
                RecognizerDialog mDialog = new RecognizerDialog(this, null);
                //2.设置accent、language等参数
                mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                // 3.设置回调接口
                mDialog.setListener(new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult result, boolean isLast) {
                        mBuffer.append(ParseJSON.parse(result.getResultString(), Const.JSON_SPEAK));
                        if (isLast) {
                            addItem(mBuffer.toString(), Const.VALUE_RIGHT_TEXT);
                            addLeftItem(mBuffer.toString());
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {

                    }

                });
                //4.显示dialog，接收语音输入
                mDialog.show();
                break;
        }
    }
}
