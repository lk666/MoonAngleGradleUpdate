package cn.com.bluemoon.delivery.app.api.model.wash.pack;

/**
 * Created by bm on 2016/9/29.
 */
public class BackOrder {

    /**
     * 还衣单号
     */
    private String backOrderCode;
    /**
     * 衣物箱号
     */
    private String boxCode;
    /**
     * 衣物数量
     */
    private int clothesNum;

    public String getBackOrderCode() {
        return backOrderCode;
    }

    public void setBackOrderCode(String backOrderCode) {
        this.backOrderCode = backOrderCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public int getClothesNum() {
        return clothesNum;
    }

    public void setClothesNum(int clothesNum) {
        this.clothesNum = clothesNum;
    }
}
