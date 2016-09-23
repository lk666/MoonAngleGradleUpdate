package cn.com.bluemoon.delivery.app.api.model.wash.driver;

import java.io.Serializable;

/**
 * Created by bm on 2016/9/20.
 */
public class DriverCarriage implements Serializable{
    /** 应收箱数 */
    private int boxNum;
    /** 承运单号 */
    private String carriageCode;
    /** 洗衣中心 */
    private String centerName;
    /** 实收箱数 */
    private int actualNum;
    /** 承运收货时间 */
    private int receiverSignTime;
    public int getBoxNum() {
        return boxNum;
    }
    public void setBoxNum(int boxNum) {
        this.boxNum = boxNum;
    }
    public String getCarriageCode() {
        return carriageCode;
    }
    public void setCarriageCode(String carriageCode) {
        this.carriageCode = carriageCode;
    }
    public String getCenterName() {
        return centerName;
    }
    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public int getActualNum() {
        return actualNum;
    }

    public void setActualNum(int actualNum) {
        this.actualNum = actualNum;
    }

    public int getReceiverSignTime() {
        return receiverSignTime;
    }

    public void setReceiverSignTime(int receiverSignTime) {
        this.receiverSignTime = receiverSignTime;
    }
}
