package cn.com.bluemoon.delivery.app.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Decoder.BASE64Encoder;

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
     * 1.1功能角标统计查询
     *
     * @param token 登录凭证(必填) String
     * @param type  角标类型 String
     */
    public static void queryCornerNum(String token, String type, AsyncHttpResponseHandler handler) {
        if (null == token || null == type) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("type", type);
        postRequest(params, "washingService-controller/wash/queryCornerNum%s", handler);
    }

    /**
     * 1.2上传异常图片（签名图片）
     *
     * @param file  文件流
     * @param token 登录凭证(必填) String
     */
    public static void uploadExceptionImage(byte[] file, String token, AsyncHttpResponseHandler
            handler) {
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
     * 1.3获取打包区域列表
     *
     * @param token 登录凭证(必填) String
     */
    public static void queryAreaList(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/queryAreaList%s", handler);
    }

    /**
     * 2.1扫描衣物
     *
     * @param clothesCode 还衣单号 String
     * @param clothesCode 衣物编码 String
     * @param token       登录凭证(必填) String
     */
    public static void scanClothes(String clothesCode, String token, AsyncHttpResponseHandler
            handler) {
        if (null == clothesCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesCode", clothesCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/cabinet/scanClothes%s", handler);
    }

    /**
     * 2.2扫描分拨柜
     *
     * @param clothesCode  衣物编码 String
     * @param cupboardCode 分拨柜号 String
     * @param token        登录凭证(必填) String
     */
    public static void scanCupboard(String clothesCode, String cupboardCode, String token,
                                    AsyncHttpResponseHandler handler) {
        if (null == clothesCode || null == cupboardCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesCode", clothesCode);
        params.put("cupboardCode", cupboardCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/cabinet/scanCupboard%s", handler);
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

    /**
     * 4.6获取封箱条码历史列表
     *
     * @param opTime   封箱时间 long
     * @param pageFalg 分页时间戳(分页标志) long
     * @param token    登录凭证(必填) String
     */
    public static void queryCloseBoxHistoryList(long opTime, long pageFalg, String token,
                                                AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("opTime", opTime);
        params.put("pageFalg", pageFalg);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/closeBox/queryCloseBoxHistoryList%s",
                handler);
    }

    /**
     * 4.7获取封箱详情
     *
     * @param tagCode 封箱条码 String
     * @param token   登录凭证(必填) String
     */
    public static void queryCloseBoxDetail(String tagCode, String token, AsyncHttpResponseHandler
            handler) {
        if (null == tagCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("tagCode", tagCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/closeBox/queryCloseBoxDetail%s",
                handler);
    }

    /**
     * 4.2获取衣物箱还衣单列表(扫描)
     *
     * @param boxCode 衣物箱号(必填) String
     * @param token   登录凭证(必填) String
     */
    public static void queryClothesBoxBackOrderList(String boxCode, String token,
                                                    AsyncHttpResponseHandler handler) {
        if (null == boxCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("boxCode", boxCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/closeBox/queryClothesBoxBackOrderList" +
                "%s", handler);
    }
}
