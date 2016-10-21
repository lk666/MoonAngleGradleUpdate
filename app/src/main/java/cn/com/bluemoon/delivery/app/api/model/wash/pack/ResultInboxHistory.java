package cn.com.bluemoon.delivery.app.api.model.wash.pack;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/29.
 */
public class ResultInboxHistory extends ResultBase{

    private List<BackOrder> backOrderList;
    /** 筛选之后的总数 */
    private int backOrderSum;
    /** 分页时间戳(分页标志) */
    private long pageFlag;

    public List<BackOrder> getBackOrderList() {
        return backOrderList;
    }

    public void setBackOrderList(List<BackOrder> backOrderList) {
        this.backOrderList = backOrderList;
    }

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }

    public int getBackOrderSum() {
        return backOrderSum;
    }

    public void setBackOrderSum(int backOrderSum) {
        this.backOrderSum = backOrderSum;
    }
}
