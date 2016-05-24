package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * Prepare receive goods detail
 * Created by summer on 2016/4/12.
 */
public class ResultEndReceiveOrderVo implements Serializable {

    private String orderCode;

    private double totalCase;
    private int totalNum;
    private long totalMoney;
    private int totalDiffNum;
    private long reDate;

    private String reStoreCode;
    private String reStoreName;
    private String reStoreAddrName;
    private String reStoreType;
    private String reStoreChargeName;

    private String companyName;

    private String deliveryName;
    private String deliveryTel;
    private String deliveryAddr;

    private List<ProductEndReceiveVo> productDetails;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
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

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getTotalDiffNum() {
        return totalDiffNum;
    }

    public void setTotalDiffNum(int totalDiffNum) {
        this.totalDiffNum = totalDiffNum;
    }

    public long getReDate() {
        return reDate;
    }

    public void setReDate(long reDate) {
        this.reDate = reDate;
    }

    public String getReStoreCode() {
        return reStoreCode;
    }

    public void setReStoreCode(String reStoreCode) {
        this.reStoreCode = reStoreCode;
    }

    public String getReStoreName() {
        return reStoreName;
    }

    public void setReStoreName(String reStoreName) {
        this.reStoreName = reStoreName;
    }

    public String getReStoreAddrName() {
        return reStoreAddrName;
    }

    public void setReStoreAddrName(String reStoreAddrName) {
        this.reStoreAddrName = reStoreAddrName;
    }

    public String getReStoreType() {
        return reStoreType;
    }

    public void setReStoreType(String reStoreType) {
        this.reStoreType = reStoreType;
    }

    public String getReStoreChargeName() {
        return reStoreChargeName;
    }

    public void setReStoreChargeName(String reStoreChargeName) {
        this.reStoreChargeName = reStoreChargeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryTel() {
        return deliveryTel;
    }

    public void setDeliveryTel(String deliveryTel) {
        this.deliveryTel = deliveryTel;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public List<ProductEndReceiveVo> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductEndReceiveVo> productDetails) {
        this.productDetails = productDetails;
    }
}
