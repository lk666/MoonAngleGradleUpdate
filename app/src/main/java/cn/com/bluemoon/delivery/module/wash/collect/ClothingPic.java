package cn.com.bluemoon.delivery.module.wash.collect;

import java.io.Serializable;

/**
 * 衣物照片实体类
 * Created by lk on 2016/6/20.
 */
public class ClothingPic implements Serializable {

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
