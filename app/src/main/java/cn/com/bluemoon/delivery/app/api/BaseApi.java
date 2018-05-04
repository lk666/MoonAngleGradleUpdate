package cn.com.bluemoon.delivery.app.api;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;
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
            //                        ApiHttpClient.postMock(context, url, jsonString, handler);
            ApiHttpClient.postNewBase(name, context, url, jsonString, handler);
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
            ApiHttpClient.postNewMock(name, context, url, jsonString, handler);
        } catch (Exception ex) {
            ViewUtil.longToast("系统配置发生变化，请重新启动应用。");
        }
    }

    protected static boolean isEmpty(String... str) {
        for (String s : str) {
            if (TextUtils.isEmpty(s)) {
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

    protected static void onError(AsyncHttpResponseHandler handler) {
        if (handler != null)
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param)));
    }
}
