package cn.com.bluemoon.delivery.module.account.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.common.ClientStateManager;

/**
 * Created by bm on 2016/8/1.
 */
public class LoginModel implements ILoginModel {


    @Override
    public String getUserName() {
        return ClientStateManager.getUserName(AppContext.getInstance());
    }

    @Override
    public void login(String name, String psw,AsyncHttpResponseHandler handler) {
        DeliveryApi.ssoLogin(0,name,psw,handler);
    }

    @Override
    public void saveData(String jsonString, String name) {
        ResultToken tokenResult = JSON.parseObject(jsonString,
                ResultToken.class);
        ClientStateManager.setLoginToken(AppContext.getInstance(), tokenResult.getToken());
        ClientStateManager.setUserName(AppContext.getInstance(), name);
        MobclickAgent.onProfileSignIn(name);
    }

    @Override
    public void clearData() {
        ClientStateManager.clearData(AppContext.getInstance());
    }

}
