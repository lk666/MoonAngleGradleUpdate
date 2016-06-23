package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

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
     * 成功标志
     */
    private boolean isSuccess;
    /**
     * 备注
     */
    private String remark;
    /**
     * 返回码
     */
    private int responseCode;
    /**
     * 返回提示
     */
    private String responseMsg;
    /**
     * 衣物类型编号
     */
    private String typeCode;
    /**
     * 衣物类型名称
     */
    private String typeName;

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

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
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

}
