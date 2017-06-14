package cn.com.bluemoon.delivery.app.api;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * 适配new base接口基类
 * Created by lk on 2016/10/18.
 */
class BaseApi {
    static final String TOKEN = "token";

    /**
     * 提交http请求
     *
     * @param params  参数列表
     * @param subUrl  请求的url子部
     * @param handler 回调
     */
    @Deprecated
    static void postRequest(Map<String, Object> params, String subUrl,
                            WithContextTextHttpResponseHandler handler) {
        postRequest(null, params, subUrl, handler);
    }

    /**
     * 提交http请求
     *
     * @param name    请求的名称，可空
     * @param params  参数列表
     * @param subUrl  请求的url子部
     * @param handler 回调
     */
    static void postRequest(String name, Map<String, Object> params, String subUrl,
                            WithContextTextHttpResponseHandler handler) {
        try {
            String jsonString = JSONObject.toJSONString(params);
            String url = String.format(subUrl, ApiClientHelper.getParamUrl());

            Context context = handler.getContext();
            //            ApiHttpClient.postMock(name, context, url, jsonString, handler);
            ApiHttpClient.post(name, context, url, jsonString, handler);
        } catch (Exception ex) {
            ViewUtil.longToast("系统配置发生变化，请重新启动应用。");
        }
    }

    /**
     * 提交http请求
     *
     * @param name    请求的名称，可空
     * @param params  参数列表
     * @param subUrl  请求的url子部
     * @param handler 回调
     */
    static void postMockRequest(String name, Map<String, Object> params, String subUrl,
                                WithContextTextHttpResponseHandler handler) {
        try {
            String jsonString = JSONObject.toJSONString(params);
            String url = String.format(subUrl, ApiClientHelper.getParamUrl());

            Context context = handler.getContext();
            ApiHttpClient.postMock(name, context, url, jsonString, handler);
        } catch (Exception ex) {
            ViewUtil.longToast("系统配置发生变化，请重新启动应用。");
        }
    }

    protected static boolean isEmpty(String... str) {
        for (String s : str) {
            if (!StringUtils.isNoneBlank(s)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isEmptyList(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }
}
