package cn.com.bluemoon.delivery.app.api.model.coupon;

import java.io.Serializable;

/**
 * Created by bm on 2016/4/20.
 */
public class MensendLog implements Serializable{
    private int sendNum;
    private long sendTime;
    private CouponAct activity;
    private UserBase userBase;

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public CouponAct getActivity() {
        return activity;
    }

    public void setActivity(CouponAct activity) {
        this.activity = activity;
    }

    public UserBase getUserBase() {
        return userBase;
    }

    public void setUserBase(UserBase userBase) {
        this.userBase = userBase;
    }
}
