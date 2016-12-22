package cn.com.bluemoon.delivery.app.api;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.sz.util.Constants;

/**
 * 预约收衣
 * Created by lk on 2016/9/18.
 */
public class AppointmentApi extends DeliveryApi {
    /** todo 正式测试时删除提交到mock
     */
    protected static void postRequest(Map<String, Object> params, String subUrl,
                                      AsyncHttpResponseHandler handler) {
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(subUrl, ApiClientHelper.getParamUrl());

        Context context = AppContext.getInstance();
        if (handler instanceof WithContextTextHttpResponseHandler) {
            context = ((WithContextTextHttpResponseHandler) handler).getContext();
        }

        ApiHttpClient.postMock(context, url, jsonString, handler);
    }

    /**
     * 7.6预约收衣记录
     *
     * @param endDate   查询结束日期 long
     * @param startDate 查询开始日期 long
     * @param timestamp 分页标识 long
     * @param token     登录凭证(必填) String
     */
    public static void appointmentCollectList(long endDate, long startDate, long timestamp,
                                              String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param)
                            + ":" + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("endDate", endDate);
        params.put("startDate", startDate);
        params.put("timestamp", timestamp);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/appointment/appointmentCollectList%s",
                handler);
    }


}
