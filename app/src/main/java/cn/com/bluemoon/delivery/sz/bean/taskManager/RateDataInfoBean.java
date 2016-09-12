package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;

/**
 * Created by Wan.N
 * Date       2016/9/10
 * Desc       评价信息
 * ========================
 * is_valid 是否有效业绩 0有效，1无效 string
 * quality_score 质量评分（10分制） string
 * review_cont 评价内容 string
 * state 进展状态 0未开始，1进行中，2已完成，3暂停 string
 * usage_time 实际用时 string 任务的实际用时，从<获得单日工作任务列表&月度绩效积分组合接口>接口中获取
 * valid_min 有效工时（min） string
 * work_task_id 任务ID string
 */
public class RateDataInfoBean implements Serializable {
    private String is_valid;
    private String quality_score;
    private String review_cont;
    private String state;
    private String usage_time;
    private String valid_min;
    private String work_task_id;

    public RateDataInfoBean() {
    }

    public String getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(String is_valid) {
        this.is_valid = is_valid;
    }

    public String getQuality_score() {
        return quality_score;
    }

    public void setQuality_score(String quality_score) {
        this.quality_score = quality_score;
    }

    public String getReview_cont() {
        return review_cont;
    }

    public void setReview_cont(String review_cont) {
        this.review_cont = review_cont;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUsage_time() {
        return usage_time;
    }

    public void setUsage_time(String usage_time) {
        this.usage_time = usage_time;
    }

    public String getValid_min() {
        return valid_min;
    }

    public void setValid_min(String valid_min) {
        this.valid_min = valid_min;
    }

    public String getWork_task_id() {
        return work_task_id;
    }

    public void setWork_task_id(String work_task_id) {
        this.work_task_id = work_task_id;
    }

    @Override
    public String toString() {
        return "RateDataInfoBean{" +
                "is_valid='" + is_valid + '\'' +
                ", quality_score='" + quality_score + '\'' +
                ", review_cont='" + review_cont + '\'' +
                ", state='" + state + '\'' +
                ", usage_time='" + usage_time + '\'' +
                ", valid_min='" + valid_min + '\'' +
                ", work_task_id='" + work_task_id + '\'' +
                '}';
    }
}
