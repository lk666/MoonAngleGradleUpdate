package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.7封箱历史清点详情服务器返回json
 */
public class ResultCloseBoxHistoryDetail extends ResultBase {
    /**
     * 还衣单号列表
     */
    private List<BackOrder> backOrderList;
    /**
     * 还衣单数量
     */
    private int backOrderNum;
    /**
     * 清点时间
     */
    private long opTime;
    /**
     * 封箱条码
     */
    private String tagCode;

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public List<BackOrder> getBackOrderList() {
        return backOrderList;
    }

    public void setBackOrderList(List<BackOrder> backOrderList) {
        this.backOrderList = backOrderList;
    }

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
    }

    public long getOpTime() {
        return opTime;
    }

    public void setOpTime(long opTime) {
        this.opTime = opTime;
    }

}
