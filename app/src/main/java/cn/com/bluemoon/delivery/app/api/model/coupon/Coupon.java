package cn.com.bluemoon.delivery.app.api.model.coupon;

import java.io.Serializable;

/**
 * Created by bm on 2016/4/20.
 */
public class Coupon implements Serializable{
    private String couponCode;
    private String couponName;
    private String couponSName;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect;

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponSName() {
        return couponSName;
    }

    public void setCouponSName(String couponSName) {
        this.couponSName = couponSName;
    }
}
