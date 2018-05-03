package cn.com.bluemoon.delivery.app.api.model.ptxs60;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
*【05】支付接口，并校验库存,返回payinfo
*/
public class ResultPay extends ResultBase {/** 订单编号 */
public String orderCode;
/** 订单流水号 */
public String orderSeq;
/** 在线支付预加载信息 */
public String payInfo;

}