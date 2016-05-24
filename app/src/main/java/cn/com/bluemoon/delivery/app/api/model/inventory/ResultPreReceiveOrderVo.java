package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * Prepare receive goods detail
 * Created by summer on 2016/4/12.
 */
public class ResultPreReceiveOrderVo implements Serializable {

    private String orderCode;
    private long outDate;
    private double totalCase;
    private int totalNum;
    private long totalMoney;

    private String reStoreCode;
    private String reStoreName;
    private int reStoreAddrId;
    private String reStoreAddrName;

    private String reStoreType;
    private String reStoreChargeName;

    private String companyName;

    private String deliveryName;
    private String deliveryTel;
    private String deliveryAddr;
    private boolean isAllowedEditAddress;

    private List<ProductPreReceiveVo> productDetails;


    public String getReStoreAddrName() {
        return reStoreAddrName;
    }

    public void setReStoreAddrName(String reStoreAddrName) {
        this.reStoreAddrName = reStoreAddrName;
    }

    public int getReStoreAddrId() {
        return reStoreAddrId;
    }

    public void setReStoreAddrId(int reStoreAddrId) {
        this.reStoreAddrId = reStoreAddrId;
    }

    public String getReStoreName() {
        return reStoreName;
    }

    public void setReStoreName(String reStoreName) {
        this.reStoreName = reStoreName;
    }

    public String getReStoreCode() {
        return reStoreCode;
    }

    public void setReStoreCode(String reStoreCode) {
        this.reStoreCode = reStoreCode;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
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

    public long getOutDate() {
        return outDate;
    }

    public void setOutDate(long outDate) {
        this.outDate = outDate;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
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

    public boolean isAllowedEditAddress() {
        return isAllowedEditAddress;
    }

    public void setIsAllowedEditAddress(boolean isAllowedEditAddress) {
        this.isAllowedEditAddress = isAllowedEditAddress;
    }

    public List<ProductPreReceiveVo> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductPreReceiveVo> productDetails) {
        this.productDetails = productDetails;
    }
}
