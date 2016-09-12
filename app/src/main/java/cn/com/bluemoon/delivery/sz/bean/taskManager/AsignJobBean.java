package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;

/**
 * Created by jiangyuehua on 16/9/8.
 */
public class AsignJobBean implements Serializable {

	/**
	 * begin_time : 开始时间
	 * createtime : 创建时间
	 * end_time : 结束时间
	 * is_valid : 是否有效业绩 0有效，1无效
	 * produce_cont : 工作输出简介
	 * quality_score : 质量评分（10分制）
	 * review_cont : 验收评价
	 * score : 绩效积分[质量评分*有效工时/60]
	 * state : 进展状态 0未开始，1进行中，2已完成，3暂停
	 * task_cont : 工作任务内容
	 * task_idx : 任务序号
	 * usage_time : 实际用时（min）
	 * valid_min : 有效工时（min）
	 * work_day_id : 任务日计划ID
	 * work_task_id : 任务ID
	 */

	private String begin_time;
	private String createtime;
	private String end_time;
	private String is_valid;
	private String produce_cont;
	private String quality_score;
	private String review_cont;
	private String score;
	private String state;
	private String task_cont;
	private String task_idx;
	private String usage_time;
	private String valid_min;
	private String work_day_id;
	private String work_task_id;

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getIs_valid() {
		return is_valid;
	}

	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}

	public String getProduce_cont() {
		return produce_cont;
	}

	public void setProduce_cont(String produce_cont) {
		this.produce_cont = produce_cont;
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

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTask_cont() {
		return task_cont;
	}

	public void setTask_cont(String task_cont) {
		this.task_cont = task_cont;
	}

	public String getTask_idx() {
		return task_idx;
	}

	public void setTask_idx(String task_idx) {
		this.task_idx = task_idx;
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

	public String getWork_day_id() {
		return work_day_id;
	}

	public void setWork_day_id(String work_day_id) {
		this.work_day_id = work_day_id;
	}

	public String getWork_task_id() {
		return work_task_id;
	}

	public void setWork_task_id(String work_task_id) {
		this.work_task_id = work_task_id;
	}

	@Override
	public String toString() {
		return "AsignJobBean{" +
				"begin_time='" + begin_time + '\'' +
				", createtime='" + createtime + '\'' +
				", end_time='" + end_time + '\'' +
				", is_valid='" + is_valid + '\'' +
				", produce_cont='" + produce_cont + '\'' +
				", quality_score='" + quality_score + '\'' +
				", review_cont='" + review_cont + '\'' +
				", score='" + score + '\'' +
				", state='" + state + '\'' +
				", task_cont='" + task_cont + '\'' +
				", task_idx='" + task_idx + '\'' +
				", usage_time='" + usage_time + '\'' +
				", valid_min='" + valid_min + '\'' +
				", work_day_id='" + work_day_id + '\'' +
				", work_task_id='" + work_task_id + '\'' +
				'}';
	}
}
