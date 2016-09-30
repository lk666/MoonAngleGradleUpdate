package cn.com.bluemoon.delivery.app.api.model.wash.pack;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/29.
 */
public class ResultBackOrderDetail extends ResultBase{
    /** 详细地址 */
    private String address;
    /** 城市 */
    private String city;
    /** 衣物数量 */
    private int clothesNum;
    /** 收衣来源 */
    private String clothesSource;
    /** 区/县 */
    private String county;
    /** 收货人姓名 */
    private String customerName;
    /** 收货人电话 */
    private String customerPhone;
    /** 折叠数 */
    private int foldNum;
    /** 悬挂数 */
    private int hangNum;
    /** 省 */
    private String province;
    /** 乡镇/街道 */
    private String street;
    /** 社区/村 */
    private String village;

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public int getClothesNum() {
        return clothesNum;
    }
    public void setClothesNum(int clothesNum) {
        this.clothesNum = clothesNum;
    }
    public String getClothesSource() {
        return clothesSource;
    }
    public void setClothesSource(String clothesSource) {
        this.clothesSource = clothesSource;
    }
    public String getCounty() {
        return county;
    }
    public void setCounty(String county) {
        this.county = county;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerPhone() {
        return customerPhone;
    }
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    public int getFoldNum() {
        return foldNum;
    }
    public void setFoldNum(int foldNum) {
        this.foldNum = foldNum;
    }
    public int getHangNum() {
        return hangNum;
    }
    public void setHangNum(int hangNum) {
        this.hangNum = hangNum;
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
    public String getVillage() {
        return village;
    }
    public void setVillage(String village) {
        this.village = village;
    }

}
