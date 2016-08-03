package cn.com.bluemoon.delivery.module.base.interf;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/8/1.
 */
public interface IHandlerListener {

    void onSuccessResponse(int requestCode, String jsonString,ResultBase result);
    void onFailureResponse(int requestCode);
    void onErrorResponse(int requestCode, ResultBase result);
}
