package cn.com.bluemoon.delivery.sz.bean;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/8/16.
 */
public class MainMsgCountBean implements Serializable {

    private long msgId;
    private String msgInfo;
    private String msgTime;
    private int msgCounts;
    private int msgType;

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public int getMsgCounts() {
        return msgCounts;
    }

    public void setMsgCounts(int msgCounts) {
        this.msgCounts = msgCounts;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
