package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by LIANGJIANGLI on 2016/6/22.
 */
public class ResultPromoteList extends ResultBase{

    private long timestamp;
    private List<Item> itemList;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public class Item {
        private String bpCode;
        private String bpCode1;
        private String bpName;
        private String bpName1;
        private String holidayPrice;
        private String siteType;
        private String siteTypeName;
        private int useArea;
        private int workPrice;

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

        public String getHolidayPrice() {
            return holidayPrice;
        }

        public void setHolidayPrice(String holidayPrice) {
            this.holidayPrice = holidayPrice;
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

        public int getWorkPrice() {
            return workPrice;
        }

        public void setWorkPrice(int workPrice) {
            this.workPrice = workPrice;
        }
    }
}
