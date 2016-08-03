package cn.com.bluemoon.delivery.module.base.interf;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by bm on 2016/8/2.
 */
public interface BaseMainInterface extends DialogControl {
    AsyncHttpResponseHandler getMainHandler();
}
