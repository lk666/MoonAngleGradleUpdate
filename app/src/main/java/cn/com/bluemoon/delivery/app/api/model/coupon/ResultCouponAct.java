package cn.com.bluemoon.delivery.app.api.model.coupon;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/4/20.
 */
public class ResultCouponAct extends ResultBase {
    private List<CouponAct> activitys;

    public List<CouponAct> getActivitys() {
        return activitys;
    }

    public void setActivitys(List<CouponAct> activitys) {
        this.activitys = activitys;
    }
}
