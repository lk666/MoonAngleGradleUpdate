package cn.com.bluemoon.delivery.app.api.model.inventory;

/**
 * Created by allenli on 2016/3/23.
 */
public class OrderVo {
    private String orderCode;
    private long orderDate;
    private double totalCase;
    private int totalNum;
    private long totalAmountRmb;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalCase() {
        return totalCase;
    }

    public void setTotalCase(double totalCase) {
        this.totalCase = totalCase;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public long getTotalAmountRmb() {
        return totalAmountRmb;
    }

    public void setTotalAmountRmb(long totalAmountRmb) {
        this.totalAmountRmb = totalAmountRmb;
    }
}
