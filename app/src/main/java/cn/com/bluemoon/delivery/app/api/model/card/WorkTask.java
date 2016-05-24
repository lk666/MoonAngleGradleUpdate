package cn.com.bluemoon.delivery.app.api.model.card;

/**
 * Created by bm on 2016/3/29.
 */
public class WorkTask {
    private String workTaskType;
    private String taskCode;
    private String taskName;
    public boolean isSelected;

    public String getWorkTaskType() {
        return workTaskType;
    }

    public void setWorkTaskType(String workTaskType) {
        this.workTaskType = workTaskType;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
