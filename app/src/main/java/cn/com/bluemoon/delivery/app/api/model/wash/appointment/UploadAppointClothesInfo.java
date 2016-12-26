package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;

/**
 * 7.5预约收衣登记 保存（整合收衣记录）字段值
 * Created by lk on 2016/6/28.
 */
public class UploadAppointClothesInfo implements Serializable {

    /**
     * 图片
     */
    @JSONField(serialize = false)
    private String imgPath;

    /**
     * 商品名称名称
     */
    @JSONField(serialize = false)
    private String washName;
    /**
     * 一级分类名称
     */
    @JSONField(serialize = false)
    private String oneLevelName;
    /**
     * 衣物编码
     */
    private String clothesCode;

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
     * 一级分类编码（必填）
     */
    private String oneLevelCode;

    /**
     * 商品编码（必填）
     */
    private String washCode;

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

        UploadAppointClothesInfo that = (UploadAppointClothesInfo) o;

        return clothesCode.equals(that.clothesCode);
    }

    @Override
    public int hashCode() {
        return clothesCode.hashCode();
    }

    public String getOneLevelCode() {
        return oneLevelCode;
    }

    public void setOneLevelCode(String oneLevelCode) {
        this.oneLevelCode = oneLevelCode;
    }

    public String getWashCode() {
        return washCode;
    }

    public void setWashCode(String washCode) {
        this.washCode = washCode;
    }

    public String getWashName() {
        return washName;
    }

    public void setWashName(String washName) {
        this.washName = washName;
    }

    public String getOneLevelName() {
        return oneLevelName;
    }

    public void setOneLevelName(String oneLevelName) {
        this.oneLevelName = oneLevelName;
    }
}
