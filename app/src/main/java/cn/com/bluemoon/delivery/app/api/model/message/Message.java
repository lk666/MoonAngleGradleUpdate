package cn.com.bluemoon.delivery.app.api.model.message;

/**
 * Created by allenli on 2016/5/20.
 */
public class Message {
    private String msgId;
    private String msgContent;
    private long pushTime;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public long getPushTime() {
        return pushTime;
    }

    public void setPushTime(long pushTime) {
        this.pushTime = pushTime;
    }
}
