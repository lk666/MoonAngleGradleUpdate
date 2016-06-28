package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2016/6/22.
 */
public class ResultPromoteInfo extends ResultBase{
    private PromoteInfo promoteInfo;

    public PromoteInfo getPromoteInfo() {
        return promoteInfo;
    }

    public void setPromoteInfo(PromoteInfo promoteInfo) {
        this.promoteInfo = promoteInfo;
    }

    public class PromoteInfo{
        private String address;
        private String bpCode;
        private String bpCode1;
        private String bpName;
        private String bpName1;
        private String cashPledge;
        private int number;
        private String holidayPrice;
        private List<PeopleFlow> peopleFlow;
        private List<String> picInfo;
        private String remark;
        private String siteType;
        private String siteTypeName;
        private int useArea;
        private boolean wifi;
        private boolean wiredNetwork;
        private String workPrice;
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

        public String getCashPledge() {
            return cashPledge;
        }

        public void setCashPledge(String cashPledge) {
            this.cashPledge = cashPledge;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getHolidayPrice() {
            return holidayPrice;
        }

        public void setHolidayPrice(String holidayPrice) {
            this.holidayPrice = holidayPrice;
        }

        public List<PeopleFlow> getPeopleFlow() {
            return peopleFlow;
        }

        public void setPeopleFlow(List<PeopleFlow> peopleFlow) {
            this.peopleFlow = peopleFlow;
        }

        public List<String> getPicInfo() {
            return picInfo;
        }

        public void setPicInfo(List<String> picInfo) {
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

        public int getUseArea() {
            return useArea;
        }

        public void setUseArea(int useArea) {
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

        public String getWorkPrice() {
            return workPrice;
        }

        public void setWorkPrice(String workPrice) {
            this.workPrice = workPrice;
        }

        public boolean getRentInfo() {
            return rentInfo;
        }

        public void setRentInfo(boolean rentInfo) {
            this.rentInfo = rentInfo;
        }
    }

}
