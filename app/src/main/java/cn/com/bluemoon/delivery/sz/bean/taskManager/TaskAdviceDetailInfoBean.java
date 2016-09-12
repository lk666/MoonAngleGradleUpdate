package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wan.N
 * Date       2016/9/9
 * Desc      单条建议详情实体
 */
public class TaskAdviceDetailInfoBean implements Serializable {
    private List<AsignJobBean> asignJobs; //工作任务列表[<员工工作任务详情>]
    private String suggestContent; //建议内容

    public TaskAdviceDetailInfoBean() {
    }

    public List<AsignJobBean> getAsignJobs() {
        return asignJobs;
    }

    public void setAsignJobs(List<AsignJobBean> asignJobs) {
        this.asignJobs = asignJobs;
    }

    public String getSuggestContent() {
        return suggestContent;
    }

    public void setSuggestContent(String suggestContent) {
        this.suggestContent = suggestContent;
    }

    @Override
    public String toString() {
        return "TaskAdviceDetailInfoBean{" +
                "asignJobs=" + asignJobs +
                ", suggestContent='" + suggestContent + '\'' +
                '}';
    }
}
