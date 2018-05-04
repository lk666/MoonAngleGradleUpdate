package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.DispachInfo;

/**
 * 7.6预约收衣记录
 */
public class ResultAppointmentQueryList extends ResultBase {
    /**
     * 分页标示
     */
    private long timestamp;
    /**
     * 记录
     */
    private List<AppointmentListBean> appointmentList;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<AppointmentListBean> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<AppointmentListBean> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public static class AppointmentListBean implements Serializable {

        /**
         * 预约单状态:待接单
         */
        public static final String APPOINTMENT_WAIT_ORDERS = "APPOINTMENT_WAIT_ORDERS";
        /**
         * 预约单状态:已接单
         */
        public static final String APPOINTMENT_ALREADY_ORDERS = "APPOINTMENT_ALREADY_ORDERS";

        /**
         * 地址详情
         */
        private String address;
        /**
         * 预约单编号
         */
        private String appointmentCode;
        /**
         * 预约单状态
         */
        private String appointmentStatus;
        /**
         * 城市
         */
        private String city;
        /**
         * 区/县
         */
        private String county;
        /**
         * 下单时间
         */
        private long createTime;
        /**
         * 收货人
         */
        private String customerName;
        /**
         * 收货人电话
         */
        private String customerPhone;
        /**
         * 省份
         */
        private String province;
        /**
         * 备注
         */
        private String remark;
        /**
         * 乡镇/街道
         */
        private String street;
        /**
         * 社区/村
         */
        private String village;

        public DispachInfo dispachInfo;

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

        public String getAppointmentStatus() {
            return appointmentStatus;
        }

        public void setAppointmentStatus(String appointmentStatus) {
            this.appointmentStatus = appointmentStatus;
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

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
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

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
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
}

