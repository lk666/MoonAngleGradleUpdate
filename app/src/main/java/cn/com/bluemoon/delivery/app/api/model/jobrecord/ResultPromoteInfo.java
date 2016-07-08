package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2016/6/22.
 */
public class ResultPromoteInfo extends ResultBase{
    private PromoteInfo promoteInfo;

    public PromoteInfo getPromoteInfo() {
        return promoteInfo;
    }

    public void setPromoteInfo(PromoteInfo promoteInfo) {
        this.promoteInfo = promoteInfo;
    }

}
