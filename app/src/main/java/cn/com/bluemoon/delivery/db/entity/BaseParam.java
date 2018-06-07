package cn.com.bluemoon.delivery.db.entity;

import android.os.Build;

import java.io.Serializable;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.NetWorkUtil;

/**
 * Created by bm on 2018/6/7.
 */

public class BaseParam implements Serializable {

    private String ip;
    private String lng;
    private String lat;
    private String mac;
    private String unit_brand;
    private String unit_model;
    private String useragent;

    public BaseParam(){
        String lng = ClientStateManager.getLongitude();
        if ("999".equals(lng) || "999.0".equals(lng)) {
            lng = "";
        }
        String lat = ClientStateManager.getLatitude();
        if ("999".equals(lat) || "999.0".equals(lat)) {
            lat = "";
        }
        this.ip = NetWorkUtil.getMacAddressFromIp(AppContext.getInstance());
        this.lng = lng;
        this.lat = lat;
        this.mac = NetWorkUtil.getMacAddressFromIp(AppContext.getInstance());
        this.unit_brand = Build.BRAND;
        this.unit_model = Build.MODEL;
        this.useragent = ClientStateManager.getUserAgent();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public String getUnit_brand() {
        return unit_brand;
    }

    public void setUnit_brand(String unit_brand) {
        this.unit_brand = unit_brand;
    }

    public String getUnit_model() {
        return unit_model;
    }

    public void setUnit_model(String unit_model) {
        this.unit_model = unit_model;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
