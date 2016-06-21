package cn.com.bluemoon.delivery.app.api.model.clothing.collect;


/**
 * {@link OrderReceiveItem#clothesInfo}数据类
 * Created by lk on 2016/6/21.
 */
public class ClothesInfo {
    /**
     * 衣物编码
     */
    private String clothesCode;

    public String getClothesCode() {
        return clothesCode;
    }

    public void setClothesCode(String clothesCode) {
        this.clothesCode = clothesCode;
    }

    /**
     * 衣物名称
     */
    private String clothesName;

    public String getClothesName() {
        return clothesName;
    }

    public void setClothesName(String clothesName) {
        this.clothesName = clothesName;
    }

    /**
     * 衣物类型名称
     */
    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 衣物类型编号
     */
    private String typeCode;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * 图片
     */
    private String imgPath;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
