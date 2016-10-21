package cn.com.bluemoon.delivery.app.api.model.wash.transportreceive;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 6.3获取签收历史列表服务器返回json
 * Created by lk on 2016/9/19.
 */
public class ResultReceiveHistoryList extends ResultBase {
    /**
     * 承运单列表
     */
    private List<ReceiveCarriage> carriageList;
    /**
     * 筛选承运单总数
     */
    private int carriageSum;
    /**
     * 分页时间戳(分页标志)
     */
    private long pageFlag;

    public List<ReceiveCarriage> getCarriageList() {
        return carriageList;
    }

    public void setCarriageList(List<ReceiveCarriage> carriageList) {
        this.carriageList = carriageList;
    }

    public int getCarriageSum() {
        return carriageSum;
    }

    public void setCarriageSum(int carriageSum) {
        this.carriageSum = carriageSum;
    }

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }
}
