package cn.com.bluemoon.delivery.utils;

import cn.com.bluemoon.delivery.BuildConfig;

public class UrlString {
  
	public static String DOMAIN;
	public static String DOMAIN_WX;
	
	static {
		if (BuildConfig.RELEASE) {
			DOMAIN = "https://mallorder.bluemoon.com.cn/mallErpcrm";
			DOMAIN_WX = "http://mh.bluemoon.com.cn/wechats/activity/venuePlan";
		} else {
			DOMAIN = "https://angelapi.bluemoon.com.cn:8881/mallErpcrm";
			DOMAIN_WX = "http://webchatqyh.bluemoon.com.cn/wechats/activity/venuePlan";
		}
	}

	public final static String CHECK_VERION_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.appmanager.getLastVersion.biz.ext";
	
	public final static String LOGIN_ACTION = DOMAIN + "/cn.com.bluemoon.dispatch.app.user.userLogin.biz.ext";
	
	public final static String SEND_MSG_ACTION = DOMAIN + "/cn.com.bluemoon.dispatch.app.user.getVerifyCode.biz.ext";
	
	public final static String RESET_SSO_PASSWORD_ACTION = DOMAIN + "/cn.com.bluemoon.dispatch.app.user.resetPassword.biz.ext";
	
	public final static String GET_ANGEL_CODE = DOMAIN + "/cn.com.bluemoon.dispatch.app.qrcode.moonAngelQrCodeService.biz.ext";
	
	public final static String GET_USERINFO_ACTION = DOMAIN + "/cn.com.bluemoon.dispatch.app.user.getUserInfo.biz.ext";
	
	public final static String CHANGE_PASSWORD_ACTION = DOMAIN + "/cn.com.bluemoon.dispatch.app.user.updatePassword.biz.ext";
	
	public final static String LOGINOUT_ACTION = DOMAIN + "/cn.com.bluemoon.dispatch.app.user.logout.biz.ext";
	
	public final static String GET_ORDER_COUNT_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.getOrderCount.biz.ext";
	
	public final static String GET_ORDERS_DETAIL_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.getOrdersDetail.biz.ext";
	
	public final static String ACCEPT_ORDERS_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.acceptOrders.biz.ext";
	
	public final static String CANCEL_APPOINTMENT_ORDER_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.cancelAppointmentOrder.biz.ext";
	
	public final static String GET_ORDER_DETAIL_BY_DISPATCHID_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.getOrderDetailByDispatchId.biz.ext";
	
	public final static String TO_DELIVER_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.toDeliver.biz.ext";
	
	public final static String RETURN_OR_EXCHANGE_GOODS_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.returnOrExchangeGoods.biz.ext";
	
	public final static String UPDATE_OR_APPOINTMENT_DELIVERY_TIME_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.updateOrAppointmentDeliveryTime.biz.ext";
	
	public final static String ACCEPTANCE_CHECK_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.acceptanceCheck.biz.ext";
	
	public final static String ORDER_SIGN_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.orderSign.biz.ext";
	
	
	public final static String GET_ORDERDETAIL_BYDISPATCHID_ACTION = DOMAIN + "/cn.com.bluemoon.dispatch.app.order.getOrderDetailByDispatchId.biz.ext";

	public final static String  GET_HISTORY_ORDERS_ACTION = DOMAIN +"/cn.com.bluemoon.dispatch.app.order.getHistoryOrders.biz.ext";
	
	public final static String  GET_STOREHOUSE_LIST_ACTION = DOMAIN +"/cn.com.bluemoon.dispatch.app.storehouse.getStorehouseList.biz.ext";
	public final static String  SAVE_STOREHOUSE_ACTION = DOMAIN +"/cn.com.bluemoon.dispatch.app.storehouse.saveStorehouse.biz.ext";

	public final static String ORDER_PICKUP_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.pickup.getOrderInfoByPickupCode.biz.ext";
	
	public final static String ORDER_CONFIRM_PICKUP_API = DOMAIN + "/cn.com.bluemoon.dispatch.app.pickup.pickupOrder.biz.ext";
	
	/******************************ticket***********************************/
	public final static String FIND_SUBTICKECT_ACTION = DOMAIN_WX + "/findSubTickect";

	public final static String SAVE_CHECKRECORD_ACTION = DOMAIN_WX + "/saveCheckRecord";
	
	public final static String SAVE_CHECKRECORD_FOR_EMPLOYEE = DOMAIN_WX + "/saveCheckRecordForEmployee";
	
	public final static String FIND_ACTTIME_BY_VENUE_AND_DATE_ACTION = DOMAIN_WX + "/findActTimeByVenueAndDate";
	
	
}
