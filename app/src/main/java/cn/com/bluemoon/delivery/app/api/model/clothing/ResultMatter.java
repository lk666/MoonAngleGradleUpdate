package cn.com.bluemoon.delivery.app.api.model.clothing;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/6/22.
 */
public class ResultMatter extends ResultBase {
        private String activityName;
         private String   matterDesc;

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getMatterDesc() {
        return matterDesc;
    }

    public void setMatterDesc(String matterDesc) {
        this.matterDesc = matterDesc;
    }
}
