package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 7.5预约收衣登记 保存（整合收衣记录）
 */
public class ResultAppointmentCollectSave extends ResultBase {

    private OrderInfoBean orderInfo;

    public OrderInfoBean getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfoBean orderInfo) {
        this.orderInfo = orderInfo;
    }

    public static class OrderInfoBean {
        /**
         * 实际收件数
         */
        private int actualCount;
        /**
         * 详细地址
         */
        private String address;
        /**
         * 市
         */
        private String city;
        /**
         * 收衣单号
         */
        private String collectCode;
        /**
         * 区/县
         */
        private String county;
        /**
         * 消费者
         */
        private String customerName;
        /**
         * 消费者联系电话
         */
        private String customerPhone;
        /**
         * 洗衣订单号
         */
        private String outerCode;
        /**
         * 省
         */
        private String province;
        /**
         * 乡镇
         */
        private String street;
        /**
         * 村
         */
        private String village;

        public OrderInfoBean() {
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getOuterCode() {
            return outerCode;
        }

        public void setOuterCode(String outerCode) {
            this.outerCode = outerCode;
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

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getActualCount() {
            return actualCount;
        }

        public void setActualCount(int actualCount) {
            this.actualCount = actualCount;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getCollectCode() {
            return collectCode;
        }

        public void setCollectCode(String collectCode) {
            this.collectCode = collectCode;
        }

        public String getVillage() {
            return village;
        }

        public void setVillage(String village) {
            this.village = village;
        }
    }
}
