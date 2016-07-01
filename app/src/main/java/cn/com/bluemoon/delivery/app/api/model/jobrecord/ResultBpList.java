package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by LIANGJIANGLI on 2016/6/22.
 */
public class ResultBpList extends ResultBase implements Serializable{
    private List<Item> itemList;

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public class Item implements Serializable{
        private String bpCode;
        private String bpCode1;
        private String bpName;
        private String bpName1;
        private boolean isSelect;

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
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
    }
}
