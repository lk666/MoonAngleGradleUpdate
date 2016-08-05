package cn.com.bluemoon.delivery.app.api.model.team;

import java.io.Serializable;

import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.view.selectordialog.ISecectedItem;

/**
 * Created by bm on 2016/6/22.
 */
public class Community implements Serializable,ISecectedItem{
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

    @Override
    public String getShowText() {
        return StringUtil.getStringParams(bpCode, bpName);
    }
}
