package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by summer on 2016/4/12.
 */
public class ResultPreDeliverOrderVo implements Serializable {

    private String orderCode;
    private long orderDate;
    private double totalCase;
    private int totalNum;
    private long totalMoney;
    private String deliStoreCode;
    private String deliStoreName;
    private int deliStoreAddrId;
    private String deliStoreAddrName;
    private String deliStoreType;
    private String deliStoreChargeName;
    private String companyName;
    private String receiveName;
    private String receiveTel;
    private String receiveAddr;
    private boolean isAllowedEditAddress;

    private List<ProductPreDeliverVo> productDetails;


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

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getDeliStoreCode() {
        return deliStoreCode;
    }

    public void setDeliStoreCode(String deliStoreCode) {
        this.deliStoreCode = deliStoreCode;
    }

    public String getDeliStoreName() {
        return deliStoreName;
    }

    public void setDeliStoreName(String deliStoreName) {
        this.deliStoreName = deliStoreName;
    }

    public int getDeliStoreAddrId() {
        return deliStoreAddrId;
    }

    public void setDeliStoreAddrId(int deliStoreAddrId) {
        this.deliStoreAddrId = deliStoreAddrId;
    }

    public String getDeliStoreAddrName() {
        return deliStoreAddrName;
    }

    public void setDeliStoreAddrName(String deliStoreAddrName) {
        this.deliStoreAddrName = deliStoreAddrName;
    }

    public String getDeliStoreType() {
        return deliStoreType;
    }

    public void setDeliStoreType(String deliStoreType) {
        this.deliStoreType = deliStoreType;
    }

    public String getDeliStoreChargeName() {
        return deliStoreChargeName;
    }

    public void setDeliStoreChargeName(String deliStoreChargeName) {
        this.deliStoreChargeName = deliStoreChargeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceiveTel() {
        return receiveTel;
    }

    public void setReceiveTel(String receiveTel) {
        this.receiveTel = receiveTel;
    }

    public String getReceiveAddr() {
        return receiveAddr;
    }

    public void setReceiveAddr(String receiveAddr) {
        this.receiveAddr = receiveAddr;
    }

    public boolean isAllowedEditAddress() {
        return isAllowedEditAddress;
    }

    public void setIsAllowedEditAddress(boolean isAllowedEditAddress) {
        this.isAllowedEditAddress = isAllowedEditAddress;
    }

    public List<ProductPreDeliverVo> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductPreDeliverVo> productDetails) {
        this.productDetails = productDetails;
    }
}
