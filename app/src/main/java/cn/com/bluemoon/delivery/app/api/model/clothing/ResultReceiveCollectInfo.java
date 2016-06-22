package cn.com.bluemoon.delivery.app.api.model.clothing;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;

/**
 * Created by allenli on 2016/6/22.
 */
public class ResultReceiveCollectInfo extends ResultBase {
    private List<ClothesInfo> clothesInfo;

    private int actualCount;

    private String collectBrcode;

    private String collectCode;

    private int isUrgent;

    private String receiverCode;

    public List<ClothesInfo> getClothesInfo() {
        return clothesInfo;
    }

    public void setClothesInfo(List<ClothesInfo> clothesInfo) {
        this.clothesInfo = clothesInfo;
    }

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
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

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }

    public String getReceiverCode() {
        return receiverCode;
    }

    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTransmitCode() {
        return transmitCode;
    }

    public void setTransmitCode(String transmitCode) {
        this.transmitCode = transmitCode;
    }

    public String getTransmitName() {
        return transmitName;
    }

    public void setTransmitName(String transmitName) {
        this.transmitName = transmitName;
    }

    public String getTransmitPhone() {
        return transmitPhone;
    }

    public void setTransmitPhone(String transmitPhone) {
        this.transmitPhone = transmitPhone;
    }

    private String  remark;
    private String transmitCode;
    private String transmitName;
    private String transmitPhone;

}
