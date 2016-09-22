package cn.com.bluemoon.delivery.app.api.model.wash.driver;

/**
 * Created by bm on 2016/9/20.
 */
public class TagBoxHistory {
    /** 收衣点 */
    private String centerName;
    /** 收货人 */
    private String receiver;
    /** 承运收货时间 */
    private int receiverSignTime;
    /** 封箱标签 */
    private String tagCode;

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public int getReceiverSignTime() {
        return receiverSignTime;
    }

    public void setReceiverSignTime(int receiverSignTime) {
        this.receiverSignTime = receiverSignTime;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
