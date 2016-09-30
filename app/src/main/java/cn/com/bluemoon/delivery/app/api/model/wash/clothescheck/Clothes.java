package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import java.io.Serializable;

// TODO: lk 2016/9/30 数据分包

/**
 * 衣物
 * Created by lk on 2016/9/21.
 */
public class Clothes implements Serializable {

    /**
     * 衣物编码
     */
    private String clothesCode;
    /**
     * 图片
     */
    private String imagePath;

    public String getClothesCode() {
        return clothesCode;
    }

    public void setClothesCode(String clothesCode) {
        this.clothesCode = clothesCode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
