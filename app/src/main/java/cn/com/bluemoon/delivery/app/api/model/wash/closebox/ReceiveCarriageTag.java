package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import java.io.Serializable;

/**
 * 封箱标签
 * Created by lk on 2016/9/23.
 */
public class ReceiveCarriageTag implements Serializable {

    /**
     * 还衣单数量
     */
    private int backOrderNum;
    /**
     * 收货人
     */
    private String receiver;
    /**
     * 签收时间(收货时间)
     */
    private long receiverSignTime;
    /**
     * 封箱标签号
     */
    private String tagCode;

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getReceiverSignTime() {
        return receiverSignTime;
    }

    public void setReceiverSignTime(long receiverSignTime) {
        this.receiverSignTime = receiverSignTime;
    }
}
