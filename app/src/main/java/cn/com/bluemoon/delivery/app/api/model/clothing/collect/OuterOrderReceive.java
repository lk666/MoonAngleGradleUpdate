package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import java.util.List;

/**
 * {@link ResultOuterOrderInfo#orderReceive}数据类
 * Created by lk on 2016/6/27.
 */
public class OuterOrderReceive {

    /**
     * 实收数量
     */
    private int actualCount;
    /**
     * 衣物详情列表
     */
    private List<ClothesInfo> clothesInfo;
    /**
     * 收衣单条码
     */
    private String collectBrcode;
    /**
     * 收衣单号
     */
    private String collectCode;
    /**
     * 是否加急(1.加急， 0 不加急)
     */
    private int isUrgent;

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }

    public List<ClothesInfo> getClothesInfo() {
        return clothesInfo;
    }

    public void setClothesInfo(List<ClothesInfo> clothesInfo) {
        this.clothesInfo = clothesInfo;
    }

    public String getCollectBrcode() {
        return collectBrcode;
    }

    public void setCollectBrcode(String collectBrcode) {
        this.collectBrcode = collectBrcode;
    }

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }
}
