package cn.com.bluemoon.delivery.app.api.model.wash.driver;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/20.
 */
public class ResultCarriageHistory extends ResultBase {
    /** 承运单列表 */
    private List<DriverCarriage> carriageList;
    /** 筛选承运单总数 */
    private int carriageSum;
    /** 分页时间戳(分页标志) */
    private long pageFlag;

    public List<DriverCarriage> getCarriageList() {
        return carriageList;
    }

    public void setCarriageList(List<DriverCarriage> carriageList) {
        this.carriageList = carriageList;
    }

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }

    public int getCarriageSum() {
        return carriageSum;
    }

    public void setCarriageSum(int carriageSum) {
        this.carriageSum = carriageSum;
    }
}
