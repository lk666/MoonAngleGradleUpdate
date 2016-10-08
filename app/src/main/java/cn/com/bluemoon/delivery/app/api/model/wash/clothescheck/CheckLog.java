package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import java.io.Serializable;

/**
 * 清点历史
 */
public class CheckLog implements Serializable {
    /**
     * 封箱标签
     */
    public static final String INVENTORY_SEALED_BOX = "INVENTORY_SEALED_BOX";
    /**
     * 还衣单号
     */
    public static final String INVENTORY_BACK_ORDER = "INVENTORY_BACK_ORDER";


    /**
     * 清点历史主表id
     */
    private String checkLogId;
    /**
     * 是否异常
     */
    private boolean isAbnormal;
    /**
     * 操作时间
     */
    private long opTime;
    /**
     * 源单单号(封箱标签/还衣单号)
     */
    private String sourceCode;
    /**
     * 数量(还衣单数量/衣物数量)
     */
    private int sourceDetailNum;

    /**
     * 源单类型(封箱标签/还衣单号)
     */
    private String sourceType;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCheckLogId() {
        return checkLogId;
    }

    public void setCheckLogId(String checkLogId) {
        this.checkLogId = checkLogId;
    }

    public boolean isAbnormal() {
        return isAbnormal;
    }

    public void setAbnormal(boolean abnormal) {
        isAbnormal = abnormal;
    }

    public long getOpTime() {
        return opTime;
    }

    public void setOpTime(long opTime) {
        this.opTime = opTime;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public int getSourceDetailNum() {
        return sourceDetailNum;
    }

    public void setSourceDetailNum(int sourceDetailNum) {
        this.sourceDetailNum = sourceDetailNum;
    }
}
