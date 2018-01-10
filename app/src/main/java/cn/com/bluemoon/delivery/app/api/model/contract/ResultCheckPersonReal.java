package cn.com.bluemoon.delivery.app.api.model.contract;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultCheckPersonReal extends ResultBase {

	/**
	 * checkContent : 需实名认证的提示文案
	 * empId : 员工编号
	 * empName : 员工姓名
	 * idCard : 身份证号码
	 * isNeedReal : 是否需要实名认证
	 * mobileNo : 手机号码
	 * remarkText : 说明文字列表
	 */
	public String checkContent;
	public String empId;
	public String empName;
	public String idCard;
	public boolean isNeedReal;
	public String mobileNo;
	public List<String> remarkText;
}
