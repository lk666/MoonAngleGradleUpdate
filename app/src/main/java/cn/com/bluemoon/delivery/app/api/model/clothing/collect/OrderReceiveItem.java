package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import java.util.List;

/**
 * {@link ResultStartCollectInfos#orderReceive}数据类
 * Created by lk on 2016/6/21.
 */
public class OrderReceiveItem {
    /**
     * 实收数量
     */
    private int actualCount;
    /**
     * 衣物列表
     */
    List<ClothesInfo> clothesInfo;

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

}
