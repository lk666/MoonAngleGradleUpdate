package cn.com.bluemoon.delivery.app.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ByteArrayEntity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import Decoder.BASE64Encoder;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.track.api.WithStatusTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.lib.utils.LibFileUtil;
import cn.com.bluemoon.liblog.NetLogUtils;

public class ApiHttpClient {

    public static AsyncHttpClient client;

    public ApiHttpClient() {
    }

    public static AsyncHttpClient getHttpClient() {
        return client;
    }

    public static void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

    public static String getAbsoluteApiUrl(String partUrl) {
        return String.format(BuildConfig.API_URL, partUrl);
    }

    public static String getMockUrl(String partUrl) {
        return String.format(BuildConfig.MOCK_URL, partUrl);
    }

    public static void log(String log) {
        LogUtils.d("BaseApi", log);
    }

    public static void post(Context context, String partUrl, String jsonString,
                            AsyncHttpResponseHandler handler) {

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.addHeader("Host", BuildConfig.HOST);
        client.post(context, getAbsoluteApiUrl(partUrl), entity,
                "application/json", handler);

        log(new StringBuilder("POST ").append(partUrl).append("----->")
                .append(jsonString).toString());
    }

    /**
     * post数据，debug时可将日志写入临时文件，在再次启动时会被清除
     */
    public static void postTrack(Context context, String partUrl, String jsonString,
                                 WithStatusTextHttpResponseHandler handler) {

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.addHeader("Host", BuildConfig.TRACK_HOST);
        client.post(context, BuildConfig.API_TRACK_URL, entity, "application/json", handler);

        log(new StringBuilder("POST ").append(partUrl).append("----->")
                .append(jsonString).toString());
    }

    public static void postDirect(Context context, String partUrl,
                                  String jsonString, AsyncHttpResponseHandler handler) {

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context, partUrl, entity, "application/json", handler);

        log(new StringBuilder("POST ").append(partUrl).append("----->")
                .append(jsonString).toString());
    }

    /**
     * post数据，debug时可将日志写入临时文件，在再次启动时会被清除
     *
     * @param name 接口名称，可为空
     */
    public static void postNewBase(String name, Context context, String partUrl, String jsonString,
                            WithContextTextHttpResponseHandler handler) {

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.addHeader("Host", BuildConfig.HOST);
        client.post(context, getAbsoluteApiUrl(partUrl), entity,
                "application/json", handler);

        NetLogUtils.dNetRequest(Constants.TAG_HTTP_REQUEST, handler.getUuid(),
                partUrl, name, jsonString);
    }

    public static void postMock(Context context, String partUrl, String jsonString,
                                AsyncHttpResponseHandler handler) {

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.addHeader("Host", BuildConfig.HOST);
        client.post(context, getMockUrl(partUrl), entity,"application/json", handler);

        log(new StringBuilder("POST ").append(partUrl).append("----->")
                .append(jsonString).toString());
    }

    /**
     * mock数据，debug时可将日志写入临时文件，在再次启动时会被清除
     *
     * @param name 接口名称，可为空
     */
    public static void postMock(String name, Context context, String partUrl, String jsonString,
                                WithContextTextHttpResponseHandler handler) {
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.addHeader("Host", BuildConfig.HOST);
        client.post(context, getMockUrl(partUrl), entity, "application/json", handler);

        NetLogUtils.dNetRequest(Constants.TAG_HTTP_REQUEST, handler.getUuid(),
                partUrl, name, jsonString);
    }

    public static void setHttpClient(AsyncHttpClient c) {
        client = c;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host", BuildConfig.HOST);
        client.addHeader("Connection", "Keep-Alive");

        client.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        setUserAgent(ApiClientHelper.getUserAgent(AppContext.getInstance()));
    }

    public static void setUserAgent(String userAgent) {
        client.setUserAgent(userAgent);
    }

    public static void setCookie(String cookie) {
        client.addHeader("Cookie", cookie);
    }

    private static String appCookie;

    public static void cleanCookie() {
        appCookie = "";
    }

    public static String getCookie(AppContext appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");
        }
        return appCookie;
    }

}
  
