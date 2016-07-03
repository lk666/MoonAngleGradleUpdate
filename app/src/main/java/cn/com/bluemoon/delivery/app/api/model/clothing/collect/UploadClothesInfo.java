package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.alibaba.fastjson.annotation.JSONField;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.module.clothing.collect.ClothingPic;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#registerCreatedCollectInfo(String, String,
 * long, String, List, String, String, String, String, int, int, String, String, String, String,
 * AsyncHttpResponseHandler)} 字段值
 * <p/>
 * Created by lk on 2016/6/28.
 */
public class UploadClothesInfo extends ClothesInfo {

    /**
     * 图片IDs 多个用豆号隔开3232,3234223
     */
    private String clothesImgIds;

    /**
     * 瑕疵描述
     */
    private String flawDesc;

    /**
     * 有无瑕疵（1:有；0：无）
     */
    private int hasFlaw;

    /**
     * 有无污渍（1:有；0：无）
     */
    private int hasStain;

    /**
     * 备注
     */
    private String remark;

    /**
     * 衣物图片列表
     */
    @JSONField(serialize = false)
    private List<ClothingPic> clothingPics;

    public List<ClothingPic> getClothingPics() {
        return clothingPics;
    }

    public void setClothingPics(List<ClothingPic> clothingPics) {
        this.clothingPics = clothingPics;
    }


    public String getClothesImgIds() {
        return clothesImgIds;
    }

    public void setClothesImgIds(String clothesImgIds) {
        this.clothesImgIds = clothesImgIds;
    }

    public String getFlawDesc() {
        return flawDesc;
    }

    public void setFlawDesc(String flawDesc) {
        this.flawDesc = flawDesc;
    }

    public int getHasFlaw() {
        return hasFlaw;
    }

    public void setHasFlaw(int hasFlaw) {
        this.hasFlaw = hasFlaw;
    }

    public int getHasStain() {
        return hasStain;
    }

    public void setHasStain(int hasStain) {
        this.hasStain = hasStain;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        UploadClothesInfo obj = (UploadClothesInfo) o;
        if (obj != null) {
            return obj.getClothesCode().equals(getClothesCode());
        }
        return false;
    }
}
