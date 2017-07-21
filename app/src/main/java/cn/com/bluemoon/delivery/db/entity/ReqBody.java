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
    @Generated(hash = 341310080)
    public ReqBody(Long timestamp, String imei, @NotNull String appId,
                   @NotNull String appVersion, @NotNull String os, String uid,
                   @NotNull String code, @NotNull String eventType, String eventParam,
                   long status) {
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
    }
    @Generated(hash = 1323616520)
    public ReqBody() {
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


}
