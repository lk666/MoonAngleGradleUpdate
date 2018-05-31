package cn.com.bluemoon.delivery.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by bm on 2017/5/9.
 */

@Entity
public class ReqBody {

    @Override
    public String toString() {
        return "ReqBody{" +
                "timestamp=" + timestamp +
                ", imei='" + imei + '\'' +
                ", appId='" + appId + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", os='" + os + '\'' +
                ", uid='" + uid + '\'' +
                ", code='" + code + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventParam='" + eventParam + '\'' +
                ", status=" + status +
                ", ip='" + ip + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", mac='" + mac + '\'' +
                ", unitBrand='" + unitBrand + '\'' +
                ", unitModel='" + unitModel + '\'' +
                '}';
    }
    public Long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public String getImei() {
        return this.imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
    public String getAppId() {
        return this.appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getAppVersion() {
        return this.appVersion;
    }
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    public String getOs() {
        return this.os;
    }
    public void setOs(String os) {
        this.os = os;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getEventType() {
        return this.eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public String getEventParam() {
        return this.eventParam;
    }
    public void setEventParam(String eventParam) {
        this.eventParam = eventParam;
    }
    public long getStatus() {
        return this.status;
    }
    public void setStatus(long status) {
        this.status = status;
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getLng() {
        return this.lng;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getLat() {
        return this.lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getUnitBrand() {
        return this.unitBrand;
    }
    public void setUnitBrand(String unitBrand) {
        this.unitBrand = unitBrand;
    }
    public String getUnitModel() {
        return this.unitModel;
    }
    public void setUnitModel(String unitModel) {
        this.unitModel = unitModel;
    }

    @Id
    private Long timestamp;
    private String imei;
    @NotNull
    private String appId;
    @NotNull
    private String appVersion;
    @NotNull
    private String os;
    private String uid;
    @NotNull
    private String code;
    @NotNull
    private String eventType;
    private String eventParam;
    private long status;
    private String ip;
    private String lng;
    private String lat;
    private String mac;
    private String unitBrand;
    private String unitModel;
    @Generated(hash = 450258984)
    public ReqBody(Long timestamp, String imei, @NotNull String appId,
            @NotNull String appVersion, @NotNull String os, String uid,
            @NotNull String code, @NotNull String eventType, String eventParam,
            long status, String ip, String lng, String lat, String mac,
            String unitBrand, String unitModel) {
        this.timestamp = timestamp;
        this.imei = imei;
        this.appId = appId;
        this.appVersion = appVersion;
        this.os = os;
        this.uid = uid;
        this.code = code;
        this.eventType = eventType;
        this.eventParam = eventParam;
        this.status = status;
        this.ip = ip;
        this.lng = lng;
        this.lat = lat;
        this.mac = mac;
        this.unitBrand = unitBrand;
        this.unitModel = unitModel;
    }
    @Generated(hash = 1323616520)
    public ReqBody() {
    }



}
