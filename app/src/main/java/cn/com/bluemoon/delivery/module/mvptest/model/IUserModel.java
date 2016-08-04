package cn.com.bluemoon.delivery.module.mvptest.model;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by bm on 2016/8/1.
 */
public interface IUserModel {

    void getUser(AsyncHttpResponseHandler handler);

    void logout(AsyncHttpResponseHandler handler);

}
