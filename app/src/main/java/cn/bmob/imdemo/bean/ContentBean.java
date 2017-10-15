package cn.bmob.imdemo.bean;

/**
 * Created by JK on 2017/5/3.
 * 聚合数据 实体类
 */

public class ContentBean {

    /**
     * reason : 成功的返回
     * result : {"code":100000,"text":"你好啊，希望你今天过的快乐"}
     * error_code : 0
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * code : 100000
         * text : 你好啊，希望你今天过的快乐
         */

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
