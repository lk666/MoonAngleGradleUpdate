package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import java.io.Serializable;

/**
 * 还衣单清单数据项
 */
public class CheckBackOrder implements Serializable {
    /**
     * 还衣单情况异常
     * 衣物清点-还衣单清点状态
     */
    public static final String EXCEPTION = "EXCEPTION";
    /**
     * 还衣单清点状态不存在
     * 衣物清点-还衣单清点状态
     */
    public static final String NONEXIST = "NONEXIST";
    /**
     * 还衣单情况正常
     */
    public static final String NORMAL = "NORMAL";

    /**
     * 还衣单号
     */
    private String backOrderCode;
    
    /**
     * 还衣单清点状态
     */
    private String checkStatus;

    /**
     * @param checkStatus 衣物清点-还衣单清点状态，取值{@link #EXCEPTION}、{@link #NONEXIST}或{@link #NORMAL}
     */
    public CheckBackOrder(String backOrderCode, String checkStatus) {
        this.backOrderCode = backOrderCode;
        this.checkStatus = checkStatus;
    }

    public String getBackOrderCode() {
        return backOrderCode;
    }

    public void setBackOrderCode(String backOrderCode) {
        this.backOrderCode = backOrderCode;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }
}
