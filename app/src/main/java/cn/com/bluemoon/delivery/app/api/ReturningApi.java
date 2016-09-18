package cn.com.bluemoon.delivery.app.api;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Decoder.BASE64Encoder;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;

/**
 * 还衣API
 * Created by lk on 2016/9/18.
 */
public class ReturningApi extends DeliveryApi {
    // TODO: lk 2016/9/14 暂时全部用的是mock数据，过后删掉
    protected static void postRequest(Map<String, Object> params, String subUrl,
                                      AsyncHttpResponseHandler handler) {
        postMockRequest(params, subUrl, handler);
    }

    /**
     *1.1功能角标统计查询
     * @param token 登录凭证(必填) String
     * @param type 角标类型 String
     */
    public static void queryCornerNum(String token,String type,AsyncHttpResponseHandler handler){
        if(null == token||null == type) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN,token);
        params.put("type",type);
        postRequest(params, "washingService-controller/wash/queryCornerNum%s", handler);
    }

    /**
     *1.2上传异常图片（签名图片）
     * @param file 文件流
     * @param token 登录凭证(必填) String
     */
    public static void uploadExceptionImage(byte[] file,String token,AsyncHttpResponseHandler handler){
        if (null == token || null == file) {
            return;
        }

        BASE64Encoder encoder = new BASE64Encoder();
        String fileString = encoder.encode(file);

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("fileData", fileString);
        params.put("fileName", UUID.randomUUID() + ".png");
        postRequest(params, "washingService-controller/wash/uploadExceptionImage%s", handler);
    }

    /**
     *1.3获取打包区域列表
     * @param token 登录凭证(必填) String
     */
    public static void queryAreaList(String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/queryAreaList%s", handler);
    }

    /**
     * 4.1获取待封箱列表
     *
     * @param pageFalg  分页时间戳(分页标志) long
     * @param token     登录凭证(必填) String
     * @param waitInbox 待封箱 boolean
     */
    public static void queryWaitCloseBoxList(long pageFalg, String token, boolean waitInbox,
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
