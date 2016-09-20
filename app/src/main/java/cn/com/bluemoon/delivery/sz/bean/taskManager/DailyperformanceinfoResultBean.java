package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by jiangyuehua on 16/9/10.
 * 获得单日工作任务列表&月度绩效积分组合接口
 */
public class DailyperformanceinfoResultBean extends ResultBase implements Serializable{


	/**
	 * jobsdata	工作任务数据<单日绩效数据>	object
	 monthlyPer	月绩效积分*/

	DailyPerformanceInfoBean jobsdata=null;
	String monthlyPer=null;
	/**当天以前的时间不可修改*/


	public DailyPerformanceInfoBean getJobsdata() {
		return jobsdata;
	}

	public void setJobsdata(DailyPerformanceInfoBean jobsdata) {
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
		return "DailyperformanceinfoResultBean{" +
				"jobsdata=" + jobsdata +
				", monthlyPer='" + monthlyPer + '\'' +
				'}';
	}
}
