package cn.com.bluemoon.delivery.app.api;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.QueryInfo;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 企业收衣
 * Created by ljl on 2017/4/28.
 */
public class EnterpriseApi extends DeliveryApi {

    /**
     * 提交http请求
     *
     * @param params  参数列表
     * @param subUrl  请求的url子部
     * @param handler 回调
     */
    protected static void postRequest(Map<String, Object> params, String subUrl,
                                      AsyncHttpResponseHandler handler) {
        //TODO jl Mock 联调要删除这个方法
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(subUrl, ApiClientHelper.getParamUrl());

        Context context = AppContext.getInstance();
        if (handler instanceof WithContextTextHttpResponseHandler) {
            context = ((WithContextTextHttpResponseHandler) handler).getContext();
        }
        if (!BuildConfig.RELEASE) {
            ApiHttpClient.postMock(context, url, jsonString, handler);
        } else {
            ApiHttpClient.post(context, url, jsonString, handler);
        }
    }


    /**
     * 8.01企业收衣列表展示
     *
     * @param currentPage 当前页数 int
     * @param token       登录凭证(必填) String
     */
    public static void getWashEnterpriseList(int currentPage, String token, AsyncHttpResponseHandler
            handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", currentPage);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/getWashEnterpriseList%s",
                handler);
    }

    /**
     * 8.10企业收衣历史记录列表
     */
    public static void getWashEnterpriseRecordList(long timestamp, QueryInfo queryInfo, AsyncHttpResponseHandler
            handler) {
        Map<String, Object> params = new HashMap<>();
        params.put("queryInfo", queryInfo);
        params.put("timestamp", timestamp);
        postRequest(params, "washingService-controller/wash/enterprise/getWashEnterpriseRecordList%s", handler);
    }


    /**
     * 8.07企业收衣洗衣订单详情（查看和编辑）
     *
     * @param outerCode 洗衣订单编码 String
     * @param token     登录凭证(必填) String
     */
    public static void getWashEnterpriseDetail(String outerCode, String token,
                                               AsyncHttpResponseHandler
                                                       handler) {
        if (null == token || outerCode == null) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ? " null=token" : "")
                            + (null == outerCode ? " null=outerCode" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("outerCode", outerCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/getWashEnterpriseDetail%s" +
                "", handler);
    }

    /**
     * 8.12取消订单
     *
     * @param outerCode 洗衣订单编码 String
     * @param token     登录凭证(必填) String
     */
    public static void cancelWashEnterpriseOrder(String outerCode, String token, AsyncHttpResponseHandler
            handler) {
        if (null == token || outerCode == null) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ? " null=token" : "")
                            + (null == outerCode ? " null=outerCode" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("outerCode", outerCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/cancelWashEnterpriseOrder%s", handler);
    }

    /**
     * 8.11历史记录企业查询列表
     *
     * @param token 登录凭证(必填) String
     */
    public static void getEnterpriseRecordQuery(String token, AsyncHttpResponseHandler
            handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/getEnterpriseRecordQuery%s", handler);
    }

    /**
     * 8.02手动搜索列表展示
     *
     * @param queryCode 查询编码 String
     * @param token     登录凭证 String
     */
    public static void getWashEnterpriseQuery(String queryCode, String token,
                                              AsyncHttpResponseHandler handler) {
        if (null == queryCode || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == queryCode ? " null=queryCode" : "") + (null == token
                            ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("queryCode", queryCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/getWashEnterpriseQuery%s",
                handler);
    }
}
