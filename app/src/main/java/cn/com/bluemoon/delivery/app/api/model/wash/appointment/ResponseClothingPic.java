package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 衣物照片实体类，继承base的
 * Created by lk on 2016/6/20.
 */
public class ResponseClothingPic extends ResultBase {

    /**
     * 上传到服务器后的图片id
     */
    private String imgId;
    /**
     * 上传到服务器后的服务器图片地址
     */
    private String imgPath;

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
