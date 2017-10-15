package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.bean.ChatBean;
import cn.bmob.imdemo.util.Const;

/**
 * Created by JK on 2017/10/14.
 *
 * 在线助手适配器
 */

public class HelperChatAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ChatBean> list;

    public HelperChatAdapter(Context context, List<ChatBean> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeftHolder leftHolder = null;
        RightHolder rightHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case Const.VALUE_LEFT_TEXT:
                    convertView = inflater.inflate(R.layout.helper_left_item, null);
                    leftHolder = new LeftHolder();
                    leftHolder.tv_left_text = (TextView) convertView.findViewById(R.id.tv_left_text);
                    convertView.setTag(leftHolder);
                    break;
                case Const.VALUE_RIGHT_TEXT:
                    convertView = inflater.inflate(R.layout.helper_right_item, null);
                    rightHolder = new RightHolder();
                    rightHolder.tv_right_text = (TextView) convertView.findViewById(R.id.tv_right_text);
                    convertView.setTag(rightHolder);
                    break;
            }
        } else {
            switch (type) {
                case Const.VALUE_LEFT_TEXT:
                    leftHolder = (LeftHolder) convertView.getTag();
                    break;
                case Const.VALUE_RIGHT_TEXT:
                    rightHolder = (RightHolder) convertView.getTag();
                    break;
            }
        }
        //赋值
        ChatBean data = list.get(position);
        switch (type) {
            case Const.VALUE_LEFT_TEXT:
                leftHolder.tv_left_text.setText(data.getText());
                break;
            case Const.VALUE_RIGHT_TEXT:
                rightHolder.tv_right_text.setText(data.getText());
                break;
        }
        return convertView;
    }

    //根据数据源的positiion来返回要显示的item
    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    //返回所有layout的数量
    @Override
    public int getViewTypeCount() {
        return 3;
    }

    private class LeftHolder {
        private TextView tv_left_text;
    }

    private class RightHolder {
        private TextView tv_right_text;
    }

}
