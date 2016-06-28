package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

/**
 * Created by allenli on 2016/6/27.
 */
public class CollectInfo {
   private int actualCount;
   private String address;
    private String  city;
    private String  collectBrcode;
    private String  collectCode;
    private String   collectStatus;
    private String   county;
    private String   isUrgent;
    private String   outerCode;
    private String   payTotal;
    private String   province;
    private String   receiveName;
    private String   receivePhone;
    private String    street;
    private String   village;

    private String activityCode;
    private String activityName;
    private String  customerName;
    private String customerPhone;
    private long  opTime;

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public long getOpTime() {
        return opTime;
    }

    public void setOpTime(long opTime) {
        this.opTime = opTime;
    }

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }

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

    public String getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        this.isUrgent = isUrgent;
    }

    public String getOuterCode() {
        return outerCode;
    }

    public void setOuterCode(String outerCode) {
        this.outerCode = outerCode;
    }

    public String getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(String payTotal) {
        this.payTotal = payTotal;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
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
}
