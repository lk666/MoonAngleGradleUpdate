package cn.com.bluemoon.delivery.app.api.model.wash.driver;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/20.
 */
public class ResultSendList extends ResultBase {
    /** 收衣点列表 */
    private List<ClothCenter> centerList;
    /** 分页时间戳(分页标志) */
    private int pageFlag;

    public List<ClothCenter> getCenterList() {
        return centerList;
    }

    public void setCenterList(List<ClothCenter> centerList) {
        this.centerList = centerList;
    }

    public int getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(int pageFlag) {
        this.pageFlag = pageFlag;
    }
}
