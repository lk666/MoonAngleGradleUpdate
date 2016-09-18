package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

/**
 * {@link ResultWaitCloseBoxList#inboxList}数据项
 * Created by lk on 2016/9/14.
 */
public class BoxItem {
    /**
     * 已入箱还衣单
     */
    private int backOrderIntoNum;
    /**
     * 规划还衣单数
     */
    private int backOrderNum;
    /**
     * 衣物箱号
     */
    private String boxCode;

    public int getBackOrderIntoNum() {
        return backOrderIntoNum;
    }

    public void setBackOrderIntoNum(int backOrderIntoNum) {
        this.backOrderIntoNum = backOrderIntoNum;
    }

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
