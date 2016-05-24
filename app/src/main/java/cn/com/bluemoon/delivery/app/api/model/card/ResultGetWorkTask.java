package cn.com.bluemoon.delivery.app.api.model.card;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/3/30.
 */
public class ResultGetWorkTask extends ResultBase {

    private List<WorkTask> workTaskList;

    public List<WorkTask> getWorkTaskList() {
        return workTaskList;
    }

    public void setWorkTaskList(List<WorkTask> workTaskList) {
        this.workTaskList = workTaskList;
    }
}
