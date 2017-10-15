package cn.bmob.imdemo.bean;

/**
 * Created by JK on 2017/5/3.
 * 聊天内容 实体类
 */

public class ChatBean {
    //type
    private int type;
    //文本
    private String text;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
