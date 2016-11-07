package cn.com.bluemoon.delivery.app.api.model;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.other.OrderVo;

public class ResultOrderVo extends ResultBase {
    private List<OrderVo> itemList;
    private long timestamp;

    /**
     * itemList.
     *
     * @return the itemList
     * @since JDK 1.6
     */
    public List<OrderVo> getItemList() {
        return itemList;
    }

    /**
     * itemList.
     *
     * @param itemList the itemList to set
     * @since JDK 1.6
     */
    public void setItemList(List<OrderVo> itemList) {
        this.itemList = itemList;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
  
