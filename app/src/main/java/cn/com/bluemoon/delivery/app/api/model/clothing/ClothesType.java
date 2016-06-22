package cn.com.bluemoon.delivery.app.api.model.clothing;

/**
 * 衣物名称项
 * Created by lk on 2016/6/20.
 */
public class ClothesType implements Comparable<ClothesType> {

    /**
     * 衣物名称
     */
    private String clothesName;
    /**
     * 衣物名称编码
     */
    private String clothesnameCode;
    /**
     * 排序
     */
    private int sequenceNo;
    /**
     * 图片地址
     */
    private String imgPath;


    public String getClothesName() {
        return clothesName;
    }

    public void setClothesName(String clothesName) {
        this.clothesName = clothesName;
    }

    public String getClothesnameCode() {
        return clothesnameCode;
    }

    public void setClothesnameCode(String clothesnameCode) {
        this.clothesnameCode = clothesnameCode;
    }

//    /**
//     * 标准价格
//     */
//    private int normalPrice;
//
//    public int getNormalPrice() {
//        return normalPrice;
//    }
//
//    public void setNormalPrice(int normalPrice) {
//        this.normalPrice = normalPrice;
//    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public int compareTo(ClothesType another) {
        return sequenceNo - another.sequenceNo;
    }
}
