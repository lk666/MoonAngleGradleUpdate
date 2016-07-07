package cn.com.bluemoon.delivery.app.api.model.clothing;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by luokai on 2016/7/7.
 */
public class ResultActivityMatters extends ResultBase {
    private String matterDesc;
    private String activityName;

    public String getMatterDesc() {
        return matterDesc;
    }

    public void setMatterDesc(String matterDesc) {
        this.matterDesc = matterDesc;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
