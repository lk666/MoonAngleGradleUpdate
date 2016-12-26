package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import java.io.Serializable;

/**
 * 衣物登记商品
 */
public class TwoLevel implements Serializable {

    /**
     * 商品名称名称
     */
    private String washName;

    /**
     * 商品编码
     */
    private String washCode;

    /**
     * 商品图片url
     */
    private String clothImgUrl;

    public String getWashName() {
        return washName;
    }

    public void setWashName(String washName) {
        this.washName = washName;
    }

    public String getClothImgUrl() {
        return clothImgUrl;
    }

    public void setClothImgUrl(String clothImgUrl) {
        this.clothImgUrl = clothImgUrl;
    }

    public String getWashCode() {
        return washCode;
    }

    public void setWashCode(String washCode) {
        this.washCode = washCode;
    }
}
