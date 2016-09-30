package cn.com.bluemoon.delivery.app.api.model.wash.pack;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/29.
 */
public class ResultBackOrderClothes extends ResultBase {
    /** 还衣单号 */
    private String backOrderCode;
    /** 衣物列表 */
    private List<ClothesItem> clothesList;

    public String getBackOrderCode() {
        return backOrderCode;
    }

    public void setBackOrderCode(String backOrderCode) {
        this.backOrderCode = backOrderCode;
    }

    public List<ClothesItem> getClothesList() {
        return clothesList;
    }

    public void setClothesList(List<ClothesItem> clothesList) {
        this.clothesList = clothesList;
    }
}
