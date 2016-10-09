package cn.com.bluemoon.delivery.sz.bean.MeetingerChooseBean;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by jiangyuehua on 16/7/13.
 */
public class ResultUserInfoDetails extends ResultBase{

	private List<UserInfoDetailsBean> data;

	public List<UserInfoDetailsBean> getData() {
		return data;
	}

	public void setData(List<UserInfoDetailsBean> data) {
		this.data = data;
	}
}
