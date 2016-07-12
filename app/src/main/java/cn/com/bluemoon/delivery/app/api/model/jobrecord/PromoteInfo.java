package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class PromoteInfo implements Serializable{

    private String address;
    private String bpCode;
    private String bpCode1;
    private String bpName;
    private String bpName1;
    private String bpCode2;

    public String getBpName2() {
        return bpName2;
    }

    public void setBpName2(String bpName2) {
        this.bpName2 = bpName2;
    }

    public String getBpCode2() {
        return bpCode2;
    }

    public void setBpCode2(String bpCode2) {
        this.bpCode2 = bpCode2;
    }

    private String bpName2;
    private double cashPledge;
    private int number;
    private double holidayPrice;
    private List<PeopleFlow> peopleFlow;
    private List<ImageInfo> picInfo;
    private String remark;
    private String siteType;
    private String siteTypeName;
    private double useArea;
    private boolean wifi;
    private boolean wiredNetwork;
    private double workPrice;
    private boolean rentInfo;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpCode1() {
        return bpCode1;
    }

    public void setBpCode1(String bpCode1) {
        this.bpCode1 = bpCode1;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpName1() {
        return bpName1;
    }

    public void setBpName1(String bpName1) {
        this.bpName1 = bpName1;
    }

    public double getCashPledge() {
        return cashPledge;
    }

    public void setCashPledge(double cashPledge) {
        this.cashPledge = cashPledge;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getHolidayPrice() {
        return holidayPrice;
    }

    public void setHolidayPrice(double holidayPrice) {
        this.holidayPrice = holidayPrice;
    }

    public List<PeopleFlow> getPeopleFlow() {
        return peopleFlow;
    }

    public void setPeopleFlow(List<PeopleFlow> peopleFlow) {
        this.peopleFlow = peopleFlow;
    }

    public List<ImageInfo> getPicInfo() {
        return picInfo;
    }

    public void setPicInfo(List<ImageInfo> picInfo) {
        this.picInfo = picInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getSiteTypeName() {
        return siteTypeName;
    }

    public void setSiteTypeName(String siteTypeName) {
        this.siteTypeName = siteTypeName;
    }

    public double getUseArea() {
        return useArea;
    }

    public void setUseArea(double useArea) {
        this.useArea = useArea;
    }

    public boolean getWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean getWiredNetwork() {
        return wiredNetwork;
    }

    public void setWiredNetwork(boolean wiredNetwork) {
        this.wiredNetwork = wiredNetwork;
    }

    public double getWorkPrice() {
        return workPrice;
    }

    public void setWorkPrice(double workPrice) {
        this.workPrice = workPrice;
    }

    public boolean getRentInfo() {
        return rentInfo;
    }

    public void setRentInfo(boolean rentInfo) {
        this.rentInfo = rentInfo;
    }
}
