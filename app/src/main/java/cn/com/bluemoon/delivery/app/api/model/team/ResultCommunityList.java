package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/22.
 */
public class ResultCommunityList extends ResultBase{
    List<Community> itemList;

    public List<Community> getItemList() {
        return itemList;
    }

    public void setItemList(List<Community> itemList) {
        this.itemList = itemList;
    }
}
