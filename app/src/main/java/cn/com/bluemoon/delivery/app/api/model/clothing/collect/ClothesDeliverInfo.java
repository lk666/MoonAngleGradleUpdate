package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

/**
 * Created by allenli on 2016/6/23.
 */
public class ClothesDeliverInfo {
    private String receiverCode;
    private String receiverName;
    private String  receiverPhone;
    private String  refusalReason;
    private long transmitTime;

    public String getReceiverCode() {
        return receiverCode;
    }

    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    public long getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(long transmitTime) {
        this.transmitTime = transmitTime;
    }
}
