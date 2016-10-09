package cn.com.bluemoon.delivery.sz.bean;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/8/11.
 */
public class SchedualCommonBean implements Serializable{

    private String title;
    private String adjust;
    private String bTime;
    private String eTime;
    private String smID;
    private String type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdjust() {
        return adjust;
    }

    public void setAdjust(String adjust) {
        this.adjust = adjust;
    }

    public String getbTime() {
        return bTime;
    }

    public void setbTime(String bTime) {
        this.bTime = bTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getSmID() {
        return smID;
    }

    public void setSmID(String smID) {
        this.smID = smID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
