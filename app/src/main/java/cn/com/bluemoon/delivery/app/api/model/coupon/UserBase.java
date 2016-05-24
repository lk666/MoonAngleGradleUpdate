package cn.com.bluemoon.delivery.app.api.model.coupon;

import java.io.Serializable;

/**
 * Created by bm on 2016/4/20.
 */
public class UserBase implements Serializable{

    private String mobile;
    private String nickName;
    private String registClient;
    private long registTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegistClient() {
        return registClient;
    }

    public void setRegistClient(String registClient) {
        this.registClient = registClient;
    }

    public long getRegistTime() {
        return registTime;
    }

    public void setRegistTime(long registTime) {
        this.registTime = registTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
