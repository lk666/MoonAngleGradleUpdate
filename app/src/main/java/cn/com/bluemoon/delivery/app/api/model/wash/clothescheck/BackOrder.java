package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import java.io.Serializable;

/**
 * 还衣单
 */
public class BackOrder implements Serializable {
    /**
     * 还衣单号
     */
    private String backOrderCode;

    /**
     * 是否异常
     */
    public boolean isAbnormal;

    public boolean isAbnormal() {
        return isAbnormal;
    }

    public void setAbnormal(boolean abnormal) {
        isAbnormal = abnormal;
    }

    public String getBackOrderCode() {
        return backOrderCode;
    }

    public void setBackOrderCode(String backOrderCode) {
        this.backOrderCode = backOrderCode;
    }
}
