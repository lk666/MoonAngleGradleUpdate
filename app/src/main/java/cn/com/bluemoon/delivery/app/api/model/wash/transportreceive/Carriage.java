package cn.com.bluemoon.delivery.app.api.model.wash.transportreceive;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 承运单
 * Created by lk on 2016/9/23.
 */
public class Carriage implements Serializable {
    /**
     * 承运单号
     */
    private String carriageCode;
    /**
     * 司机姓名
     */
    private String carriagePeopleName;
    /**
     * 联系方式
     */
    private String carriagePhone;
    /**
     * 是否签收
     */
    public boolean isSign;
    /**
     * 封箱标签数量
     */
    private int sealTags;

    /**
     * 数据列表
     */
    private ArrayList<CarriageTag> tagList;

    public String getCarriageCode() {
        return carriageCode;
    }

    public void setCarriageCode(String carriageCode) {
        this.carriageCode = carriageCode;
    }

    public String getCarriagePeopleName() {
        return carriagePeopleName;
    }

    public void setCarriagePeopleName(String carriagePeopleName) {
        this.carriagePeopleName = carriagePeopleName;
    }

    public String getCarriagePhone() {
        return carriagePhone;
    }

    public void setCarriagePhone(String carriagePhone) {
        this.carriagePhone = carriagePhone;
    }

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
    }

    public int getSealTags() {
        return sealTags;
    }

    public void setSealTags(int sealTags) {
        this.sealTags = sealTags;
    }


    public ArrayList<CarriageTag> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<CarriageTag> tagList) {
        this.tagList = tagList;
    }
}
