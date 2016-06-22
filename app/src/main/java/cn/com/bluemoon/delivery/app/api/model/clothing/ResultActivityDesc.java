package cn.com.bluemoon.delivery.app.api.model.clothing;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/6/22.
 */
public class ResultActivityDesc extends ResultBase {
   private String activityDesc	;
    private String activityName;

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
