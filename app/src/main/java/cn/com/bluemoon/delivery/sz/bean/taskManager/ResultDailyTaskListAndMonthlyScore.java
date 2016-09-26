package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by Wan.N
 * Date       2016/9/9
 * Desc      获得单日工作任务列表&月度绩效积分组合数据实体
 */
public class ResultDailyTaskListAndMonthlyScore extends ResultBase {

    private List<DailyPerformanceInfoBean> jobsdata;//工作任务数据<单日绩效数据>

    private String monthlyPer;//月绩效积分

    public List<DailyPerformanceInfoBean> getJobsdata() {
        return jobsdata;
    }

    public void setJobsdata(List<DailyPerformanceInfoBean> jobsdata) {
        this.jobsdata = jobsdata;
    }

    public String getMonthlyPer() {
        return monthlyPer;
    }

    public void setMonthlyPer(String monthlyPer) {
        this.monthlyPer = monthlyPer;
    }

    @Override
    public String toString() {
        return "ResultDailyTaskListAndMonthlyScore{" +
                "jobsdata=" + jobsdata +
                ", monthlyPer='" + monthlyPer + '\'' +
                '}';
    }
}
