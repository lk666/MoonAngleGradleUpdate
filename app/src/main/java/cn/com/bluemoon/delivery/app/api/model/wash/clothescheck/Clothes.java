package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

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

    /**
     * 是否已被扫描
     */
    @JSONField(serialize = false)
    private boolean isScaned = false;

    public boolean isScaned() {
        return isScaned;
    }

    public void setScaned(boolean scaned) {
        isScaned = scaned;
    }

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
