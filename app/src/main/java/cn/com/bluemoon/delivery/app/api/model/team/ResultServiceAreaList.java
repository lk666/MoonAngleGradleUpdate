package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/23.
 */
public class ResultServiceAreaList extends ResultBase {

    private List<ServiceArea> itemList;
    private int total;

    public List<ServiceArea> getItemList() {
        return itemList;
    }

    public void setItemList(List<ServiceArea> itemList) {
        this.itemList = itemList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
