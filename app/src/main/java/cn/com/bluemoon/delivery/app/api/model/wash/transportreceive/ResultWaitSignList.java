package cn.com.bluemoon.delivery.app.api.model.wash.transportreceive;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 6.1获取待签收列表返回json
 * Created by lk on 2016/9/14.
 */
public class ResultWaitSignList extends ResultBase {

    /**
     * 数据列表
     */
    private List<Carriage> carriageList;
    /**
     * 分页时间戳(分页标志)
     */
    private long pageFlag;

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }

    public List<Carriage> getCarriageList() {
        return carriageList;
    }

    public void setCarriageList(List<Carriage> carriageList) {
        this.carriageList = carriageList;
    }
}
