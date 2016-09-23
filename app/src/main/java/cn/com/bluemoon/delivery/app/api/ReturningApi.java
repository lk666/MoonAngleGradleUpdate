package cn.com.bluemoon.delivery.app.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Decoder.BASE64Encoder;
import cn.com.bluemoon.delivery.module.wash.returning.manager.model.ImageInfo;

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
     * 5.1获取待装车列表
     *
     * @param pageFlag 分页时间戳(分页标志) int
     * @param token    登录凭证(必填) String
     */
    public static void queryWaitLoadList(long pageFlag, String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFlag", pageFlag);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryWaitLoadList%s", handler);
    }

    /**
     * 5.2获取确认运输列表
     *
     * @param carriageCode 承运单号（必填） String
     * @param token        登录凭证(必填) String
     */
    public static void queryTransportList(String carriageCode, String token, AsyncHttpResponseHandler handler) {
        if (null == carriageCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("carriageCode", carriageCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryTransportList%s", handler);
    }

    /**
     * 5.3装车完成
     *
     * @param boxCodeList  衣物箱号（必填） List<String>
     * @param carriageCode 承运单号（必填） String
     * @param token        登录凭证(必填) String
     */
    public static void loadComplete(List<String> boxCodeList, String carriageCode, String token, AsyncHttpResponseHandler handler) {
        if (null == boxCodeList || null == carriageCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("boxCodeList", boxCodeList);
        params.put("carriageCode", carriageCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/driverCarrier/loadComplete%s", handler);
    }

    /**
     * 5.4获取承运单详情
     *
     * @param carriageCode 承运单号（必填） String
     * @param token        登录凭证(必填) String
     */
    public static void queryCarriageDetail(String carriageCode, String token, AsyncHttpResponseHandler handler) {
        if (null == carriageCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("carriageCode", carriageCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryCarriageDetail%s", handler);
    }

    /**
     * 5.5获取待送达列表
     *
     * @param pageFlag 分页时间戳(分页标志) int
     * @param token    登录凭证(必填) String
     */
    public static void queryWaitSendList(long pageFlag, String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFlag", pageFlag);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryWaitSendList%s", handler);
    }

    /**
     * 5.6确认收货人(扫描)
     *
     * @param angleCode         员工编号（必填） String
     * @param carriageAddressId 承运单明细id（必填） String
     * @param tagCodeList       封箱条码（必填） List<String>
     * @param token             登录凭证(必填) String
     */
    public static void scanReceiver(String angleCode, String carriageAddressId, List<String> tagCodeList, String token, AsyncHttpResponseHandler handler) {
        if (null == angleCode || null == carriageAddressId || null == tagCodeList || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("angleCode", angleCode);
        params.put("carriageAddressId", carriageAddressId);
        params.put("tagCodeList", tagCodeList);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/driverCarrier/scanReceiver%s", handler);
    }

    /**
     * 5.7获取承运历史列表
     *
     * @param pageFlag         分页时间戳(分页标志) int
     * @param receiverSignTime 收货时间 int
     * @param token            登录凭证(必填) String
     */
    public static void queryCarriageHistoryList(long pageFlag, int receiverSignTime, String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFlag", pageFlag);
        params.put("receiverSignTime", receiverSignTime);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/driverCarrier/queryCarriageHistoryList%s", handler);
    }

    /**
     * 5.8获取承运单历史详情
     *
     * @param carriageCode 承运单号（必填） String
     * @param token        登录凭证(必填) String
     */
    public static void queryCarriageHistoryDetail(String carriageCode, String token, AsyncHttpResponseHandler handler) {
        if (null == carriageCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("carriageCode", carriageCode);
        params.put(TOKEN, token);
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

    /**
     * 9.1还衣管理-获取快递收货列表
     *
     * @param pageFalg 分页时间戳(分页标志) int
     * @param token    登录凭证(必填) String
     */
    public static void queryExpressReceiveList(long pageFalg, String token,
                                               AsyncHttpResponseHandler handler) {
        if (null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("pageFalg", pageFalg);
        params.put("token", token);
        postMockRequest(params,
                "washingService-controller/wash/backOrderManage/queryExpressReceiveList%s",
                handler);
    }

    /**
     * 9.4还衣管理-获取还衣列表
     *
     * @param backOrderType 还衣列表类型 String
     * @param pageFlag      分页时间戳(分页标志) int
     * @param signEndTime   签收结束时间 int
     * @param signStartTime 签收开始时间 int
     * @param token         登录凭证(必填) String
     */
    public static void queryBackOrderList(String backOrderType, long pageFlag, long signEndTime,
                                          long signStartTime, String token,
                                          AsyncHttpResponseHandler handler) {
        if (null == backOrderType || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("backOrderType", backOrderType);
        params.put("pageFlag", pageFlag);
        params.put("signEndTime", signEndTime);
        params.put("signStartTime", signStartTime);
        params.put("token", token);
        postMockRequest(params,
                "washingService-controller/wash/backOrderManage/queryBackOrderList%s", handler);
    }

    /**
     * 7.6查看物流
     *
     * @param companyCode 快递公司编号 String
     * @param expressCode 快递单号 String
     * @param token       登录凭证(必填) String
     */
    public static void seeExpress(String companyCode, String expressCode, String token,
                                  AsyncHttpResponseHandler handler) {
        if (null == companyCode || null == expressCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("companyCode", companyCode);
        params.put("expressCode", expressCode);
        params.put("token", token);
        postMockRequest(params, "washingService-controller/wash/express/seeExpress%s", handler);
    }

    /**
     * 9.5获取还衣单详情
     *
     * @param backOrderCode 还衣单号 String
     * @param token         登录凭证(必填) String
     */
    public static void queryBackOrderDetail(String backOrderCode, String token, AsyncHttpResponseHandler handler) {
        if (null == backOrderCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("backOrderCode", backOrderCode);
        params.put("token", token);
        postMockRequest(params, "washingService-controller/wash/backOrderManage/queryBackOrderDetail%s", handler);
    }

    /**
     *9.11还衣详情历史列表
     * @param backOrderCode 还衣单号 String
     * @param token 登录凭证(必填) String
     */
    public static void returnClothesHistoryList(String backOrderCode,String token,AsyncHttpResponseHandler handler){
        if(null == backOrderCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("backOrderCode",backOrderCode);
        params.put("token",token);
        postMockRequest(params, "washingService-controller/wash/backOrderManage/returnClothesHistoryList%s", handler);
    }


    /**
     * 9.10衣物详情
     *
     * @param clothesCode 衣物编码(必填) String
     * @param token       登录凭证(必填) String
     */
    public static void clothesDetail(String clothesCode, String token, AsyncHttpResponseHandler handler) {
        if (null == clothesCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesCode", clothesCode);
        params.put("token", token);
        postMockRequest(params, "washingService-controller/wash/backOrderManage/clothesDetail%s", handler);
    }

    /**
     * 9.8消费者拒签列表
     *
     * @param backOrderCode 还衣单号 String
     * @param token         登录凭证(必填) String
     */
    public static void refuseSignList(String backOrderCode, String token, AsyncHttpResponseHandler handler) {
        if (null == backOrderCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("backOrderCode", backOrderCode);
        params.put("token", token);
        postMockRequest(params, "washingService-controller/wash/backOrderManage/refuseSignList%s", handler);
    }

    /**
     * 9.9消费者拒签
     *
     * @param clothesCode     衣物编码 String
     * @param refuseImages    图片id List<object>
     * @param fileName        文件名称 String
     * @param imagePath       文件路径 String
     * @param refuseIssueDesc 拒签原因 String
     * @param refuseTagTime   拒签时间 String
     * @param token           登录凭证(必填) String
     */
    public static void refuseSign(String clothesCode, List<ImageInfo> refuseImages, String fileName, String imagePath, String refuseIssueDesc, String refuseTagTime, String token, AsyncHttpResponseHandler handler) {
        if (null == clothesCode || null == refuseImages || null == fileName || null == imagePath || null == refuseIssueDesc || null == refuseTagTime || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesCode", clothesCode);
        params.put("refuseImages", refuseImages);
        params.put("fileName", fileName);
        params.put("imagePath", imagePath);
        params.put("refuseIssueDesc", refuseIssueDesc);
        params.put("refuseTagTime", refuseTagTime);
        params.put("token", token);
        postMockRequest(params, "washingService-controller/wash/backOrderManage/refuseSign%s", handler);
    }

    /**
     * 9.12查看拒签单
     *
     * @param clothesCode 衣物编码 String
     * @param token       登录凭证(必填) String
     */
    public static void refuseSignDetail(String clothesCode, String token, AsyncHttpResponseHandler handler) {
        if (null == clothesCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("clothesCode", clothesCode);
        params.put("token", token);
        postMockRequest(params, "washingService-controller/wash/backOrderManage/refuseSignDetail%s", handler);
    }

    /**
     *9.2查看快递详情
     * @param expressCode 快递单号 String
     * @param token 登录凭证(必填 String
     */
    public static void seeExpressDetail(String expressCode,String token,AsyncHttpResponseHandler handler){
        if(null == expressCode||null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("expressCode",expressCode);
        params.put("token",token);
        postMockRequest(params, "washingService-controller/wash/backOrderManage/seeExpressDetail%s", handler);
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

    /**
     * 4.5待封箱-还衣单(扫描)
     *
     * @param backOrderCode 还衣单号 String
     * @param boxCode       衣物箱号 String
     * @param token         登录凭证(必填) String
     */
    public static void scanBackOrder(String backOrderCode, String boxCode, String token,
                                     AsyncHttpResponseHandler handler) {
        if (null == backOrderCode || null == boxCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("backOrderCode", backOrderCode);
        params.put("boxCode", boxCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/closeBox/scanBackOrder%s", handler);
    }

    /**
     * 4.3获取打印封箱条列表
     *
     * @param boxCode 衣物箱号 String
     * @param token   登录凭证(必填) String
     */
    public static void queryCloseBoxList(String boxCode, String token, AsyncHttpResponseHandler handler) {
        if (null == boxCode || null == token) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("boxCode", boxCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/closeBox/queryCloseBoxList%s", handler);
    }
}
