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
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.RequestEnterpriseOrderInfo;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 企业收衣
 * Created by ljl on 2017/4/28.
 */
public class EnterpriseApi extends DeliveryApi {

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
    public static void getWashEnterpriseRecordList(long timestamp, QueryInfo queryInfo,
                                                   AsyncHttpResponseHandler
                                                           handler) {
        Map<String, Object> params = new HashMap<>();
        params.put("queryInfo", queryInfo);
        params.put("timestamp", timestamp);
        postRequest(params, "washingService-controller/wash/enterprise" +
                "/getWashEnterpriseRecordList%s", handler);
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
    public static void cancelWashEnterpriseOrder(String outerCode, String token,
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
        postRequest(params, "washingService-controller/wash/enterprise/cancelWashEnterpriseOrder" +
                "%s", handler);
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
        postRequest(params, "washingService-controller/wash/enterprise/getEnterpriseRecordQuery%s" +
                "", handler);
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

    /**
     * 8.06企业收衣提交扣款（确认无误）接口
     *
     * @param outerCode 洗衣订单编号 String
     * @param token     登录凭证 String
     */
    public static void payWashEnterpriseOrder(String outerCode, String token,
                                              AsyncHttpResponseHandler handler) {
        if (null == outerCode || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == outerCode ? " null=queryCode" : "") + (null == token
                            ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("outerCode", outerCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/payWashEnterpriseOrder%s",
                handler);
    }

    /**
     * 8.08企业收衣衣物保存接口
     *
     * @param outerCode 洗衣订单编号 String
     * @param washCode  商品编码 String
     * @param token     登录凭证 String
     */
    public static void saveWashClothes(String outerCode, String washCode, String token,
                                       AsyncHttpResponseHandler handler) {
        if (null == outerCode || null == token || null == washCode) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == outerCode ? " null=queryCode" : "") + (null == token
                            ? " null=token" : "") + (null == token
                            ? " null=washCode" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("outerCode", outerCode);
        params.put("washCode", washCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/saveWashClothes%s",
                handler);
    }

    /**
     * 8.03企业收衣扫一扫
     *
     * @param queryCode 查询编码 String
     * @param token     登陆凭证 String
     */
    public static void getWashEnterpriseScan(String queryCode, String token,
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
        postRequest(params, "washingService-controller/wash/enterprise/getWashEnterpriseScan%s",
                handler);
    }

    /**
     * 8.04企业收衣创建订单
     *
     * @param enterpriseOrderInfo 基本信息
     * @param token               登陆凭证 String
     */
    public static void saveWashEnterpriseOrder(RequestEnterpriseOrderInfo enterpriseOrderInfo,
                                               String token, AsyncHttpResponseHandler handler) {
        if (null == enterpriseOrderInfo || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == enterpriseOrderInfo ? " null=enterpriseOrderInfo" : "")
                            + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("enterpriseOrderInfo", enterpriseOrderInfo);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/saveWashEnterpriseOrder%s" +
                "", handler);
    }

    /**
     * 8.09企业收衣删除衣物
     *
     * @param clothesId 衣物id String
     * @param token     登陆凭证 String
     */
    public static void deleteClothes(String clothesId, String token, AsyncHttpResponseHandler
            handler) {
        if (null == clothesId || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == clothesId ? " null=clothesId" : "") + (null == token
                            ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesId", clothesId);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/deleteClothes%s", handler);
    }

    /**
     * 8.05企业收衣获取合作品类商品
     *
     * @param outerCode 订单编码 String
     * @param token     登陆凭证 String
     */
    public static void getCooperationList(String outerCode, String token,
                                          AsyncHttpResponseHandler handler) {
        if (null == outerCode || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == outerCode ? " null=outerCode" : "") + (null == token
                            ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("outerCode", outerCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/enterprise/getCooperationList%s",
                handler);
    }
}
