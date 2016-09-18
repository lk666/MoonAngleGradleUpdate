package cn.com.bluemoon.delivery.app.api;

import android.content.Context;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Decoder.BASE64Encoder;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfoPickup;
import cn.com.bluemoon.delivery.app.api.model.Storehouse;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.UploadClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.coupon.Coupon;
import cn.com.bluemoon.delivery.app.api.model.inventory.ProductPreDeliverVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ProductPreReceiveVo;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PromoteInfo;
import cn.com.bluemoon.delivery.app.api.model.punchcard.Product;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.app.api.model.team.RelationDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.entity.ProductType;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.inventory.ImageUtil;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DES;
import cn.com.bluemoon.delivery.utils.StringUtil;

public class DeliveryApi {

    protected static final String TOKEN = "token";

    /**
     * 提交http请求
     *
     * @param params  参数列表
     * @param subUrl  请求的url子部
     * @param handler 回调
     */
    protected static void postRequest(Map<String, Object> params, String subUrl,
                                    AsyncHttpResponseHandler handler) {
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(subUrl, ApiClientHelper.getParamUrl());

        Context context = AppContext.getInstance();
        if (handler instanceof WithContextTextHttpResponseHandler) {
            context = ((WithContextTextHttpResponseHandler) handler).getContext();
        }

        ApiHttpClient.post(context, url, jsonString, handler);
    }

    /**
     * 提交mock http请求
     *
     * @param params  参数列表
     * @param subUrl  请求的url子部
     * @param handler 回调
     */
    protected static void postMockRequest(Map<String, Object> params, String subUrl,
                                        AsyncHttpResponseHandler handler) {
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(subUrl, ApiClientHelper.getParamUrl());

        Context context = AppContext.getInstance();
        if (handler instanceof WithContextTextHttpResponseHandler) {
            context = ((WithContextTextHttpResponseHandler) handler).getContext();
        }

        ApiHttpClient.postMock(context, url, jsonString, handler);
    }

    /************************
     * 2.1 用户相关
     **********************************/

	/* 2.1.1 用户登录 */
    /* 返回： ResulToken */
    public static void ssoLogin(String account, String password,
                                AsyncHttpResponseHandler handler) {

        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(password)) {
            return;
        }

        String passwordEncrypt = DES.encrypt(password, Constants.DES_KEY);

        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", passwordEncrypt);
        params.put("deviceNum",
                ClientStateManager.getChannelId(AppContext.getInstance()));
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/user/ssoLogin%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.1.2 获取用户信息 */
    /* 返回： ResultUser */
    public static void getUserInfo(String token,
                                   AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/user/getUserInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.1.3 退出登录 */
    /* 返回： ResultBase */
    public static void logout(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("deviceNum",
                ClientStateManager.getChannelId(AppContext.getInstance()));

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/user/logout%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.1.4 修改密码 */
    /* 返回： ResultBase */
    public static void updatePassword(String token, String oldPassword,
                                      String newPassword, AsyncHttpResponseHandler handler) {

        if (null == token || StringUtil.isEmpty(oldPassword)
                || StringUtil.isEmpty(newPassword)) {
            return;
        }

        String oldPasswordEncrypt = DES.encrypt(oldPassword, Constants.DES_KEY);
        String newPasswordEncrypt = DES.encrypt(newPassword, Constants.DES_KEY);

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("oldPassword", oldPasswordEncrypt);
        params.put("newPassword", newPasswordEncrypt);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/user/updatePassword%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.1.5 重置密码 */
    /* 返回： ResultBase */
    public static void resetPassword(String mobileNo, String verifyCode,
                                     String newPassword, AsyncHttpResponseHandler handler) {

        if (StringUtil.isEmpty(mobileNo) || StringUtil.isEmpty(verifyCode)
                || StringUtil.isEmpty(newPassword)) {
            return;
        }

        String newPasswordEncrypt = DES.encrypt(newPassword, Constants.DES_KEY);

        Map<String, String> params = new HashMap<>();
        params.put("mobileNo", mobileNo);
        params.put("verifyCode", verifyCode);
        params.put("newPassword", newPasswordEncrypt);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/user/resetPassword%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.1.6 获取验证码(短信) */
    /* 返回： ResultVailCode */
    public static void getVerifyCode(String mobileNo, String account,
                                     AsyncHttpResponseHandler handler) {

        if (StringUtil.isEmpty(mobileNo) || StringUtil.isEmpty(account)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("mobileNo", mobileNo);
        params.put("account", account);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/user/getVerifyCode%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.1.7 获得用户APP菜单权限 */
    /* 返回： ResultUserRight */
    public static void getAppRights(String token,
                                    AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/user/getAppRights%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /************************
     * 2.2 订单相关
     **********************************/

	/* 2.2.1获取各类型订单的数量 */
    /* 返回： ResultOrderCount */
    public static void getOrderCount(String token,
                                     AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/order/getOrderCount%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.2 根据类型获取订单列表 */
    /* 返回： ResultOrderVo */
    public static void getOrdersByType(String token, OrderType type,
                                       AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("type", type.getType());
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/order/getOrdersByType%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.3 根据外部订单编号获取订单详情 */
    /* 返回： ResultOrderInfo */
    public static void getOrderDetailByOrderId(String token, String orderId,
                                               AsyncHttpResponseHandler handler) {

        if (null == token || orderId == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderId", orderId);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(
                "bluemoon-control/order/getOrderDetailByOrderId%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.4 接单 */
    /* 返回： ResultBase */
    public static void acceptOrder(String token, String orderId,
                                   AsyncHttpResponseHandler handler) {

        if (null == token || orderId == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderId", orderId);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/order/acceptOrder%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.5 修改/预约送货时间 */
    /* 返回： ResultBase */
    public static void updateOrAppointmentDeliveryTime(String token,
                                                       String orderId, Long date, String type,
                                                       AsyncHttpResponseHandler handler) {

        if (null == token || orderId == null || type == null) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderId", orderId);
        params.put("date", date);
        params.put("type", type);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(
                "bluemoon-control/order/updateOrAppointmentDeliveryTime%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.6 送货 */
    /* 返回： ResultBase */
    public static void toDelivery(String token, String orderId,
                                  AsyncHttpResponseHandler handler) {

        if (null == token || orderId == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderId", orderId);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/order/toDelivery%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.7 订单签收 */
    /* 返回： ResultBase */
    public static void orderSign(String token, String orderId, String signType,
                                 String receiveCode, AsyncHttpResponseHandler handler) {

        if (null == token || orderId == null
                || signType == null
                || receiveCode == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderId", orderId);
        params.put("signType", signType);
        params.put("receiveCode", receiveCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/order/orderSign%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.8 取消已接单订单 */
    /* 返回： ResultBase */
    public static void cancelAppointmentOrder(String token, String orderId,
                                              AsyncHttpResponseHandler handler) {

        if (null == token || orderId == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderId", orderId);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(
                "bluemoon-control/order/cancelAppointmentOrder%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.9 订单退换货 */
    /* 返回： ResultBase */
    public static void returnOrExchangeGoods(String token, String orderId,
                                             String dispatchId, String type, String orderSource,
                                             String msg,
                                             String productId, String productType, int
                                                     productAmount,
                                             int productTotal, byte[] file, String explain,
                                             AsyncHttpResponseHandler handler) {

        if (null == token || orderId == null
                || StringUtil.isEmpty(dispatchId) || StringUtil.isEmpty(type)
                || StringUtil.isEmpty(type)
                || StringUtil.isEmpty(orderSource) || StringUtil.isEmpty(msg)
                || StringUtil.isEmpty(productType) || null == file) {
            return;
        }

        BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(file);

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderId", orderId);
        params.put("dispatchId", dispatchId);
        params.put("type", type);
        params.put("orderSource", orderSource);
        params.put("msg", msg);
        params.put("productId", productId);
        params.put("productType", productType);
        params.put("productAmount", productAmount);
        params.put("productTotal", productTotal);
        params.put("file", fileString);
        params.put("explain", explain);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(
                "bluemoon-control/order/returnOrExchangeGoods%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.2.9.1 订单退货 */
    /* 返回： ResultBase */
    public static void returnOrExchangeGoods(String token, String orderId,
                                             String dispatchId, String orderSource, String msg,
                                             byte[] file,
                                             String explain, AsyncHttpResponseHandler handler) {
        returnOrExchangeGoods(token, orderId, dispatchId, "return",
                orderSource, msg, "-1", "all", -1, -1, file, explain, handler);
    }

    /* 2.2.10 获取历史订单列表 */
    /* 返回： ResultOrder */
    public static void getHistoryOrders(String token,
                                        String orderType, int count, long timestamp, long
                                                startTime, long endTime,
                                        AsyncHttpResponseHandler handler) {

        if (null == token || orderType == null) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderType", orderType);
        params.put("count", count);
        params.put("timestamp", timestamp);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/order/getHistoryOrders%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /******************
     * 2.3 仓库相关
     *******************************/

	/* 2.3.1 获取仓库列表信息 */
    /* 返回： ResultStorehouse */
    public static void getStorehouseList(String token,
                                         AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(
                "bluemoon-control/storehouse/getStorehouseList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.3.2 保存选择仓库信息 */
    /* 返回： ResultBase */
    public static void saveStorehouse(String token, String dispatchId,
                                      Storehouse storehouse, AsyncHttpResponseHandler handler) {

        if (null == token || StringUtil.isEmpty(dispatchId)
                || null == storehouse) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("dispatchId", dispatchId);
        params.put("storehouseCode", storehouse.getStorehouseCode());
        params.put("storehouseName", storehouse.getStorehouseName());
        params.put("storechargeCode", storehouse.getStorechargeCode());
        params.put("storechargeName", storehouse.getStorechargeName());
        params.put("storechargeMobileno", storehouse.getStorechargeMobileno());
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(
                "bluemoon-control/storehouse/saveStorehouse%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /******************
     * 2.4 二维码相关
     *******************************/

	/* 2.4.1 月亮天使获取二维码 */
    /* 返回： ResultAngelQr */
    public static void moonAngelQrCodeService(String token,
                                              AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(
                "bluemoon-control/qrcode/moonAngelQrCodeService%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.5.1 扫描自提码获取订单信息接口 */
    /* 返回： ResultOrderInfoPickup */
    public static void getOrderInfo(String token, String pickupCode, AsyncHttpResponseHandler
            handler) {

        if (null == token || StringUtil.isEmpty(pickupCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("pickupCode", pickupCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/pickup/getOrderInfoByPickupCode%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.5.2 确定自提订单 */
    /* 返回： ResultBase */
    public static void pickupOrder(String signType, String token, ResultOrderInfoPickup result,
                                   AsyncHttpResponseHandler handler) {
        String orderId = result.getOrderId();
        String orderSource = result.getOrderSource();
        String storehouseCode = result.getStorehouseCode();
        String storehouseName = result.getStorehouseName();
        String storechargeCode = result.getStorechargeCode();
        String storechargeName = result.getStorechargeName();
        String storechargeMobileno = result.getStorechargeMobileno();
        String pickupCode = result.getPickupCode();
        String mobilePhone = result.getMobilePhone();

		/*if (null == token || StringUtil.isEmpty(orderId) || StringUtil.isEmpty(orderSource)
                || StringUtil.isEmpty(storehouseCode) || StringUtil.isEmpty(storehouseName)
				|| StringUtil.isEmpty(storechargeCode) || StringUtil.isEmpty(storechargeName)
				|| StringUtil.isEmpty(storechargeMobileno) || StringUtil.isEmpty(pickupCode)
				|| StringUtil.isEmpty(mobilePhone) || StringUtil.isEmpty(signType)) {
			return;
		}*/

        Map<String, String> params = new HashMap<>();
        params.put("signType", signType);
        params.put(TOKEN, token);
        params.put("orderId", orderId);
        params.put("orderSource", orderSource);
        params.put("storehouseCode", storehouseCode);
        params.put("storehouseName", storehouseName);
        params.put("storechargeCode", storechargeCode);
        params.put("storechargeName", storechargeName);
        params.put("storechargeMobileno", storechargeMobileno);
        params.put("pickupCode", pickupCode);
        params.put("mobilePhone", mobilePhone);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/pickup/pickupOrder%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.6.1 获取业务字典接口 */
    /* 返回： ResultDict */
    public static void getDictInfo(AsyncHttpResponseHandler handler) {

        Map<String, String> params = new HashMap<>();
        params.put("type", Constants.TYPE_DICTINFO);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/dict/getDictInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.7.1 获取app最新版本 */
    /* 返回： ResultVersionInfo */
    public static void getLastVersion(AsyncHttpResponseHandler handler) {
        Map<String, String> params = new HashMap<>();
        params.put("platform", ApiClientHelper.CLIENT);
        params.put("appType", Constants.APP_TYPE);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/version/getLastVersion%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /* 2.8.1 获取场馆/场次列表 */
    /* 返回： ResultVenueInfo */
    public static void getVenueList(String token, String type, String venueCode,
                                    AsyncHttpResponseHandler handler) {

        if (null == token || StringUtil.isEmpty(type)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("type", type);
        params.put("venueCode", venueCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/ticket/getVenueList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.8.2 科技馆检票扫码 */
    /* 返回： ResultAppointmentInfo */
    public static void checkScanCode(String token, String ticketCode, AsyncHttpResponseHandler
            handler) {

        if (null == token || StringUtil.isEmpty(ticketCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("ticketCode", ticketCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/ticket/checkScanCode%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.8.3 扫描门票后进场 */
    /* 返回： ResultBase */
    public static void comesInto(String token, String venueCode, String timesCode, String
            ticketCode, AsyncHttpResponseHandler handler) {

        if (null == token || StringUtil.isEmpty(ticketCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("venueCode", venueCode);
        params.put("timesCode", timesCode);
        params.put("ticketCode", ticketCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/ticket/comesInto%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /***************************************
     * 收货相关接口
     *********************************/

	/*待收货-汇总接口 */
    /* 返回： ResultOrderVo */
    public static void getWaitReceiptOrders(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);

        postRequest(params, "bluemoon-control/receipt/getWaitReceiptOrders%s", handler);
    }

    /*已收货-汇总接口 */
    /* 返回： ResultOrderVo */
    public static void getReceiptOrders(String token, long startDate, long endDate,
                                        AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        postRequest(params, "bluemoon-control/receipt/getReceiptOrders%s", handler);
    }

    /*待收货详情*/
    /* 返回： ResultDeliverOrderDetailInfo */
    public static void getReceiveDetail(String token, String orderCode, AsyncHttpResponseHandler
            handler) {
        if (null == token || StringUtil.isEmpty(orderCode)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderCode", orderCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/receipt/getWaitReceiptOrderDetail%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /***************************************
     * 发货相关接口
     *********************************/


	/*待发货-汇总接口 */
    /* 返回： ResultOrderVo */
    public static void getWaitOutOrders(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);

        postRequest(params, "bluemoon-control/out/getWaitOutOrders%s", handler);
    }

    /*待发货详情*/
    /* 返回： ResultDeliverOrderDetailInfo */
    public static void getDeliverDetail(String token, String orderCode, AsyncHttpResponseHandler
            handler) {
        if (null == token) {
            return;
        }
        if (StringUtil.isEmpty(orderCode)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderCode", orderCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/out/getWaitOutOrderDetail%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*已发货详情*/
    /* 返回： ResultDeliverOrderDetailInfo */
    public static void getOutDeliverDetail(String token, String orderCode,
                                           AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        if (StringUtil.isEmpty(orderCode)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderCode", orderCode);
        postRequest(params, "bluemoon-control/out/getOutOrderDetail%s", handler);
    }

    /*已收货详情*/
    /* 返回： ResultDeliverOrderDetailInfo */
    public static void getOutReceiveDetail(String token, String orderCode,
                                           AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        if (StringUtil.isEmpty(orderCode)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderCode", orderCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/receipt/getReceiptOrderDetail%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*已发货-汇总接口 */
    /* 返回： ResultOrderVo */
    public static void getOutOrders(String token, long startDate, long endDate,
                                    AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        postRequest(params, "bluemoon-control/out/getOutOrders%s", handler);
    }


    /*客服（营运专员）查询接口 */
    /* 返回： ResultCustormerService */
    public static void queryOperatorPersons(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/mallerpCommon/queryOperatorPersons%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /***************************************
     * 仓库相关接口
     *********************************/



	/*库存汇总信息查询接口 */
    /* 返回： ResultStock */
    public static void queryStockSummary(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/stock/queryStockSummary%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*仓库列表查询接口 */
    /* 返回： ResultStore */
    public static void queryStoresBycharger(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/store/queryStoresBycharger%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*仓库收货地址查询接口 */
    /* 返回： ResultMallStoreRecieverAddress */
    public static void queryReceiveAddressByStoreCode(String token, String storeCode,
                                                      AsyncHttpResponseHandler handler) {

        if (StringUtil.isEmpty(token) || StringUtil.isEmpty(storeCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("storeCode", storeCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/store/queryReceiveAddressByStoreCode%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*仓库地址新增与编辑接口 */
    /* 返回： Result */
    public static void manageReceiveAddress(String token, MallStoreRecieverAddress
            mallStoreRecieverAddress, AsyncHttpResponseHandler handler) {

        if (StringUtil.isEmpty(token) || null == mallStoreRecieverAddress) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("storeReceiveAddress", mallStoreRecieverAddress);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/store/manageReceiveAddress%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*默认仓库地址修改接口 */
    /* 返回： Result */
    public static void modifyDefaultAddress(String token, String storeCode, int addressId,
                                            AsyncHttpResponseHandler handler) {

        if (StringUtil.isEmpty(token) || StringUtil.isEmpty(storeCode) || addressId == 0) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("storeCode", storeCode);
        params.put("addressId", addressId);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/store/modifyDefaultAddress%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*删除仓库收货地址 */
    /* 返回： Result */
    public static void deleteReceiveAddress(String token, int addressId, AsyncHttpResponseHandler
            handler) {

        if (StringUtil.isEmpty(token) || addressId == 0) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("addressId", addressId);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/store/deleteReceiveAddress%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /* 正常品/不良品 库存详情查询 */
    /* 返回： ResultProductDetail */
    public static void queryStockDetail(String token, String storeCode, ProductType productType,
                                        AsyncHttpResponseHandler handler) {

        if (null == token || StringUtil.isEmpty(storeCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("storeCode", storeCode);
        String jsonString = JSONObject.toJSONString(params);
        String url;
        if (productType.equals(ProductType.BAD)) {
            url = String.format("bluemoon-control/stock/queryBadStockDetail%s",
                    ApiClientHelper.getParamUrl());
        } else {
            url = String.format("bluemoon-control/stock/queryNormalStockDetail%s",
                    ApiClientHelper.getParamUrl());
        }
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /***********************
     * 2.0行政区域相关
     ********************************/

	/* 2.0.1 级联查询全国行政区域 */
    /* 返回： ResultArea */
    public static void getRegionSelect(String pid, String type,
                                       AsyncHttpResponseHandler handler) {

        Map<String, String> params = new HashMap<>();
        String jsonString = "{}";
        if (!(pid == null && type == null)) {
            params.put("pid", pid);
            params.put("type", type);
            jsonString = JSONObject.toJSONString(params);
        }

        String url = String.format(
                "moonRegion/region/getRegionSelect.action%s",
                ApiClientHelper.getParamUrl());

        ApiHttpClient.postDirect(AppContext.getInstance(), String.format(BuildConfig
                .ADDRESS_URL, url), jsonString, handler);

    }


    /*2.9.1是否打卡查询接口 */
    /* 返回： ResultIsPunchCard */
    public static void isPunchCard(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/isPunchCard%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.9.2考勤打卡扫码 */
    /* 返回： ResultCheckScanCode */
    public static void checkScanCodeCard(String token, String attendanceCode,
                                         AsyncHttpResponseHandler handler) {

        if (null == token || StringUtil.isEmpty(attendanceCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("attendanceCode", attendanceCode);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/checkScanCode%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.9.3保存更新打卡信息 */
    /* 返回： ResultBase */
    public static void confirmAttendance(String token, PunchCard punchCard, String workTask,
                                         AsyncHttpResponseHandler handler) {

        if (null == token || punchCard == null || StringUtil.isEmpty(workTask)) {
            return;
        }

        if (punchCard.getLongitude() == Constants.DEFAUL_LOCATION) {
            punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
        }
        if (punchCard.getLatitude() == Constants.DEFAUL_LOCATION) {
            punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
        }
        if (punchCard.getAltitude() == Constants.DEFAUL_LOCATION) {
            punchCard.setAltitude(Constants.UNKNOW_LONGITUDE);
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("punchCard", punchCard);
        params.put("workTask", workTask);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/confirmAttendance%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.4 展示打卡信息 */
    /* 返回： ResultShowPunchCardDetail */
    public static void getPunchCard(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getPunchCard%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.4 展示打卡信息 */
    /* 返回： ResultGetProduct */
    public static void getProductList(String token, String condition, long timestamp,
                                      AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("condition", condition);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getProductList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.5 获取工作日志 */
    /* 返回： ResultDiaryContent */
    public static void getWorkDiary(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getWorkDiary%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*收货和发货详情获取单据图片列表*/
    public static void getPicDetail(String token, String relativeOrderCode,
                                    AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        if (StringUtil.isEmpty(relativeOrderCode)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("relativeOrderCode", relativeOrderCode);
        postRequest(params, "bluemoon-control/mallerpCommon/getOrderPics%s", handler);
    }

    /* 上传单据图片 */
    public static void uploadTicketPic(String token, String relativeOrderCode, String
            operateType, Bitmap file,
                                       AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

		/*BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(ImageUtil.getBytes(file));*/
        BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(ImageUtil.Bitmap2Bytes(file));

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("relativeOrderCode", relativeOrderCode);
        params.put("operateType", operateType);
        params.put("picBase64", fileString);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/mallerpCommon/upLoadPic%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.11 展示图片 */
    /* 返回： ResultImage */
    public static void getImgList(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getImgList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.13 展示打卡记录 */
    /* 返回： ResultPunchCardList */
    public static void getPunchCardList(String token, long timestamp, AsyncHttpResponseHandler
            handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getPunchCardList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.14 选择上班点（分页） */
	/* 返回： ResultWorkPlaceList */
    public static void getWorkplaceList(String token, String condition, int count, long
            timestamp, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("condition", condition);
        params.put("count", count);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getWorkplaceList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.15 展示工作任务 */
	/* 返回： ResultGetWorkTask */
    public static void getWorkTask(String token, String workTaskType, AsyncHttpResponseHandler
            handler) {

        if (null == token || StringUtil.isEmpty(workTaskType)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("workTaskType", workTaskType);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getWorkTask%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.6 保存更新工作日志 */
	/* 返回： ResultBase */
    public static void confirmWorkDiary(String token, String diaryContent,
                                        AsyncHttpResponseHandler handler) {

        if (null == token || StringUtil.isEmpty(diaryContent)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("diaryContent", diaryContent);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/confirmWorkDiary%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.7 获取日报 */
	/* 返回： ResultGetWorkDiaryList */
    public static void getWorkDiaryList(String token, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/getWorkDailyList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.9 保存更新日报 */
	/* 返回： ResultBase */
    public static void confirmWorkDaily(String token, int totalBreedSalesNum, int totalSalesNum,
                                        Product[] wd, AsyncHttpResponseHandler handler) {

        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("workDailyList", wd);
        params.put("totalBreedSalesNum", totalBreedSalesNum);
        params.put("totalSalesNum", totalSalesNum);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/confirmWorkDaily%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.10 上传图片 */
	/* 返回： ResultBase */
    public static void uploadImg(String token, byte[] file, AsyncHttpResponseHandler handler) {

        if (null == token || null == file) {
            return;
        }

        BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(file);

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("imgPath", fileString);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/uploadImg%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.9.10 2.9.12 删除图片 */
	/* 返回： ResultBase */
    public static void removeImg(String token, long imgId, AsyncHttpResponseHandler handler) {
        if (null == token || imgId == 0) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("imgId", imgId);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/attendance/removeImg%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /* 获取差异原因*/
    public static void getDictInfo(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("dictTypeId", "MS_RECEIPT_DIFFER");
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/mallerpCommon/queryDifferReasons%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);

    }


    /*提交发货详情*/
    public static void getSubmitDeliverDetail(String token, String orderCode, long deliDate,
                                              String outBackup
            , int deliStoreAddrId, List<ProductPreDeliverVo> outOrderDetail,
                                              AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(orderCode)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderCode", orderCode);
        params.put("deliDate", deliDate);
        params.put("outBackup", outBackup);
        params.put("deliStoreAddrId", deliStoreAddrId);
        params.put("outOrderDetail", outOrderDetail);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/out/addOutOrder%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*提交收货详情*/
    public static void getSubmitReceiveDetail(String token, String orderCode, long reDate, int
            reStoreAddrId,
                                              List<ProductPreReceiveVo> receiptOrderDetail,
                                              AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(orderCode)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("orderCode", orderCode);
        params.put("reDate", reDate);
        params.put("reStoreAddrId", reStoreAddrId);
        params.put("receiptOrderDetail", receiptOrderDetail);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/receipt/addReceiptOrder%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /* 首页未读消息数量统计接口 */
	/* 返回： ResultModelNum */
    public static void getModelNum(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/mallerpCommon/getModelNum%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.10.1查询消费者基本信息*/
	/*返回：ResultUserBase*/
    public static void getCustomerInfo(String token, String contents, AsyncHttpResponseHandler
            handler) {
        if (null == token || contents == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("contents", contents);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/card/getCustomerInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.10.2查询有发券权限的活动*/
	/*返回：ResultCouponAct*/
    public static void getOwnAuthCouponAct(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/card/getOwnAuthCouponAct%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.10.3人工发券*/
	/*返回：ResultBase*/
    public static void mensendCoupon(String token, String mobile, String activityCode,
                                     List<Coupon> coupons, AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(mobile)
                || StringUtil.isEmpty(activityCode) || coupons == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("mobile", mobile);
        params.put("activityCode", activityCode);
        params.put("coupons", coupons);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/card/mensendCoupon%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.10.4获取天使推送记录*/
	/*返回：ResultMensendLog*/
    public static void getMensendCouponLog(String token, long date, AsyncHttpResponseHandler
            handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("date", date);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/card/getMensendCouponLog%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /************************
     * 2.11 消息、通知、知识库相关
     **********************************/


	/*2.11.1 最新消息*/
	/*返回：ResultNews*/
    public static void getNewMessage(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/message/getNewMessage%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.11.2 消息列表*/
	/*返回：ResultMessages*/
    public static void getMessageList(String token, int pageSize, long timestamp,
                                      AsyncHttpResponseHandler handler) {
        if (null == token || pageSize <= 0) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("pageSize", pageSize);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/message/getMessageList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.11.3通知列表*/
	/*返回：ResultInfos*/
    public static void getInformationList(String token, int pageSize, long timestamp,
                                          AsyncHttpResponseHandler handler) {
        if (null == token || pageSize <= 0) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("pageSize", pageSize);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/message/getInformationList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.11.4通知详情*/
	/*返回：ResultInfoDetail*/
    public static void getInfoDetail(String token, String infoId, AsyncHttpResponseHandler
            handler) {
        if (null == token || StringUtil.isEmpty(infoId)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("infoId", infoId);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/message/getInfoDetail%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.11.5知识库菜单列表*/
	/*返回：ResultKnowledges*/
    public static void getMenuList(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/knowledge/getMenuList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.11.6知识库文章详情*/
	/*返回：ResultPaperDetail*/
    public static void getPaperDetail(String token, String paperId, AsyncHttpResponseHandler
            handler) {
        if (null == token || StringUtil.isEmpty(paperId)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("paperId", paperId);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/knowledge/getPaperDetail%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.11.7收藏列表*/
	/*返回：ResultFavorites*/
    public static void getCollectList(String token, int pageSize, long timestamp,
                                      AsyncHttpResponseHandler handler) {
        if (null == token || pageSize <= 0) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("pageSize", pageSize);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/knowledge/getCollectList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.11.8收藏/取消收藏*/
	/*返回：ResultBase*/

    public static void collectPaper(String token, String paperId, boolean isCollect,
                                    AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(paperId)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("paperId", paperId);
        params.put("isCollect", isCollect);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/knowledge/collectPaper%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /************************ 洗衣服务 **********************************/
    /**
     * 2.1	获取洗衣服务订单信息
     *
     * @param token String	Y		登录凭证
     */
    public static void getOrderInfos(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/getOrderInfos%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.5 收衣记录*/
	/*返回：ResultCollectInfo*/
    public static void collectInfoRecord(String token, long startDate, long endDate,
                                         AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/collectInfoRecord%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*5.8收衣记录*/
	/*返回：ResultCollectInfo*/
    public static void collectInfoRecord2(String token, long startDate, long endDate,
                                          AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/collectInfoRecord%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /**
     * 5.2	获取衣物配置项
     *
     * @param token String	Y		登录凭证
     */
    public static void getClothesTypeConfigs(String token, String typeCode,
                                             AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("typeCode", typeCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/getClothesTypeConfigs%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 3.4	衣物登记 保存
     *
     * @param token           String	Y		登录凭证
     * @param collectCode     String	N		收衣单号
     * @param typeCode        String	Y		衣物类型编号
     * @param clothesnameCode String	N		衣物名称编码
     * @param clothesCode     String	Y		衣物编码
     * @param hasFlaw         int	    Y		有无瑕疵（1:有；0：无）
     * @param flawDesc        String	N		瑕疵描述
     * @param hasStain        int	    Y       有无污渍（1:有；0：无）
     * @param remark          String    N		备注
     * @param clothesImgIds   String	Y		图片IDs 多个用豆号隔开 3232,3234223
     * @param outerCode       String	Y		洗衣服务订单号
     */
    public static void registerCollectInfo(String token, String collectCode, String typeCode,
                                           String clothesnameCode, String clothesCode, int hasFlaw,
                                           String flawDesc, int hasStain, String remark,
                                           String clothesImgIds, String outerCode,
                                           AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("collectCode", collectCode);
        params.put("typeCode", typeCode);
        params.put("clothesnameCode", clothesnameCode);
        params.put("clothesCode", clothesCode);
        params.put("hasFlaw", hasFlaw);

        params.put("flawDesc", flawDesc);
        params.put("hasStain", hasStain);
        params.put("remark", remark);
        params.put("clothesImgIds", clothesImgIds);
        params.put("outerCode", outerCode);

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/registerCollectInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 3.1	收衣登记
     *
     * @param token       String	Y		登录凭证
     * @param outerCode   String	N		洗衣服务订单号
     * @param collectCode String	N		收衣单号
     */
    public static void startCollectInfo(String token, String outerCode, String collectCode,
                                        AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("outerCode", outerCode);
        params.put("collectCode", collectCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/startCollectInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.3接收/取消服务订单*/
	/*返回：ResultBase*/
    public static void signOrderInfo(String token, String outerCode, String status,
                                     AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(status) || StringUtil.isEmpty(outerCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("status", status);
        params.put("outerCode", outerCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/signOrderInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.6收衣订单详情*/
	/*返回：ResultClothingRecord*/
    public static void collectInfoDetails(String token, String collectCode,
                                          AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(collectCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("collectCode", collectCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/collectInfoDetails%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*5.9收衣订单详情*/
	/*返回：ResultClothingRecord*/
    public static void collectInfoDetails2(String token, String collectCode,
                                           AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(collectCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("collectCode", collectCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/collectInfoDetails%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.4.衣物转交*/
	/*返回：ResultBase*/
    public static void turnOrderInfo(String token, String collectCode, String receiverCode,
                                     String receiverName, String receiverPhone, String remark,
                                     AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(collectCode) || StringUtil.isEmpty(receiverCode)
                || StringUtil.isEmpty(receiverName) || StringUtil.isEmpty(receiverPhone)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("collectCode", collectCode);
        params.put("receiverCode", receiverCode);
        params.put("receiverName", receiverName);
        params.put("receiverPhone", receiverPhone);
        params.put("remark", remark);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/turnOrderInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*2.9查询衣物转交记录*/
	/*返回：ResultClothesDeliverInfos*/
    public static void queryTransmitInfo(String token, String collectCode,
                                         AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(collectCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("collectCode", collectCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/queryTransmitInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*4.1拒绝接收*/
	/*返回：ResultBase*/
    public static void refuseOrderInfo(String token, String collectCode, String remark,
                                       AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(collectCode) || StringUtil.isEmpty(remark)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("remark", remark);
        params.put("collectCode", collectCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/refuseOrderInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*4.2接收洗衣*/
	/*返回：ResultReceiveCollectInfo */
    public static void receiveCollectInfo(String token, String collectCode,
                                          AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(collectCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("collectCode", collectCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/receiveCollectInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*4.3确认收衣 保存*/
	/*返回：ResultBase*/
    public static void confirmOrderInfo(String token, String collectCode,
                                        AsyncHttpResponseHandler handler) {
        if (null == token || StringUtil.isEmpty(collectCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("collectCode", collectCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/confirmOrderInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*5.3获取活动列表*/
	/*返回：ResultActivityInfo*/
    public static void getActivityInfos(String token,
                                        AsyncHttpResponseHandler handler) {
        if (StringUtil.isEmpty(token)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/getActivityInfos%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*5.4获取活动说明*/
	/*返回：ResultActivityDesc*/
    public static void getActivityInfo(String token, String activityCode,
                                       AsyncHttpResponseHandler handler) {
        if (StringUtil.isEmpty(token) || StringUtil.isEmpty(activityCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("activityCode", activityCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/getActivityInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*5.5获取注意事项*/
	/*返回：ResultMatter*/
    public static void getMatters(String token, String activityCode,
                                  AsyncHttpResponseHandler handler) {
        if (StringUtil.isEmpty(token) || StringUtil.isEmpty(activityCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("activityCode", activityCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/getMatters%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 3.2 更改收衣登记
     *
     * @param token           登录凭证（必填）	string
     * @param appointBackTime 预约还衣时间（如果加急，此字段必填） number
     * @param collectBrcode   收衣单条码	string
     * @param collectCode     收衣单号(必填)	string
     * @param isUrgent        是否加急 1为加急 0为不普通(必填)	number
     */
    public static void updateCollectInfoParam(String token, long appointBackTime, String
            collectBrcode, String collectCode, int isUrgent, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("appointBackTime", appointBackTime);
        params.put("collectCode", collectCode);
        params.put("collectBrcode", collectBrcode);
        params.put("isUrgent", isUrgent);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/updateCollectInfoParam%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /*6.4获取人员信息*/
	/*返回：ResultUserInfo*/
    public static void getEmp(String token, String empCode,
                              AsyncHttpResponseHandler handler) {
        if (StringUtil.isEmpty(token) || StringUtil.isEmpty(empCode)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("empCode", empCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/getEmp%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 3.3.衣物编码 (查看)
     *
     * @param token       登录凭证（必填）	string
     * @param clothesCode 衣物编码（必填）
     */
    public static void registerClothesCode(String token, String clothesCode,
                                           AsyncHttpResponseHandler handler) {
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("clothesCode", clothesCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/registerClothesCode%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 3.5.衣物登记 删除
     *
     * @param token       登录凭证（必填）	string
     * @param clothesCode 衣物编码（必填）
     */
    public static void delCollectInfo(String token, String clothesCode, AsyncHttpResponseHandler
            handler) {
        Map<String, String> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("clothesCode", clothesCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/delCollectInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 6.2删除图片
     *
     * @param token 登录凭证（必填）	string
     * @param imgId 衣物编码（必填）图片id(必填)
     */
    public static void delImg(String imgId, String token, AsyncHttpResponseHandler handler) {
        if (null == imgId || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("imgId", imgId);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/delImg%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 2.7.衣物详情（共用）
     *
     * @param clothesCode 衣物编码(必填) String
     * @param token       登录凭证(必填) String
     */
    public static void getCollectInfoDetailsItem(String clothesCode, String token,
                                                 AsyncHttpResponseHandler handler) {
        if (null == clothesCode || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("clothesCode", clothesCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/getCollectInfoDetailsItem%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 2.8扫描接单
     *
     * @param code  洗衣订单号或衣物编码(必填) String
     * @param token 登录凭证(必填) String
     */
    public static void scanOrderInfo(String code, String token, AsyncHttpResponseHandler handler) {
        if (null == code || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/scanOrderInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 5.7验证衣物编码
     *
     * @param clothesCode 衣物编码(必填) String
     * @param token       登录凭证(必填) String
     */
    public static void validateClothesCode(String clothesCode, String token,
                                           AsyncHttpResponseHandler handler) {
        if (null == clothesCode || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("clothesCode", clothesCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/validateClothesCode%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 2.2获取订单详情
     *
     * @param outerCode 洗衣服务订单号(必填) String
     * @param token     登录凭证(必填) String
     */
    public static void getOuterOrderInfo(String outerCode, String token, AsyncHttpResponseHandler
            handler) {
        if (null == outerCode || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("outerCode", outerCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/getOrderInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 6.1图片上传
     *
     * @param fileData 文件流(必填) byte[]
     * @param token    登录凭证(必填) String
     */
    public static void uploadClothesImg(String token, byte[] fileData, AsyncHttpResponseHandler
            handler) {
        if (null == fileData || null == token) {
            return;
        }

        BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(fileData);

        Map<String, String> params = new HashMap<>();
        params.put("fileData", fileString);
        params.put("fileName", UUID.randomUUID() + ".png");
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/uploadImg%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /**
     * 5.6收衣登记 保存(整合收衣记录)
     *
     * @param activityCode    活动编码(必填) String
     * @param address         详细地址(必填) String
     * @param appointBackTime 预约还衣时间 int
     * @param city            市(必填) String
     * @param clothesInfo     衣物列表 {@link UploadClothesInfo}
     * @param collectBrcode   收衣单条码(必填) String
     * @param county          区/县(必填) String
     * @param customerName    消费者姓名(必填) String
     * @param customerPhone   消费者电话(必填) String
     * @param isFee           是否收费（1.收费，0免费）(必填) String
     * @param isUrgent        是否加急(1,加急， 0 不加急)(必填) int
     * @param province        省份(必填) String
     * @param street          乡镇/街道(必填) String
     * @param token           登录凭证(必填) String
     * @param village         村/社区(必填) String
     */
    public static void registerCreatedCollectInfo(String activityCode, String address,
                                                  long appointBackTime, String city,
                                                  List<UploadClothesInfo> clothesInfo,
                                                  String collectBrcode, String county,
                                                  String customerName, String customerPhone,
                                                  int isFee, int isUrgent, String province,
                                                  String street, String token, String village,
                                                  AsyncHttpResponseHandler handler) {

        if (null == activityCode || null == address || null == city || null == clothesInfo ||
                null == collectBrcode || null == county || null == customerName ||
                null == customerPhone || null == province || null == street ||
                null == token || null == village) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("activityCode", activityCode);
        params.put("address", address);
        params.put("appointBackTime", appointBackTime);
        params.put("city", city);
        params.put("collectBrcode", collectBrcode);
        params.put("county", county);
        params.put("customerName", customerName);
        params.put("customerPhone", customerPhone);
        params.put("isFee", isFee);
        params.put("isUrgent", isUrgent);
        params.put("province", province);
        params.put("street", street);
        params.put(TOKEN, token);
        params.put("village", village);
        params.put("clothesInfo", JSON.toJSONString(clothesInfo));

        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/registerCollectInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 5.10查询活动收衣上限
     *
     * @param activityCode 活动编码(必填) String
     * @param token        登录凭证(必填) String
     */
    public static void queryActivityLimitNum(String activityCode, String token,
                                             AsyncHttpResponseHandler handler) {
        if (null == activityCode || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("activityCode", activityCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/activity/queryActivityLimitNum" +
                        "%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 5.1获取衣物类型列表
     *
     * @param activityCode 活动编码(必填) String
     * @param token        登录凭证(必填) String
     */
    public static void getClothesTypeInfos(String activityCode, String token,
                                           AsyncHttpResponseHandler handler) {
        if (null == activityCode || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("activityCode", activityCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/getClothesTypeInfos%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.14.1 新增推广点信息时上传图片*/
	/* 返回： ResultImageUpload */
    public static void uploadPromoteImg(String token, byte[] file, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == file) {
            return;
        }
        BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(file);
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("imgInfo", fileString);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/promote/uploadImg%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.14.2 推广点详情信息*/
	/* 返回： ResultPromoteInfo */
    public static void getPromoteInfo(String token, String bpCode, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == bpCode) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("bpCode", bpCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/promote/getPromoteInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 2.14.3 新增/编辑推广点信息 */
	/* 返回： ResultBase */
    public static void editPromoteInfo(String token, PromoteInfo info, AsyncHttpResponseHandler
            handler) {
        if (null == token || info == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("address", info.getAddress());
        params.put("bpCode", info.getBpCode());
        params.put("bpCode1", info.getBpCode1());
        params.put("bpName", info.getBpName());
        params.put("bpName1", info.getBpName1());
        params.put("cashPledge", info.getCashPledge());
        params.put("holidayPrice", info.getHolidayPrice());
        if (info.getPeopleFlow() != null) {
            params.put("peopleFlow", info.getPeopleFlow());
        }
        params.put("picInfo", info.getPicInfo());
        params.put("remark", info.getRemark());
        params.put("rentInfo", info.getRentInfo());
        params.put("siteType", info.getSiteType());
        params.put("wifi", info.getWifi());
        params.put("wiredNetwork", info.getWiredNetwork());
        params.put("workPrice", info.getWorkPrice());
        params.put("useArea", info.getUseArea());
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/promote/editPromoteInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.14.4 获取小区/商业中心列表*/
	/* 返回： ResultBpList */
    public static void getBpList(String token, String content, long timestamp,
                                 AsyncHttpResponseHandler handler) {
        if (null == token || null == content) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("content", content);
        params.put("pageSize", AppContext.PAGE_SIZE);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/promote/getBpList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*2.14.4 获取推广点档案列表*/
	/* 返回： ResultPromoteList */
    public static void getPromoteList(String token, String content, long timestamp,
                                      AsyncHttpResponseHandler handler) {
        if (null == token || content == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("content", content);
        params.put("pageSize", AppContext.PAGE_SIZE);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/promote/getPromoteList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*获取小组信息列表*/
	/*返回：ResultGroupList*/
    public static void getGroupList(String token, String content, int pageSize, long timestamp,
                                    AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        if (content == null) {
            content = "";
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("content", content);
        params.put("pageSize", pageSize);
        params.put("timestamp", timestamp);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/getGroupList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*CEO获取社区列表*/
	/*返回：ResultCommunityList*/
    public static void getCommunityList(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/getCommunityList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*CEO获取人员关系列表*/
	/*返回：ResultEmpList*/
    public static void getEmpList(String token, String content, String type,
                                  AsyncHttpResponseHandler handler) {
        if (null == token || null == type) {
            return;
        }
        if (content == null) {
            content = "";
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("content", content);
        params.put("type", type);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/getEmpList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*CEO删除人员辖区*/
	/*返回：ResultBase*/
    public static void deletePersonnelArea(String token, String bpCode, String empCode, String
            groupCode, AsyncHttpResponseHandler handler) {
        if (null == token || empCode == null || bpCode == null || groupCode == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("bpCode", bpCode);
        params.put("empCode", empCode);
        params.put("groupCode", groupCode);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/deletePersonnelArea%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /*CEO搜索服务区域列表*/
	/*返回：ResultServiceAreaList*/
    public static void getServiceAreaList(String token, String bpCode, String empCode, String
            content, int pageIndex, int pageSize, AsyncHttpResponseHandler handler) {
        if (null == token || empCode == null || bpCode == null) {
            return;
        }
        if (null == content) content = "";
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("bpCode", bpCode);
        params.put("empCode", empCode);
        params.put("content", content);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/getServiceAreaList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 机构新增人员关系 */
    public static void addRelationShip(RelationDetail item, String token, String type,
                                       AsyncHttpResponseHandler handler) {

        if (null == item || null == token || null == type) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("communityCode", item.getBpCode1());
        params.put("communityName", item.getBpName1());
        params.put("empCode", item.getEmpCode());
        params.put("empName", item.getEmpName());
        params.put("endDate", item.getEndDate());
        params.put("groupCode", item.getBpCode());
        params.put("groupName", item.getBpName());
        params.put("relationType", item.getRelType());
        params.put("remark", item.getRemark());
        params.put("startDate", item.getStartDate());
        params.put(TOKEN, token);
        params.put("type", type);
        params.put("workLength", item.getWorkLength());
        params.put("workType", item.getWorkType());
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/addRelationShip%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 添加服务区域 */
    public static void addServiceArea(String bpCode, List<String> bpCodeList, String empCode,
                                      String token, AsyncHttpResponseHandler handler) {
        if (null == bpCode || null == bpCodeList || null == empCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("bpCode", bpCode);
        params.put("bpCodeList", bpCodeList);
        params.put("empCode", empCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/addServiceArea%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 获取人员关系详情 */
    public static void getRelationShipDetail(String bpCode, String empCode,
                                             String token,
                                             AsyncHttpResponseHandler handler) {
        if (null == bpCode || null == empCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("bpCode", bpCode);
        params.put("empCode", empCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/getRelationShipDetail%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 获取人员辖区列表 */
    public static void getPersonnelAreaList(String groupCode, String empCode, int pageSize, long
            timestamp, String token, AsyncHttpResponseHandler handler) {
        if (null == empCode || null == token || null == groupCode) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("groupCode", groupCode);
        params.put("empCode", empCode);
        params.put("pageSize", pageSize);
        params.put("timestamp", timestamp);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/getPersonnelAreaList%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /* 获取小组/社区详情 */
    public static void getGroupDetailInfo(String content, int pageSize, long timestamp, String
            token, String type, AsyncHttpResponseHandler handler) {
        if (null == content || null == token || null == type) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.put("pageSize", pageSize);
        params.put("timestamp", timestamp);
        params.put(TOKEN, token);
        params.put("type", type);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/getGroupDetailInfo%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }


    /* 解除人员关系 */
    public static void deleteRelationShip(String bpCode, String empCode, long endDate, String
            token, String type, AsyncHttpResponseHandler handler) {
        if (null == bpCode || null == empCode || null == token || null == type) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("bpCode", bpCode);
        params.put("empCode", empCode);
        params.put("endDate", endDate);
        params.put(TOKEN, token);
        params.put("type", type);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("bluemoon-control/team/deleteRelationShip%s",
                ApiClientHelper.getParamUrl());
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 2.2获取订单详情，测试用
     *
     * @param outerCode 洗衣服务订单号(必填) String
     * @param token     登录凭证(必填) String
     */
    public static void _getOuterOrderInfo(String outerCode, String token,
                                          AsyncHttpResponseHandler handler) {
        if (null == outerCode || null == token) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("outerCode", outerCode);
        params.put(TOKEN, token);
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format("washingService-controller/wash/getOrderInfo%s",
                ApiClientHelper.getParamUrl());

        Context context = AppContext.getInstance();
        if (handler instanceof WithContextTextHttpResponseHandler) {
            context = ((WithContextTextHttpResponseHandler) handler).getContext();
        }

        ApiHttpClient.post(context, url, jsonString, handler);
    }

    /**
     * 5.3获取活动列表 ，测试用
     */
    public static void _getActivityInfos(String token,
                                         AsyncHttpResponseHandler handler) {
        if (StringUtil.isEmpty(token)) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);

        postRequest(params, "washingService-controller/wash/activity/getActivityInfos%s", handler);
    }

    /**
     * 2.5 收衣记录，测试用
     */
	/*返回：ResultCollectInfo*/
    public static void _collectInfoRecord(String token, long startDate, long endDate,
                                          AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        postRequest(params, "washingService-controller/wash/collectInfoRecord%s", handler);
    }


    /**
     * 5.8收衣记录，测试用
     */
	/*返回：ResultCollectInfo*/
    public static void _collectInfoRecord2(String token, long startDate, long endDate,
                                           AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        postRequest(params, "washingService-controller/wash/activity/collectInfoRecord%s", handler);
    }
}
