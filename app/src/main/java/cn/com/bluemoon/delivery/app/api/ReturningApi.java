package cn.com.bluemoon.delivery.app.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.List;
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
     *2.1扫描衣物
     * @param clothesCode 衣物编码 String
     * @param token 登录凭证(必填) String
     */
    public static void scanClothes(String clothesCode,String token,AsyncHttpResponseHandler handler){
        if(null == clothesCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesCode",clothesCode);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/cabinet/scanClothes%s", handler);
    }

    /**
     *2.2扫描分拨柜
     * @param clothesCode 衣物编码 String
     * @param cupboardCode 分拨柜号 String
     * @param token 登录凭证(必填) String
     */
    public static void scanCupboard(String clothesCode,String cupboardCode,String token,AsyncHttpResponseHandler handler){
        if(null == clothesCode||null == cupboardCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesCode",clothesCode);
        params.put("cupboardCode",cupboardCode);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/cabinet/scanCupboard%s", handler);
    }

    /**
     *5.1获取待装车列表
     * @param pageFlag 分页时间戳(分页标志) int
     * @param token 登录凭证(必填) String
     */
    public static void queryWaitLoadList(long pageFlag,String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFlag",pageFlag);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryWaitLoadList%s", handler);
    }

    /**
     *5.2获取确认运输列表
     * @param carriageCode 承运单号（必填） String
     * @param token 登录凭证(必填) String
     */
    public static void queryTransportList(String carriageCode,String token,AsyncHttpResponseHandler handler){
        if(null == carriageCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("carriageCode",carriageCode);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryTransportList%s", handler);
    }

    /**
     *5.3装车完成
     * @param boxCodeList 衣物箱号（必填） List<String>
     * @param carriageCode 承运单号（必填） String
     * @param token 登录凭证(必填) String
     */
    public static void loadComplete(List<String> boxCodeList,String carriageCode,String token,AsyncHttpResponseHandler handler){
        if(null == boxCodeList||null == carriageCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("boxCodeList",boxCodeList);
        params.put("carriageCode",carriageCode);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/loadComplete%s", handler);
    }

    /**
     *5.4获取承运单详情
     * @param carriageCode 承运单号（必填） String
     * @param token 登录凭证(必填) String
     */
    public static void queryCarriageDetail(String carriageCode,String token,AsyncHttpResponseHandler handler){
        if(null == carriageCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("carriageCode",carriageCode);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryCarriageDetail%s", handler);
    }

    /**
     *5.5获取待送达列表
     * @param pageFlag 分页时间戳(分页标志) int
     * @param token 登录凭证(必填) String
     */
    public static void queryWaitSendList(long pageFlag,String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFlag",pageFlag);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryWaitSendList%s", handler);
    }

    /**
     *5.6确认收货人(扫描)
     * @param angleCode 员工编号（必填） String
     * @param carriageAddressId 承运单明细id（必填） String
     * @param tagCodeList 封箱条码（必填） List<String>
     * @param token 登录凭证(必填) String
     */
    public static void scanReceiver(String angleCode,String carriageAddressId,List<String> tagCodeList,String token,AsyncHttpResponseHandler handler){
        if(null == angleCode||null == carriageAddressId||null == tagCodeList||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("angleCode",angleCode);
        params.put("carriageAddressId",carriageAddressId);
        params.put("tagCodeList",tagCodeList);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/scanReceiver%s", handler);
    }

    /**
     *5.7获取承运历史列表
     * @param pageFlag 分页时间戳(分页标志) int
     * @param receiverSignTime 收货时间 int
     * @param token 登录凭证(必填) String
     */
    public static void queryCarriageHistoryList(long pageFlag,int receiverSignTime,String token,AsyncHttpResponseHandler handler){
        if(null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFlag",pageFlag);
        params.put("receiverSignTime",receiverSignTime);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryCarriageHistoryList%s", handler);
    }

    /**
     *5.8获取承运单历史详情
     * @param carriageCode 承运单号（必填） String
     * @param token 登录凭证(必填) String
     */
    public static void queryCarriageHistoryDetail(String carriageCode,String token,AsyncHttpResponseHandler handler){
        if(null == carriageCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("carriageCode",carriageCode);
        params.put(TOKEN,token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryCarriageHistoryDetail%s", handler);
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
