package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 修改时的{@link UploadAppointClothesInfo}
 */
public class ModifyUploadAppointClothesInfo extends UploadAppointClothesInfo {

    /**
     * 原衣物编码
     */
    @JSONField(serialize = false)
    private String initClothesCode;

    public String getInitClothesCode() {
        return initClothesCode;
    }

    public void setInitClothesCode(String initClothesCode) {
        this.initClothesCode = initClothesCode;
    }
}
