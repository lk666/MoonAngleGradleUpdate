package cn.com.bluemoon.delivery.app.api.model.punchcard;

/**
 * Created by liangjiangli on 2016/4/5.
 */
public class Product implements Comparable{
    private String productCode;
    private String productName;
    private int salesNum;

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

    public int getSalesNum() {
        return salesNum;
    }

    public void setSalesNum(int salesNum) {
        this.salesNum = salesNum;
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}
