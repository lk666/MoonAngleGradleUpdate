package cn.com.bluemoon.delivery.app.api.model.extract;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2017/3/2.
 */

public class ResultGetAuthStore extends ResultBase {
    private List<StoresListBean> storesList;

    public List<StoresListBean> getStoresList() {
        return storesList;
    }

    public void setStoresList(List<StoresListBean> storesList) {
        this.storesList = storesList;
    }

    public static class StoresListBean {


        private long authId;
        private String storeAddress;
        private String storeChargeCode;
        private String storeChargeName;
        private String storeChargePhone;
        private String storeCode;
        private String storeName;
        private String storeType;

        public long getAuthId() {
            return authId;
        }

        public void setAuthId(long authId) {
            this.authId = authId;
        }

        public String getStoreAddress() {
            return storeAddress;
        }

        public void setStoreAddress(String storeAddress) {
            this.storeAddress = storeAddress;
        }

        public String getStoreChargeCode() {
            return storeChargeCode;
        }

        public void setStoreChargeCode(String storeChargeCode) {
            this.storeChargeCode = storeChargeCode;
        }

        public String getStoreChargeName() {
            return storeChargeName;
        }

        public void setStoreChargeName(String storeChargeName) {
            this.storeChargeName = storeChargeName;
        }

        public String getStoreChargePhone() {
            return storeChargePhone;
        }

        public void setStoreChargePhone(String storeChargePhone) {
            this.storeChargePhone = storeChargePhone;
        }

        public String getStoreCode() {
            return storeCode;
        }

        public void setStoreCode(String storeCode) {
            this.storeCode = storeCode;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public String getStoreType() {
            return storeType;
        }

        public void setStoreType(String storeType) {
            this.storeType = storeType;
        }
    }
}
