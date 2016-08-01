package cn.com.bluemoon.delivery.base;

import android.content.Context;

import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 包含context的TextHttpResponseHandler
 * Created by lk on 2016/7/29.
 */
public abstract class WithContextTextHttpResponseHandler extends TextHttpResponseHandler {
    private Context context;

    public WithContextTextHttpResponseHandler(String encoding, Context context) {
        this.setCharset(encoding);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
