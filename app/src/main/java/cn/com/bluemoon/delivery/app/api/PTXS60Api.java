package cn.com.bluemoon.delivery.app.api;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
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
}
