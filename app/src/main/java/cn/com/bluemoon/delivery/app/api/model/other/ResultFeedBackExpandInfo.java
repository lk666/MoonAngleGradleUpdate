package cn.com.bluemoon.delivery.app.api.model.other;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ___liangdahong on 2017/3/13.
 */

public class ResultFeedBackExpandInfo extends ResultBase {

    private String questionValue;
    private List<ItemListBean> itemList;

    public String getQuestionValue() {
        return questionValue;
    }

    public void setQuestionValue(String questionValue) {
        this.questionValue = questionValue;
    }

    public List<ItemListBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemListBean> itemList) {
        this.itemList = itemList;
    }

    public static class ItemListBean {

        private String dictId;
        private String dictName;
        public boolean isSelected;

        public String getDictId() {
            return dictId;
        }

        public void setDictId(String dictId) {
            this.dictId = dictId;
        }

        public String getDictName() {
            return dictName;
        }

        public void setDictName(String dictName) {
            this.dictName = dictName;
        }
    }
}
