package cn.com.bluemoon.delivery.app.api.model.wash.pack;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/29.
 */
public class ResultWaitInbox extends ResultBase{
    /**
     * 分页时间戳(分页标志)
     */
    private List<BackOrder> backOrderList;
    /**
     * 分页时间戳(分页标志)
     */
    private long pageFlag;

    public long getPageFlag() {
        return pageFlag;
    }

    public List<BackOrder> getBackOrderList() {
        return backOrderList;
    }

    public void setBackOrderList(List<BackOrder> backOrderList) {
        this.backOrderList = backOrderList;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }
}
