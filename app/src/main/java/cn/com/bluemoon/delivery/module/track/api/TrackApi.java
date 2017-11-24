package cn.com.bluemoon.delivery.module.track.api;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.db.entity.ReqBody;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * Created by bm on 2016/12/12.
 */
public class TrackApi {

    /**
     * 返回参数错误
     *
     * @param handler
     */
    protected static void showErrorHandler(AsyncHttpResponseHandler handler) {
        handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                new Exception(AppContext.getInstance().getString(R.string.error_local_param)));
    }


    /**
     * 数据埋点
     *
     * @param reqBody
     * @param handler
     */
    public static void postTrack(List<ReqBody> reqBody, WithStatusTextHttpResponseHandler handler) {
        if (reqBody == null) {
            showErrorHandler(handler);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("reqBody", reqBody);
        params.put("reqKey", "");
        String jsonString = JSONObject.toJSONString(params);
        Context context = AppContext.getInstance();
        ApiHttpClient.postTrack(context,"",jsonString,handler);
    }
}
