package cn.com.bluemoon.delivery.app.api.model.storage;

/**
 * Created by allenli on 2016/3/27.
 */
public class ProductDetail {

    private String productNo;
    private String productName;
    private int realNum;
    private double realCase;
    private long realPrice;
    private String type;

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getRealNum() {
        return realNum;
    }

    public void setRealNum(int realNum) {
        this.realNum = realNum;
    }

    public double getRealCase() {
        return realCase;
    }

    public void setRealCase(double realCase) {
        this.realCase = realCase;
    }

    public long getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(long realPrice) {
        this.realPrice = realPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
