package cn.com.bluemoon.delivery.app.api.model.wash.pack;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/29.
 */
public class ResultWaitPackage extends ResultBase {
    private List<CabinetItem> cabinetList;
    /**
     * 列表总数
     */
    private int cabinetCount;
    /**
     * 分页时间戳(分页标志)
     */
    private long pageFlag;
    /**
     * 待打包数量
     */
    private int waitPackCount;

    public int getCabinetCount() {
        return cabinetCount;
    }

    public void setCabinetCount(int cabinetCount) {
        this.cabinetCount = cabinetCount;
    }

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }

    public int getWaitPackCount() {
        return waitPackCount;
    }

    public void setWaitPackCount(int waitPackCount) {
        this.waitPackCount = waitPackCount;
    }

    public List<CabinetItem> getCabinetList() {
        return cabinetList;
    }

    public void setCabinetList(List<CabinetItem> cabinetList) {
        this.cabinetList = cabinetList;
    }
}
