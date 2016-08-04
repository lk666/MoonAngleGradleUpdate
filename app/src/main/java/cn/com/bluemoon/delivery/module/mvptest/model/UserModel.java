package cn.com.bluemoon.delivery.module.mvptest.model;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.common.ClientStateManager;

/**
 * Created by bm on 2016/8/1.
 */
public class UserModel implements IUserModel {


    @Override
    public void getUser(AsyncHttpResponseHandler handler) {
        DeliveryApi.getUserInfo(ClientStateManager.getLoginToken(), handler);
    }

    @Override
    public void logout(AsyncHttpResponseHandler handler) {
        DeliveryApi.logout(ClientStateManager.getLoginToken(), handler);
    }
}
