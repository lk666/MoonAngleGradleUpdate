package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 修改时的{@link UploadClothesInfo}
 * Created by lk on 2016/6/28.
 */
public class ModifyUploadClothesInfo extends UploadClothesInfo {

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
