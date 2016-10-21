package cn.com.bluemoon.delivery.app.api.model.wash.manager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/21.
 */
public class ResultClothesDetail extends ResultBase{


    private String remark;
    private int hasFlaw;
    private int hasStain;
    private String typeName;
    private String typeCode;
    private String washResult;
    private String flawDesc;
    private int isUrgent;
    private String clothesName;

    public String getStainMakeup() {
        return stainMakeup;
    }

    public void setStainMakeup(String stainMakeup) {
        this.stainMakeup = stainMakeup;
    }

    private String stainMakeup;

    private List<ClothesImgBean> clothesImg;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getWashResult() {
        return washResult;
    }

    public void setWashResult(String washResult) {
        this.washResult = washResult;
    }

    public String getFlawDesc() {
        return flawDesc;
    }

    public void setFlawDesc(String flawDesc) {
        this.flawDesc = flawDesc;
    }

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }

    public String getClothesName() {
        return clothesName;
    }

    public void setClothesName(String clothesName) {
        this.clothesName = clothesName;
    }

    public List<ClothesImgBean> getClothesImg() {
        return clothesImg;
    }

    public void setClothesImg(List<ClothesImgBean> clothesImg) {
        this.clothesImg = clothesImg;
    }

    public static class ClothesImgBean {
        private String imgId;
        private String imgPath;

        public String getImgId() {
            return imgId;
        }

        public void setImgId(String imgId) {
            this.imgId = imgId;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }
    }
}
