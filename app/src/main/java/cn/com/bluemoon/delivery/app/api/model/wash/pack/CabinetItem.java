package cn.com.bluemoon.delivery.app.api.model.wash.pack;

/**
 * Created by bm on 2016/9/29.
 */
public class CabinetItem {
    /**
     * 实际数量
     */
    private int actInNum;
    /**
     * 规划数量
     */
    private int capacity;
    /**
     * 分拨柜号
     */
    private String cupboardCode;
    /**
     * 区域
     */
    private String region;

    public int getActInNum() {
        return actInNum;
    }

    public void setActInNum(int actInNum) {
        this.actInNum = actInNum;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCupboardCode() {
        return cupboardCode;
    }

    public void setCupboardCode(String cupboardCode) {
        this.cupboardCode = cupboardCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
