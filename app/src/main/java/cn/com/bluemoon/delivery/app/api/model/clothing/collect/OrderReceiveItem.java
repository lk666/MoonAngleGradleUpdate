package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import java.util.List;

/**
 * {@link ResultStartCollectInfos#orderReceive}数据类
 * Created by lk on 2016/6/21.
 */
public class OrderReceiveItem {
    /**
     * 收衣单号
     */
    private String collectCode;
    /**
     * 实收数量
     */
    private int collectCount;
    /**
     * 衣物列表
     */
    List<ClothesInfo> clothesInfo;

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }


    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }


    public List<ClothesInfo> getClothesInfo() {
        return clothesInfo;
    }

    public void setClothesInfo(List<ClothesInfo> clothesInfo) {
        this.clothesInfo = clothesInfo;
    }

}
