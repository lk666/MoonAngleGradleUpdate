package cn.com.bluemoon.delivery.app.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class ApiHttpClient {

    public static AsyncHttpClient client;

    public ApiHttpClient() {
    }

    /**
     * 设置host，并返回域名地址
     */
    public static String getAbsoluteApiUrl(String partUrl) {
        client.addHeader("Host", BuildConfig.HOST);
        return String.format(BuildConfig.API_URL, partUrl);
    }

    /**
     * 获取埋点域名地址
     */
    public static String getTrackApiUrl() {
        client.addHeader("Host", BuildConfig.TRACK_HOST);
        return BuildConfig.API_TRACK_URL;
    }

    /**
     * 获取模拟数据域名地址
     */
    public static String getMockUrl(String partUrl) {
        client.addHeader("Host", BuildConfig.HOST);
        return String.format(BuildConfig.MOCK_URL, partUrl);
    }

    public static void post(Context context, String partUrl, String jsonString,
                            AsyncHttpResponseHandler handler) {
        client.post(context, getAbsoluteApiUrl(partUrl), getEntity(jsonString),
                "application/json", handler);
        log(new StringBuilder("POST ").append(partUrl).append("----->")
                .append(jsonString).toString());
    }

    /**
     * 模拟数据post请求
     */
    public static void postTrack(Context context, String jsonString,
                                 WithStatusTextHttpResponseHandler handler) {
        client.post(context, getTrackApiUrl(), getEntity(jsonString), "application/json", handler);
        log(new StringBuilder("POST ").append("").append("----->")
                .append(jsonString).toString());
    }

    /**
     * post数据，debug时可将日志写入临时文件，在再次启动时会被清除
     *
     * @param name 接口名称，可为空
     */
    public static void postNewBase(String name, Context context, String partUrl, String jsonString,
                                   WithContextTextHttpResponseHandler handler) {
        client.post(context, getAbsoluteApiUrl(partUrl), getEntity(jsonString),
                "application/json", handler);
        NetLogUtils.dNetRequest(Constants.TAG_HTTP_REQUEST, handler.getUuid(),
                partUrl, name, jsonString);
    }

    public static void postMock(Context context, String partUrl, String jsonString,
                                AsyncHttpResponseHandler handler) {
        client.post(context, getMockUrl(partUrl), getEntity(jsonString), "application/json",
                handler);
        log(new StringBuilder("POST ").append(partUrl).append("----->")
                .append(jsonString).toString());
    }

    /**
     * mock数据，debug时可将日志写入临时文件，在再次启动时会被清除
     *
     * @param name 接口名称，可为空
     */
    public static void postNewMock(String name, Context context, String partUrl, String jsonString,
                                WithContextTextHttpResponseHandler handler) {
        client.post(context, getMockUrl(partUrl), getEntity(jsonString), "application/json",
                handler);
        NetLogUtils.dNetRequest(Constants.TAG_HTTP_REQUEST, handler.getUuid(),
                partUrl, name, jsonString);
    }

    public static void setHttpClient(AsyncHttpClient c) {
        client = c;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host", BuildConfig.HOST);//默认host
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

    public static void log(String log) {
        LogUtils.d("BaseApi", log);
    }

    public static AsyncHttpClient getHttpClient() {
        return client;
    }

    public static void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

    public static ByteArrayEntity getEntity(String jsonString) {
        try {
            return new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
  
