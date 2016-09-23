package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import java.io.Serializable;

/**
 * 封箱标签
 * Created by lk on 2016/9/21.
 */
public class CloseBoxTag implements Serializable {
    /**
     * 详细地址
     */
    private String address;
    /**
     * 还衣单数量
     */
    private int backOrderNum;
    /**
     * 衣物箱号
     */
    private String boxCode;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String county;
    /**
     * 省
     */
    private String province;
    /**
     * 街道
     */
    private String street;
    /**
     * 封箱标签
     */
    private String tagCode;
    /**
     * 村
     */
    private String village;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

}
