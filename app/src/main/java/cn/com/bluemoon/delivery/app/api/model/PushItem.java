package cn.com.bluemoon.delivery.app.api.model;

/**
 * Created by bm on 2016/10/21.
 */
public class PushItem {
    private String title;
    private String description;
    private ContParam contParam;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContParam getContParam() {
        return contParam;
    }

    public void setContParam(ContParam contParam) {
        this.contParam = contParam;
    }

    public class ContParam{
        private String source;
        private String view;
        private int num;
        private String msgId;
        private String url;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getView() {
            return view;
        }

        public void setView(String view) {
            this.view = view;
        }
    }

}
