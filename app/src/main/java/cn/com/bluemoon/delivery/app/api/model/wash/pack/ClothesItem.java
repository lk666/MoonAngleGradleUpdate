package cn.com.bluemoon.delivery.app.api.model.wash.pack;

import java.io.Serializable;

/**
 * Created by bm on 2016/9/29.
 */
public class ClothesItem implements Serializable {
    public boolean isCheck;
    /** 衣物编码 */
    private String clothesCode;
    /** 衣物图片地址 */
    private String imgPath;

    public String getClothesCode() {
        return clothesCode;
    }

    public void setClothesCode(String clothesCode) {
        this.clothesCode = clothesCode;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
