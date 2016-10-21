package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#registerClothesCode(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/6/22.
 */
public class ResultRegisterClothesCode extends ResultBase {

    /**
     * 衣物编码
     */
    private String clothesCode;
    /**
     * 衣物名称
     */
    private String clothesName;
    /**
     * 衣物名称编码
     */
    private String clothesnameCode;
    /**
     * 瑕疵描述
     */
    private String flawDesc;
    /**
     * 有无瑕疵（1:有；0：无）
     */
    private int hasFlaw;
    /**
     * 有无污渍（1:有；0：无）
     */
    private int hasStain;

    /**
     * 备注
     */
    private String remark;

    /**
     * 衣物类型编号
     */
    private String typeCode;
    /**
     * 衣物类型名称
     */
    private String typeName;

    /**
     * 衣物图片列表
     */
    private List<ClothingPic> clothesImg;

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

    public String getClothesnameCode() {
        return clothesnameCode;
    }

    public void setClothesnameCode(String clothesnameCode) {
        this.clothesnameCode = clothesnameCode;
    }

    public String getFlawDesc() {
        return flawDesc;
    }

    public void setFlawDesc(String flawDesc) {
        this.flawDesc = flawDesc;
    }

    public int getHasFlaw() {
        return hasFlaw;
    }

    public void setHasFlaw(int hasFlaw) {
        this.hasFlaw = hasFlaw;
    }

    public int getHasStain() {
        return hasStain;
    }

    public void setHasStain(int hasStain) {
        this.hasStain = hasStain;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ClothingPic> getClothesImg() {
        return clothesImg;
    }

    public void setClothesImg(List<ClothingPic> clothesImg) {
        this.clothesImg = clothesImg;
    }

}
