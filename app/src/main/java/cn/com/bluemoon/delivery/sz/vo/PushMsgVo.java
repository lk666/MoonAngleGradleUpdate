package cn.com.bluemoon.delivery.sz.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/8/15.
 */
public class PushMsgVo implements Serializable{

    private String bDate;
    private String type;
    private String targetNo;
    private String schedualId;

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetNo() {
        return targetNo;
    }

    public void setTargetNo(String targetNo) {
        this.targetNo = targetNo;
    }

    public String getSchedualId() {
        return schedualId;
    }

    public void setSchedualId(String schedualId) {
        this.schedualId = schedualId;
    }
}
