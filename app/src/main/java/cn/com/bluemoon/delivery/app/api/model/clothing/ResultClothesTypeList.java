package cn.com.bluemoon.delivery.app.api.model.clothing;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 衣物名称项列表
 * Created by luokai on 2016/6/20.
 */
public class ResultClothesTypeList extends ResultBase {
    private List<ClothesType> clothesTypeConfigs;

    public List<ClothesType> getClothesTypeConfigs() {
        return clothesTypeConfigs;
    }

    public void setClothesTypeConfigs(List<ClothesType> clothesTypeConfigs) {
        this.clothesTypeConfigs = clothesTypeConfigs;
    }
}
