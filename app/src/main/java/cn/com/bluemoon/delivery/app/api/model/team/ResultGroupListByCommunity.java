package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/23.
 */
public class ResultGroupListByCommunity extends ResultBase {

    /** 社区结果对象数组 */
    private List<Community> itemList;
    /** 小组编码 */
    private String bpCode;
    /** 小组名称 */
    private String bpName;

    public List<Community> getItemList() {
        return itemList;
    }
    public void setItemList(List<Community> itemList) {
        this.itemList = itemList;
    }
    public String getBpCode() {
        return bpCode;
    }
    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }
    public String getBpName() {
        return bpName;
    }
    public void setBpName(String bpName) {
        this.bpName = bpName;
    }


}
