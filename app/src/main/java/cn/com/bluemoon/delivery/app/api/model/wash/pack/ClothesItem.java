package cn.com.bluemoon.delivery.app.api.model.wash.pack;

/**
 * Created by bm on 2016/9/29.
 */
public class ClothesItem {
    /** 衣物编码 */
    private String clothesCode;
    /** 衣物图片地址 */
    private String clothesImgPath;

    public String getClothesCode() {
        return clothesCode;
    }

    public void setClothesCode(String clothesCode) {
        this.clothesCode = clothesCode;
    }

    public String getClothesImgPath() {
        return clothesImgPath;
    }

    public void setClothesImgPath(String clothesImgPath) {
        this.clothesImgPath = clothesImgPath;
    }
}
