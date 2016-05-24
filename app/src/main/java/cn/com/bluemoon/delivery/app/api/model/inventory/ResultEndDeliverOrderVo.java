package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by summer on 2016/4/12.
 */
public class ResultEndDeliverOrderVo implements Serializable {

    private String orderCode;

    private double totalCase;
    private int totalNum;
    private long totalMoney;
    private int totalDiffNum;
    private long orderDate;

    private String deliStoreCode;
    private String deliStoreName;
    private String deliStoreAddrName;
    private String deliStoreType;
    private String deliStoreChargeName;

    private String companyName;
    private String receiveName;
    private String receiveTel;
    private String receiveAddr;
    private String outBackup;


    private List<ProductEndDeliverVo> productDetails;


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

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
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

    public String getOutBackup() {
        return outBackup;
    }

    public void setOutBackup(String outBackup) {
        this.outBackup = outBackup;
    }

    public List<ProductEndDeliverVo> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductEndDeliverVo> productDetails) {
        this.productDetails = productDetails;
    }

    public String getDeliStoreAddrName() {
        return deliStoreAddrName;
    }

    public void setDeliStoreAddrName(String deliStoreAddrName) {
        this.deliStoreAddrName = deliStoreAddrName;
    }
}
