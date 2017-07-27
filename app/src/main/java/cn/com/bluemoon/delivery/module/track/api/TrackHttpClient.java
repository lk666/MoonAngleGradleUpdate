package cn.com.bluemoon.delivery.module.track.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ByteArrayEntity;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.app.api.ApiClientHelper;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.liblog.LogUtils;
import cn.com.bluemoon.liblog.NetLogUtils;

/**
 * Created by bm on 2017/5/10.
 */

public class TrackHttpClient {

    public static AsyncHttpClient client;

    public TrackHttpClient(){

    }

    public static void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

    public static void log(String log) {
        LogUtils.d("BaseApi", log);
    }

    public static void setHttpClient(AsyncHttpClient c) {
        client = c;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host", BuildConfig.TRACK_HOST);
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

    public static String getCookie() {
        if (appCookie == null || appCookie == "") {
            appCookie = AppContext.getInstance().getProperty("cookie");
        }
        return appCookie;
    }

    /**
     * post数据，debug时可将日志写入临时文件，在再次启动时会被清除
     *
     * @param name 接口名称，可为空
     */
    public static void post(String name, Context context, String partUrl, String jsonString,
                            WithStatusTextHttpResponseHandler handler) {

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(Constants.TAG_HTTP_REQUEST,
                    new StringBuilder("ApiHttpClient.post Error：").append(e.getMessage())
                            .append("\n").append("UUID=")
                            .append(handler.getUuid()).append(", ").append(partUrl).append("----->")
                            .append(jsonString).toString());
        }
        client.post(context, BuildConfig.API_TRACK_URL, entity,
                "application/json", handler);

        NetLogUtils.dNetRequest(Constants.TAG_HTTP_REQUEST, handler.getUuid(),
                partUrl, name, jsonString);
    }

    public static void postTrack(String name, Context context, String jsonString,
                                 WithStatusTextHttpResponseHandler handler) {
        post(name,context, "", jsonString, handler);
    }
}
