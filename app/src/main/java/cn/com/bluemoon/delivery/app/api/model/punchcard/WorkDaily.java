package cn.com.bluemoon.delivery.app.api.model.punchcard;

/**
 * Created by liangjiangli on 2016/4/8.
 */
public class WorkDaily {
    private int workDailyId;
    private int salesNum;
    private int punchCardId;

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public int getWorkDailyId() {
        return workDailyId;
    }

    public void setWorkDailyId(int workDailyId) {
        this.workDailyId = workDailyId;
    }

    public int getSalesNum() {
        return salesNum;
    }

    public void setSalesNum(int salesNum) {
        this.salesNum = salesNum;
    }

    public int getPunchCardId() {
        return punchCardId;
    }

    public void setPunchCardId(int punchCardId) {
        this.punchCardId = punchCardId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private int isValid;
    private String productCode;
    private String productName;
}
