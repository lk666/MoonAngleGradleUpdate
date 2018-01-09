package cn.com.bluemoon.delivery.module.base.interf;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * DeliveryApi请求的回调处理封装，已整合公用逻辑
 * Created by bm on 2016/8/1.
 */
public interface IHttpResponse {

    /**
     * 请求成功
     */
    void onSuccessResponse(int requestCode, String jsonString, ResultBase result);

    /**
     * onsuccess时出现exception
     */
    void onSuccessException(int requestCode, Throwable t);

    /**
     * 请求失败
     */
    void onFailureResponse(int requestCode, Throwable t);

    /**
     * 请求返回非OK
     */
    void onErrorResponse(int requestCode, ResultBase result);
}
