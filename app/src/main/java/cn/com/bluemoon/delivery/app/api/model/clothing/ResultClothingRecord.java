package cn.com.bluemoon.delivery.app.api.model.clothing;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.CollectOrderDetail;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.LaundryLog;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OrderDetail;

/**
 * Created by allenli on 2016/6/28.
 */
public class ResultClothingRecord extends ResultBase {

    private String activityName;
     private int     actualCount;
    private String address;

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }

    private String city;
    private String collectBrcode;
    private String collectCode;
    private List<CollectOrderDetail> collectOrderDetail;
   private int isUrgent;

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }

    private String collectStatus;
    private String county;
    private List<LaundryLog> laundryLog;
    private List<OrderDetail> orderDetail;
    private String outerCode;
    private int payTotal;
    private String province;
    private int receivableTotal;
    private String street;

    private String customerName;

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    private String       customerPhone;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCollectBrcode() {
        return collectBrcode;
    }

    public void setCollectBrcode(String collectBrcode) {
        this.collectBrcode = collectBrcode;
    }

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

    public List<CollectOrderDetail> getCollectOrderDetail() {
        return collectOrderDetail;
    }

    public void setCollectOrderDetail(List<CollectOrderDetail> collectOrderDetail) {
        this.collectOrderDetail = collectOrderDetail;
    }

    public String getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(String collectStatus) {
        this.collectStatus = collectStatus;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public List<LaundryLog> getLaundryLog() {
        return laundryLog;
    }

    public void setLaundryLog(List<LaundryLog> laundryLog) {
        this.laundryLog = laundryLog;
    }

    public List<OrderDetail> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OrderDetail> orderDetail) {
        this.orderDetail = orderDetail;
    }

    public String getOuterCode() {
        return outerCode;
    }

    public void setOuterCode(String outerCode) {
        this.outerCode = outerCode;
    }

    public int getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(int payTotal) {
        this.payTotal = payTotal;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getReceivableTotal() {
        return receivableTotal;
    }

    public void setReceivableTotal(int receivableTotal) {
        this.receivableTotal = receivableTotal;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    private String village;
}
