package cn.com.bluemoon.delivery.app.api.model.wash.transportreceive;

import java.io.Serializable;

/**
 * 封箱标签
 * Created by lk on 2016/9/23.
 */
public class CarriageTag implements Serializable {
    /**
     * 还衣单数量
     */
    private int backOrderNum;
    /**
     * 封箱标签是否已签收
     */
    public boolean isSign;
    /**
     * 封箱标签列表
     */
    private String tagCode;

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
    }

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }
}
