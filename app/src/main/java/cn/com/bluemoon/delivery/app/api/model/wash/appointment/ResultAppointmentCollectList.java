package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 7.6预约收衣记录
 */
public class ResultAppointmentCollectList extends ResultBase {

    /**
     * 分页标识
     */
    private long timestamp;
    /**
     * 总共查询条数
     */
    private int totalCount;
    /**
     * 收衣记录
     */
    private List<CollectInfosBean> collectInfos;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<CollectInfosBean> getCollectInfos() {
        return collectInfos;
    }

    public void setCollectInfos(List<CollectInfosBean> collectInfos) {
        this.collectInfos = collectInfos;
    }

    public static class CollectInfosBean {
        /**
         * 实收数量
         */
        private int actualCount;
        /**
         * 地址详情
         */
        private String address;
        /**
         * 城市
         */
        private String city;
        /**
         * 收衣单号
         */
        private String collectCode;
        /**
         * 收衣单状态
         */
        private String collectStatus;
        /**
         * 区县
         */
        private String county;
        /**
         * 收衣单时间
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
         * 街道
         */
        private String street;
        /**
         * 乡镇
         */
        private String village;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
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
    }
}

