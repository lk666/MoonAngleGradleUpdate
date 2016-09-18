package cn.com.bluemoon.delivery.app.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 还衣API
 * Created by lk on 2016/9/18.
 */
public class ReturningApi extends DeliveryApi {
    // TODO: lk 2016/9/14 暂时全部用的是mock数据

    /**
     * 4.1获取待封箱列表
     *
     * @param pageFalg  分页时间戳(分页标志) int
     * @param token     登录凭证(必填) String
     * @param waitInbox 待封箱 boolean
     */
    public static void queryWaitCloseBoxList(int pageFalg, String token, boolean waitInbox,
                                             AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFalg", pageFalg);
        params.put(TOKEN, token);
        params.put("waitInbox", waitInbox);
        postRequest(params, "washingService-controller/wash/closeBox/queryWaitCloseBoxList%s",
                handler);
    }

}
