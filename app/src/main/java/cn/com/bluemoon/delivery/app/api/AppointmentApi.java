package cn.com.bluemoon.delivery.app.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.UploadAppointClothesInfo;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 预约收衣
 * Created by lk on 2016/9/18.
 */
public class AppointmentApi extends DeliveryApi {

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

    /**
     * 7.1已分派的预约单列表
     * @param token     登录凭证(必填) String
     */
    public static void appointmentQueryList(String token,
                                            AsyncHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/appointment/appointmentQueryList%s",
                handler);
    }

    /**
     * 7.7订单收衣单详情
     *
     * @param collectCode 收衣单号(必填) String
     * @param token       登录凭证(必填) String
     */
    public static void appointmentCollectDetail(String collectCode, String token,
                                                AsyncHttpResponseHandler handler) {
        if (null == collectCode || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == collectCode ? " null=collectCode" : "") + (null ==
                            token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("collectCode", collectCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/appointment/appointmentCollectDetail" +
                "%s", handler);
    }

    /**
     * 7.2预约单接单
     *
     * @param appointmentCode 预约单号 String
     * @param token           登录凭证(必填) String
     */
    public static void appointmentReceived(String appointmentCode, String token,
                                           AsyncHttpResponseHandler handler) {
        if (null == appointmentCode || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == appointmentCode ? " null=appointmentCode" : "") +
                            (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appointmentCode", appointmentCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/appointment/appointmentReceived%s",
                handler);
    }


    /**
     * 7.5预约收衣登记 保存（整合收衣记录）
     *
     * @param appointBackTime 预约还衣时间 int
     * @param appointmentCode 预约单号 String
     * @param clothesInfo     衣物信息
     * @param collectBrcode   收衣单条码 String
     * @param isFree          是否收费（1.收费，0免费）(必填) int
     * @param isUrgent        是否加急(1,加急， 0 不加急)(必填) int
     * @param token           登录凭证(必填) String
     */
    public static void appointmentCollectSave(long appointBackTime, String appointmentCode,
                                              List<UploadAppointClothesInfo> clothesInfo,
                                              String collectBrcode, int isFree, int isUrgent,
                                              String token, AsyncHttpResponseHandler handler) {
        if (null == appointmentCode || null == clothesInfo || null == collectBrcode
                || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == appointmentCode ? " null=appointmentCode" : "") +
                            (null == clothesInfo ? " null=clothesInfo" : "")
                            + (null == collectBrcode ? " " + "null=collectBrcode" : "")
                            + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appointBackTime", appointBackTime);
        params.put("appointmentCode", appointmentCode);
        params.put("clothesInfo", clothesInfo);
        params.put("collectBrcode", collectBrcode);
        params.put("isFree", isFree);
        params.put("isUrgent", isUrgent);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/appointment/appointmentCollectSave%s",
                handler);
    }

    /**
     * 7.3至尊洗衣一级分类列表
     *
     * @param token 登录凭证(必填) String
     */
    public static void oneLevelTypeList(String token, AsyncHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR,
                    new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/appointment/oneLevelTypeList%s",
                handler);
    }

    /**
     * 7.4至尊洗衣商品列表
     *
     * @param oneLevelCode 一级分类编码(必填) String
     * @param token        登录凭证(必填) String
     */
    public static void washGoodsList(String oneLevelCode, String token, AsyncHttpResponseHandler
            handler) {
        if (null == oneLevelCode || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1], null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == oneLevelCode ? " null=oneLevelCode" : "") + (null ==
                            token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("oneLevelCode", oneLevelCode);
        params.put(TOKEN, token);
        postRequest(params, "washingService-controller/wash/appointment/washGoodsList%s", handler);
    }

    /**
     * 7.8预约单收衣扫一扫
     *
     * @param queryCode 扫一扫预约单号 String
     * @param token     登陆凭证 String
     */
    public static void appointmentOrderScan(String queryCode, String token,
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
        postRequest(params, "washingService-controller/wash/appointment/appointmentOrderScan%s",
                handler);
    }
}
