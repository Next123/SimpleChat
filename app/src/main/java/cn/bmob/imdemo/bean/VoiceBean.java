package cn.bmob.imdemo.bean;

import java.util.List;

/**
 * Created by 77009 on 2017-01-08.
 */

public class VoiceBean {

    /**
     * sn : 1
     * ls : false
     * bg : 0
     * ed : 0
     * ws : [{"bg":0,"cw":[{"sc":0,"w":"今天"}]},{"bg":0,"cw":[{"sc":0,"w":"天气"}]},{"bg":0,"cw":[{"sc":0,"w":"怎么样"}]}]
     */

    private List<WsBean> ws;

    public List<WsBean> getWs() {
        return ws;
    }

    public void setWs(List<WsBean> ws) {
        this.ws = ws;
    }

    public static class WsBean {
        /**
         * bg : 0
         * cw : [{"sc":0,"w":"今天"}]
         */

        private List<CwBean> cw;

        public List<CwBean> getCw() {
            return cw;
        }

        public void setCw(List<CwBean> cw) {
            this.cw = cw;
        }

        public static class CwBean {
            /**
             * sc : 0.0
             * w : 今天
             */

            private String w;

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }
        }
    }
}
