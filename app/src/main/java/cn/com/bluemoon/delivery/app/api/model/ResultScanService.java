package cn.com.bluemoon.delivery.app.api.model;

/**
 * 天使扫一扫统一服务接口
 */
public class ResultScanService extends ResultBase {

    public static final String TYPE_HTTP = "HTTP";
    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_INTERNAL = "INTERNAL";
    /**
     * 接口返回json
     */
    private String result;
    /**
     * 类型
     */
    private String type;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Http {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Text {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * 线下培训内部跳转
     */
    public static class Internal {
        public String target;
        public Data data;
    }

    public static class Data {

        public String planCode;

        public String courseCode;

        public String userMark;

        public String userType;
    }
}