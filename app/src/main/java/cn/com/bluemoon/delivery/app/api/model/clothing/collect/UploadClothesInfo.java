package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.alibaba.fastjson.annotation.JSONField;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#registerCreatedCollectInfo(String, String,
 * long, String, List, String, String, String, String, int, int, String, String, String, String,
 * AsyncHttpResponseHandler)} 字段值
 * <p/>
 * Created by lk on 2016/6/28.
 */
public class UploadClothesInfo implements Serializable {

    /**
     * 衣物名称
     */
    @JSONField(serialize = false)
    private String clothesName;
    /**
     * 衣物类型名称
     */
    @JSONField(serialize = false)
    private String typeName;
    /**
     * 图片
     */
    @JSONField(serialize = false)
    private String imgPath;

    /**
     * 衣物编码
     */
    private String clothesCode;

    /**
     * 衣物类型编号
     */
    private String typeCode;

    /**
     * 衣物名称编码
     */
    private String clothesnameCode;


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

    public String getClothesCode() {
        return clothesCode;
    }

    public void setClothesCode(String clothesCode) {
        this.clothesCode = clothesCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getClothesnameCode() {
        return clothesnameCode;
    }

    public void setClothesnameCode(String clothesnameCode) {
        this.clothesnameCode = clothesnameCode;
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

    public String getClothesName() {
        return clothesName;
    }

    public void setClothesName(String clothesName) {
        this.clothesName = clothesName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadClothesInfo that = (UploadClothesInfo) o;

        return clothesCode.equals(that.clothesCode);

    }

    @Override
    public int hashCode() {
        return clothesCode.hashCode();
    }
}
