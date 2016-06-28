package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/22.
 */
public class ResultEmpList extends ResultBase {
    private List<Emp> itemList;

    public List<Emp> getItemList() {
        return itemList;
    }

    public void setItemList(List<Emp> itemList) {
        this.itemList = itemList;
    }
}
