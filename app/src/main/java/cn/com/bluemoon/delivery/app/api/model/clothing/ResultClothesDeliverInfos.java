package cn.com.bluemoon.delivery.app.api.model.clothing;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesDeliverInfo;

/**
 * Created by allenli on 2016/6/23.
 */
public class ResultClothesDeliverInfos extends ResultBase {
    private List<ClothesDeliverInfo> clothesDeliverInfo;

    public List<ClothesDeliverInfo> getClothesDeliverInfo() {
        return clothesDeliverInfo;
    }

    public void setClothesDeliverInfo(List<ClothesDeliverInfo> clothesDeliverInfo) {
        this.clothesDeliverInfo = clothesDeliverInfo;
    }
}
