package cn.com.bluemoon.delivery.app.api.model.wash.driver;

/**
 * Created by bm on 2016/9/20.
 */
public class TagBox {
    /** 还衣单数量 */
    private int backOrderNum;
    /** 箱号 */
    private String boxCode;
    /** 封箱标签号 */
    private String tagCode;

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
