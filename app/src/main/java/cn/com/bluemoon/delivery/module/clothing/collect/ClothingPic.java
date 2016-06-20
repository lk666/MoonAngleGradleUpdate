package cn.com.bluemoon.delivery.module.clothing.collect;

/**
 * 衣物照片实体类
 * Created by lk on 2016/6/20.
 */
public class ClothingPic {
    public String getClothesImgId() {
        return clothesImgId;
    }

    public void setClothesImgId(String clothesImgId) {
        this.clothesImgId = clothesImgId;
    }

    public String getImgServicePath() {
        return imgServicePath;
    }

    public void setImgServicePath(String imgServicePath) {
        this.imgServicePath = imgServicePath;
    }

    public String getImgLocalPath() {
        return imgLocalPath;
    }

    public void setImgLocalPath(String imgLocalPath) {
        this.imgLocalPath = imgLocalPath;
    }

    /**
     * 上传到服务器后的图片id
     */
    private String clothesImgId;
    /**
     * 上传到服务器后的服务器图片地址
     */
    private String imgServicePath;
    /**
     * 本地图片地址
     */
    private String imgLocalPath;
}
