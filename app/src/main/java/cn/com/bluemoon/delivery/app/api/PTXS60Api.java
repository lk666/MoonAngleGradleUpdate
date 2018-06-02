package cn.com.bluemoon.delivery.app.api;

import android.text.TextUtils;

import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.RequestOrderDetail;
import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultGetBaseInfo;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 拼团销售API
 */
public class PTXS60Api extends BaseApi {
    /**
     * 【07】 订单列表查询
     */
    public static void queryOrderList(int pageSize, long timestamp, String token,
                                      WithContextTextHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" +
                            (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageSize", pageSize);
        params.put("timestamp", timestamp);
        params.put(TOKEN, token);
        postRequest("【07】 订单列表查询", params, "bluemoon-control/sqMoonOrder/queryOrderList%s",
                handler);
    }

    /**
     * 【05】支付接口，并校验库存,返回payinfo
     *
     * @param paymentTransaction 支付流水号 String
     * @param platform           支付平台编码 String
     * @param token              当前用户token String
     */
    public static void pay(String paymentTransaction, String platform, String token,
                           WithContextTextHttpResponseHandler handler) {
        if (null == paymentTransaction || null == platform || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == paymentTransaction ? " " +
                            "null=paymentTransaction" : "") + (null == platform ?
                            " null=platform" : "") + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("paymentTransaction", paymentTransaction);
        params.put("platform", platform);
        params.put(TOKEN, token);
        postRequest("【05】支付接口，并校验库存,返回payinfo", params, "bluemoon-control/sqMoonOrder/pay%s",
                handler);
    }

    /**
     * 【06】订单列表发起支付查询
     *
     * @param orderCode 订单编号 String
     * @param token     当前用户token String
     */
    public static void rePay(String orderCode, String token, WithContextTextHttpResponseHandler
            handler) {
        if (null == orderCode || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == orderCode ? " null=orderCode" :
                            "") + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderCode", orderCode);
        params.put(TOKEN, token);
        postRequest("【06】订单列表发起支付查询", params, "bluemoon-control/sqMoonOrder/rePay%s", handler);
    }

    /**
     * 【01】根据token获取相关信息
     *
     * @param token 当前用户token String
     */
    public static void getBaseInfo(String token, WithContextTextHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" +
                            (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest("【01】根据token获取相关信息", params, "bluemoon-control/sqMoonOrder/getBaseInfo%s",
                handler);
    }

    /**
     * 【03】根据输入团购支数求和，返回对应的区间单价和总金额
     */
    public static void getUnitPriceByNum(long orderTotalNum, String token,
                                         WithContextTextHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" +
                            (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderTotalNum", orderTotalNum);
        params.put(TOKEN, token);
        postRequest("【03】根据输入团购支数求和，返回对应的区间单价和总金额", params,
                "bluemoon-control/sqMoonOrder/getUnitPriceByNum%s", handler);
    }

    /**
     * 【02】推荐人信息查询
     */
    public static void getRecommendInfo(String recommendCode, String token,
                                        WithContextTextHttpResponseHandler handler) {
        if (recommendCode == null || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == recommendCode ? " " +
                            "null=recommendCode" :
                            "") + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("recommendCode", recommendCode);
        params.put(TOKEN, token);
        postRequest("【02】推荐人信息查询", params,
                "bluemoon-control/sqMoonOrder/getRecommendInfo%s", handler);
    }


    /**
     * 【08】订单详情查看接口
     */
    public static void getOrderDetail(String orderCode, String token,
                                      WithContextTextHttpResponseHandler handler) {
        if (orderCode == null || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == orderCode ? " " +
                            "null=orderCode" : "") + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderCode", orderCode);
        params.put(TOKEN, token);
        postRequest("【08】订单详情查看接口", params,
                "bluemoon-control/sqMoonOrder/getOrderDetail%s", handler);
    }

    /**
     * 【04】订单结算按钮接口
     */
    public static void commitOrder(ResultGetBaseInfo.AddressInfoBean addressInfo,
                                   String mendianCode, ArrayList<RequestOrderDetail> orderDetail,
                                   String recommendCode, String recommendName,
                                   String storeCode, String token,
                                   WithContextTextHttpResponseHandler handler,
                                   String storeResourceInfo, String pinTuanCode, String
                                           pinTuanName) {
        if (addressInfo == null || orderDetail == null || pinTuanCode == null || pinTuanName ==
                null ||
                recommendCode == null || recommendName == null || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(
                            AppContext.getInstance().getString(R.string.error_local_param)
                                    + ":"
                                    + (null == addressInfo ? " " + "null=addressInfo" : "")
                                    + (null == orderDetail ? " " + "null=orderDetail" : "")
                                    + (null == pinTuanCode ? " " + "null=pinTuanCode" : "")
                                    + (null == pinTuanName ? " " + "null=pinTuanName" : "")
                                    + (null == recommendCode ? " " + "null=recommendCode" : "")
                                    + (null == recommendName ? " " + "null=recommendName" : "")
                                    + (null == token ? " null=token" : "")));
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("addressInfo", addressInfo);
        params.put("mendianCode", mendianCode);
        params.put("orderDetail", orderDetail);
        params.put("recommendCode", recommendCode);
        params.put("recommendName", recommendName);
        params.put("pinTuanCode", pinTuanCode);
        params.put("pinTuanName", pinTuanName);
        params.put("storeCode", storeCode);
        if (!TextUtils.isEmpty(storeResourceInfo)) {
            params.put("storeResourceInfo", storeResourceInfo);
        }
        params.put(TOKEN, token);
        postRequest("【04】订单结算按钮接口", params,
                "bluemoon-control/sqMoonOrder/commitOrder%s", handler);
    }
}
