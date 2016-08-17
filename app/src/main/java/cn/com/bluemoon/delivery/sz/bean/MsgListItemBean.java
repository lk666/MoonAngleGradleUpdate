package cn.com.bluemoon.delivery.sz.bean;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/8/17.
 */
public class MsgListItemBean implements Serializable {

    private int bodyType;
    private String msgDec;
    private String msgId;
    private String msgNum;
    private String msgSumm;
    private String msgTitle;
    private String sendTime;

    public int getBodyType() {
        return bodyType;
    }

    public void setBodyType(int bodyType) {
        this.bodyType = bodyType;
    }

    public String getMsgDec() {
        return msgDec;
    }

    public void setMsgDec(String msgDec) {
        this.msgDec = msgDec;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(String msgNum) {
        this.msgNum = msgNum;
    }

    public String getMsgSumm() {
        return msgSumm;
    }

    public void setMsgSumm(String msgSumm) {
        this.msgSumm = msgSumm;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
