package cn.com.bluemoon.delivery.app.api.model.storage;

/**
 * Created by allenli on 2016/3/25.
 */
public class Stock {

    private String storeCode;
    private String storeName;

    public int getTotalCategory() {
        return totalCategory;
    }

    public void setTotalCategory(int totalCategory) {
        this.totalCategory = totalCategory;
    }

    private int totalCategory;
    private int totalNum;
    private double totalCase;
    private long totalPrice;

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public double getTotalCase() {
        return totalCase;
    }

    public void setTotalCase(double totalCase) {
        this.totalCase = totalCase;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
