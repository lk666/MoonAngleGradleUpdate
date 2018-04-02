package cn.com.bluemoon.delivery.app.api.model.contract;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * checkContent : 需实名认证的提示文案
 * empId : 员工编号
 * empName : 员工姓名
 * idCard : 身份证号码
 * isNeedReal : 是否需要实名认证  boolean	true-需要进行实名认证 false-已认证过，不需要
 * mobileNo : 手机号码
 * remarkText : 说明文字列表
 */
public class ResultCheckPersonReal extends ResultBase {

	public String checkContent;
	public String empId;
	public String empName;
	public String idCard;
	public boolean isNeedReal;
	public String mobileNo;
	public List<String> remarkText;

	//	银行卡号码
	public String bankCard;
}
