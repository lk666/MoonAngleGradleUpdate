package cn.com.bluemoon.delivery.app.api.model.clothing;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingPic;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#getCollectInfoDetailsItem(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/6/22.
 */
public class ResultClothesDetail extends ResultBase {

    /**
     * 预约还衣时间
     */
    private long appointBackTime;

    /**
     * 衣物图片列表
     */
    private List<ClothingPic> clothesImg;
    /**
     * 衣物名称
     */
    private String clothesName;
    /**
     * 收衣单条码
     */
    private String collectBrcode;
    /**
     * 收衣单号
     */
    private String collectCode;
    /**
     * 消费者姓名
     */
    private String customerName;
    /**
     * 消费者电话
     */
    private String customerPhone;
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
     * 是否加急(1.加急， 0 不加急)
     */
    private int isUrgent;
    /**
     * 收衣人编号
     */
    private String opCode;
    /**
     * 收衣人姓名
     */
    private String opName;
    /**
     * 收衣人电话
     */
    private String opPhone;
    /**
     * 收衣时间
     */
    private long opTime;
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

    public long getAppointBackTime() {
        return appointBackTime;
    }

    public void setAppointBackTime(long appointBackTime) {
        this.appointBackTime = appointBackTime;
    }

    public String getClothesName() {
        return clothesName;
    }

    public void setClothesName(String clothesName) {
        this.clothesName = clothesName;
    }

    public String getCollectBrcode() {
        return collectBrcode;
    }

    public void setCollectBrcode(String collectBrcode) {
        this.collectBrcode = collectBrcode;
    }

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpPhone() {
        return opPhone;
    }

    public void setOpPhone(String opPhone) {
        this.opPhone = opPhone;
    }

    public long getOpTime() {
        return opTime;
    }

    public void setOpTime(long opTime) {
        this.opTime = opTime;
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
