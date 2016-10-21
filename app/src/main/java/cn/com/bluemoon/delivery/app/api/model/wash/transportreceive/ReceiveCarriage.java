package cn.com.bluemoon.delivery.app.api.model.wash.transportreceive;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 已签收历史承运单
 * Created by lk on 2016/9/23.
 */
public class ReceiveCarriage implements Serializable {
    /**
     * 承运单号
     */
    private String carriageCode;

    /**
     * 封箱标签列表
     */
    private ArrayList<ReceiveCarriageTag> tagList;

    public ArrayList<ReceiveCarriageTag> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<ReceiveCarriageTag> tagList) {
        this.tagList = tagList;
    }

    public String getCarriageCode() {
        return carriageCode;
    }

    public void setCarriageCode(String carriageCode) {
        this.carriageCode = carriageCode;
    }
}
