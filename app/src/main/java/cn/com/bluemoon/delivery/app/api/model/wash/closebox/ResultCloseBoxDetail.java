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
     * 衣物总数量
     */
    private int clothesNum;
    /**
     * 收衣来源
     */
    private String clothesSource;
    /**
     * 封箱人
     */
    private String opName;
    /**
     * 封箱时间
     */
    private long opTime;
    /**
     * 还衣方式
     */
    private String outWays;
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
     * 衣物箱号
     */
    private String boxCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public int getBackOrderNum() {
        return backOrderNum;
    }

    public void setBackOrderNum(int backOrderNum) {
        this.backOrderNum = backOrderNum;
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

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public long getOpTime() {
        return opTime;
    }

    public void setOpTime(long opTime) {
        this.opTime = opTime;
    }

    public String getOutWays() {
        return outWays;
    }

    public void setOutWays(String outWays) {
        this.outWays = outWays;
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
}
