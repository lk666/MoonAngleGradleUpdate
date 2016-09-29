package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#queryCloseBoxDetail(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultCloseBoxDetail extends ResultBase {
    /**
     * 还衣单数量
     */
    private int backOrderNum;
    /**
     * 衣物箱号
     */
    private String boxCode;
    /**
     * 详细地址
     */
    private String sourceAddress;
    /**
     * 市
     */
    private String sourceCity;
    /**
     * 区
     */
    private String sourceCounty;
    /**
     * 省
     */
    private String sourceProvince;
    /**
     * 街道
     */
    private String sourceStreet;
    /**
     * 乡
     */
    private String streetVillage;
    /**
     * 封箱标签
     */
    private String tagCode;

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

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public String getSourceCounty() {
        return sourceCounty;
    }

    public void setSourceCounty(String sourceCounty) {
        this.sourceCounty = sourceCounty;
    }

    public String getSourceProvince() {
        return sourceProvince;
    }

    public void setSourceProvince(String sourceProvince) {
        this.sourceProvince = sourceProvince;
    }

    public String getSourceStreet() {
        return sourceStreet;
    }

    public void setSourceStreet(String sourceStreet) {
        this.sourceStreet = sourceStreet;
    }

    public String getStreetVillage() {
        return streetVillage;
    }

    public void setStreetVillage(String streetVillage) {
        this.streetVillage = streetVillage;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }
}
