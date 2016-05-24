package cn.com.bluemoon.delivery.app.api.model.coupon;


import java.io.Serializable;
import java.util.List;

/**
 * Created by bm on 2016/4/20.
 */
public class CouponAct implements Serializable {

    private String activityCode;
    private String activityName;
    private String activitySName;
    private long startTime;
    private long endTime;
    private String activityDesc;
    private List<Coupon> coupons;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getActivitySName() {
        return activitySName;
    }

    public void setActivitySName(String activitySName) {
        this.activitySName = activitySName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
}
