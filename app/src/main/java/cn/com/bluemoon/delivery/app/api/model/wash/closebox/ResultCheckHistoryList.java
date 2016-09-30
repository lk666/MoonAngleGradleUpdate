package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.5获取清点历史列表服务器返回json
 */
public class ResultCheckHistoryList extends ResultBase {

    /**
     * 清点历史列表
     */
    private List<CheckLog> checkLogList;

    /**
     * 分页时间戳(分页标志)
     */
    private long pageFlag;


    /**
     * 筛选清点总数
     */
    private int checkLogSum;

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }

    public List<CheckLog> getCheckLogList() {
        return checkLogList;
    }

    public void setCheckLogList(List<CheckLog> checkLogList) {
        this.checkLogList = checkLogList;
    }

    public int getCheckLogSum() {
        return checkLogSum;
    }

    public void setCheckLogSum(int checkLogSum) {
        this.checkLogSum = checkLogSum;
    }

}
