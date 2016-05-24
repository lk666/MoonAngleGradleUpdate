package cn.com.bluemoon.delivery.app.api.model.message;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by summer on 2016/5/23.
 */
public class ResultNewInfo  extends ResultBase {
    private String msgContent;
    private long pushTime ;

    public long getPushTime() {
        return pushTime;
    }

    public void setPushTime(long pushTime) {
        this.pushTime = pushTime;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }
}
