package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by jiangyuehua on 16/9/10.
 * 获得单日工作任务列表&月度绩效积分组合接口
 */
public class DailyperformanceinfoResultBeanList extends ResultBase implements Serializable{



	List<DailyPerformanceInfoBean> dailyPerformanceInfoBeanList=null;

	public List<DailyPerformanceInfoBean> getDailyPerformanceInfoBeanList() {
		return dailyPerformanceInfoBeanList;
	}

	public void setDailyPerformanceInfoBeanList(List<DailyPerformanceInfoBean> dailyPerformanceInfoBeanList) {
		this.dailyPerformanceInfoBeanList = dailyPerformanceInfoBeanList;
	}

	@Override
	public String toString() {
		return "DailyperformanceinfoResultBeanList{" +
				"dailyPerformanceInfoBeanList=" + dailyPerformanceInfoBeanList +
				'}';
	}
}
