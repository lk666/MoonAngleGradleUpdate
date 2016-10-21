package cn.com.bluemoon.delivery.app.api.model.wash.driver;

import java.util.List;

import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * Created by bm on 2016/9/20.
 */
public class ClothCenter {
    /** 详细地址 */
    private String address;
    /** 承运单明细id */
    private String carriageAddressId;
    /** 收衣点（人） */
    private String centerName;
    /** 联系电话 */
    private String receiverPhone;
    /** 市 */
    private String city;
    /** 区 */
    private String county;
    /** 省 */
    private String province;
    /** 街道 */
    private String street;
    /** 封箱标签列表 */
    private List<TagBox> tagList;
    /** 村 */
    private String village;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public List<TagBox> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagBox> tagList) {
        this.tagList = tagList;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCarriageAddressId() {
        return carriageAddressId;
    }

    public void setCarriageAddressId(String carriageAddressId) {
        this.carriageAddressId = carriageAddressId;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }
}
