package cn.com.bluemoon.delivery.module.account.model;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by bm on 2016/8/1.
 */
public interface ILoginModel {

    String getUserName();

    void login(String name, String psw, AsyncHttpResponseHandler handler);

    void saveData(String jsonString,String name);

    void clearData();

}
