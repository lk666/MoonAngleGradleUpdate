package cn.com.bluemoon.delivery.app.api.model.contract;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 4.进行实名认证
 */
public class ResultDoRealNameCheck extends ResultBase {

	/**
	 * 是否需要发送银行卡认证短信 true-需要发送，弹出短信认证框，false-不需要
	 */
	public boolean isSendSms;
}
