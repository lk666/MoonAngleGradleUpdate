package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.LaundryLog;

/**
 * 7.6预约收衣记录
 */
public class ResultAppointmentCollectDetail extends ResultBase {

    private String address;
    private String appointmentCode;
    private String city;
    private String collectBrcode;
    private String collectCode;
    private String collectStatus;
    private String county;
    private String customerName;
    private String customerPhone;
    private int isUrgent;
    private String outerCode;
    private int payTotal;
    private String province;
    private String receivableTotal;
    private String street;
    private String village;
    private List<ClothesInfo> collectOrderDetail;
    private List<LaundryLog> laundryLog;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public void setAppointmentCode(String appointmentCode) {
        this.appointmentCode = appointmentCode;
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

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
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

    public String getReceivableTotal() {
        return receivableTotal;
    }

    public void setReceivableTotal(String receivableTotal) {
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

    public List<ClothesInfo> getCollectOrderDetail() {
        return collectOrderDetail;
    }

    public void setCollectOrderDetail(List<ClothesInfo> collectOrderDetail) {
        this.collectOrderDetail = collectOrderDetail;
    }

    public List<LaundryLog> getLaundryLog() {
        return laundryLog;
    }

    public void setLaundryLog(List<LaundryLog> laundryLog) {
        this.laundryLog = laundryLog;
    }
}

