package cn.com.bluemoon.delivery.app.api.model.coupon;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/4/20.
 */
public class ResultUserBase extends ResultBase{

    private UserBase userBase;

    public UserBase getUserBase() {
        return userBase;
    }

    public void setUserBase(UserBase userBase) {
        this.userBase = userBase;
    }
}
