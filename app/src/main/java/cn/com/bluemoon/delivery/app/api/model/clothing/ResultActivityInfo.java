package cn.com.bluemoon.delivery.app.api.model.clothing;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ActivityInfo;

/**
 * Created by allenli on 2016/6/22.
 */
public class ResultActivityInfo extends ResultBase {
    private List<ActivityInfo> activityInfos;

    public List<ActivityInfo> getActivityInfos() {
        return activityInfos;
    }

    public void setActivityInfos(List<ActivityInfo> activityInfos) {
        this.activityInfos = activityInfos;
    }
}
