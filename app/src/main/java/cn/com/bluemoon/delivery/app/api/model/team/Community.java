package cn.com.bluemoon.delivery.app.api.model.team;

import java.io.Serializable;

/**
 * Created by bm on 2016/6/22.
 */
public class Community implements Serializable{
    private String bpCode;
    private String bpName;

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }
}
