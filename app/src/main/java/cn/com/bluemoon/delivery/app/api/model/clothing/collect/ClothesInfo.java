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
    /**
     * 衣物名称
     */
    private String clothesName;
    /**
     * 衣物类型名称
     */
    private String typeName;
    /**
     * 衣物类型编号
     */
    private String typeCode;
    /**
     * 图片
     */
    private String imgPath;

    /**
     * 衣物名称编码
     */
    private String clothesnameCode;

    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getClothesCode() {
        return clothesCode;
    }

    public void setClothesCode(String clothesCode) {
        this.clothesCode = clothesCode;
    }


    public String getClothesName() {
        return clothesName;
    }

    public void setClothesName(String clothesName) {
        this.clothesName = clothesName;
    }


    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getClothesnameCode() {
        return clothesnameCode;
    }

    public void setClothesnameCode(String clothesnameCode) {
        this.clothesnameCode = clothesnameCode;
    }
}
