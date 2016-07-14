package cn.com.bluemoon.delivery.module.base;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

/**
 * 返回解析类型的{@link TextHttpResponseHandler}
 * Created by lk on 2016/7/13.
 */
public abstract class WithClassTextHttpResponseHandler extends AsyncHttpResponseHandler {
    private Class classType;

    public WithClassTextHttpResponseHandler(Class classType) {
        this("UTF-8", classType);
    }

    public WithClassTextHttpResponseHandler(String encoding, Class classType) {
        this.setCharset(encoding);
        this.classType = classType;
    }

    public abstract void onFailure(int var1, Header[] var2, String var3, Throwable var4);

    public abstract void onSuccess(int var1, Header[] var2, String var3, Class classType);

    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        this.onSuccess(statusCode, headers, getResponseString(responseBytes, this.getCharset()),
                classType);
    }

    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable
            throwable) {
        this.onFailure(statusCode, headers, getResponseString(responseBytes, this.getCharset()),
                throwable);
    }

    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            String e = stringBytes == null ? null : new String(stringBytes, charset);
            return e != null && e.startsWith("\ufeff") ? e.substring(1) : e;
        } catch (UnsupportedEncodingException var3) {
            AsyncHttpClient.log.e("WithClassTextHttpResponseHandler", "Encoding response into " +
                    "string failed", var3);
            return null;
        }
    }
}
