package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wan.N
 * Date       2016/9/9
 * Desc       单日绩效数据
 */
public class DailyPerformanceInfoBean implements Serializable {
    /**
     * asignJobs          工作任务列表<员工工作任务详情> array<object>
     * createtime         创建时间 string
     * day_score          日绩效积分 string
     * day_valid_min      日实际有效工时（min） string
     * model              模块 string
     * reviewer           评价人 <个人信息结构> object
     * time_utilization   日时间有效利用率（百分比） string
     * updatetime         更新时间 string
     * user               <个人信息结构> object
     * work_date          工作日期 string
     * work_day_id        任务日计划ID
     */

    private List<AsignJobBean> asignJobs;
    private String createtime;
    private String day_score;
    private String day_valid_min;
    private String model;
    private UserInfoBean reviewer;
    private String time_utilization;
    private String updatetime;
    private UserInfoBean user;
    private String work_date;
    private String work_day_id;

    public DailyPerformanceInfoBean() {
    }

    public List<AsignJobBean> getAsignJobs() {
        return asignJobs;
    }

    public void setAsignJobs(List<AsignJobBean> asignJobs) {
        this.asignJobs = asignJobs;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getDay_score() {
        return day_score;
    }

    public void setDay_score(String day_score) {
        this.day_score = day_score;
    }

    public String getDay_valid_min() {
        return day_valid_min;
    }

    public void setDay_valid_min(String day_valid_min) {
        this.day_valid_min = day_valid_min;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public UserInfoBean getReviewer() {
        return reviewer;
    }

    public void setReviewer(UserInfoBean reviewer) {
        this.reviewer = reviewer;
    }

    public String getTime_utilization() {
        return time_utilization;
    }

    public void setTime_utilization(String time_utilization) {
        this.time_utilization = time_utilization;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public UserInfoBean getUser() {
        return user;
    }

    public void setUser(UserInfoBean user) {
        this.user = user;
    }

    public String getWork_date() {
        return work_date;
    }

    public void setWork_date(String work_date) {
        this.work_date = work_date;
    }

    public String getWork_day_id() {
        return work_day_id;
    }

    public void setWork_day_id(String work_day_id) {
        this.work_day_id = work_day_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DailyPerformanceInfoBean that = (DailyPerformanceInfoBean) o;

        return work_day_id != null ? work_day_id.equals(that.work_day_id) : that.work_day_id == null;

    }

    @Override
    public int hashCode() {
        return work_day_id != null ? work_day_id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DailyPerformanceInfoBean{" +
                "asignJobs=" + asignJobs +
                ", createtime='" + createtime + '\'' +
                ", day_score='" + day_score + '\'' +
                ", day_valid_min='" + day_valid_min + '\'' +
                ", model='" + model + '\'' +
                ", reviewer=" + reviewer +
                ", time_utilization='" + time_utilization + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", user=" + user +
                ", work_date='" + work_date + '\'' +
                ", work_day_id='" + work_day_id + '\'' +
                '}';
    }
}

