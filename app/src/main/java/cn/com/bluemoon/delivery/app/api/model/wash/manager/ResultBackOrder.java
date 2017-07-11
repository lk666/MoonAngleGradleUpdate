package cn.com.bluemoon.delivery.app.api.model.wash.manager;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/20.
 */
public class ResultBackOrder extends ResultBase{


    private long pageFlag;
    private List<BackOrderListBean> backOrderList;

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }

    public List<BackOrderListBean> getBackOrderList() {
        return backOrderList;
    }

    public void setBackOrderList(List<BackOrderListBean> backOrderList) {
        this.backOrderList = backOrderList;
    }

    public static class BackOrderListBean implements Serializable{

        private String backOrderCode;
        private String address;
        private String city;
        private String county;
        private String customerName;
        private String province;
        private String street;
        private String village;
        private long appointBackTime;
        private int clothesNum;
        private String customerPhone;
        private boolean isUrgent;
        private boolean isRefuse;
        private long signTime;

        public String getBackOrderCode() {
            return backOrderCode;
        }

        public void setBackOrderCode(String backOrderCode) {
            this.backOrderCode = backOrderCode;
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

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
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

        public long getAppointBackTime() {
            return appointBackTime;
        }

        public void setAppointBackTime(long appointBackTime) {
            this.appointBackTime = appointBackTime;
        }

        public int getClothesNum() {
            return clothesNum;
        }

        public void setClothesNum(int clothesNum) {
            this.clothesNum = clothesNum;
        }

        public String getCustomerPhone() {
            return customerPhone;
        }

        public void setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
        }

        public boolean isIsUrgent() {
            return isUrgent;
        }

        public void setIsUrgent(boolean isUrgent) {
            this.isUrgent = isUrgent;
        }

        public boolean isIsRefuse() {
            return isRefuse;
        }

        public void setIsRefuse(boolean isRefuse) {
            this.isRefuse = isRefuse;
        }

        public long getSignTime() {
            return signTime;
        }

        public void setSignTime(long signTime) {
            this.signTime = signTime;
        }

        public String getCustomerAddress() {
            return province + city +county + street + village + address;
        }

    }
}
