package cn.com.bluemoon.delivery.app.api.model.extract;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2017/3/2.
 */

public class ResultGetAllStores extends ResultBase {
    private List<AllStoresListBean> allStoresList;

    public List<AllStoresListBean> getAllStoresList() {
        return allStoresList;
    }

    public void setAllStoresList(List<AllStoresListBean> allStoresList) {
        this.allStoresList = allStoresList;
    }

    public static class AllStoresListBean {

        private int personNum;
        private String storeAddress;
        private String storeCode;
        private String storeName;
        private String storeType;

        public int getPersonNum() {
            return personNum;
        }

        public void setPersonNum(int personNum) {
            this.personNum = personNum;
        }

        public String getStoreAddress() {
            return storeAddress;
        }

        public void setStoreAddress(String storeAddress) {
            this.storeAddress = storeAddress;
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
