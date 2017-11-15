package cn.com.bluemoon.delivery.utils.tencentX5;

import android.util.Log;


import com.loopj.android.http.Base64;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import com.tencent.smtt.sdk.WebView;


public class JsConnectManager {

    public static final String URL_ANGEL = "angel://moonMall";
    private static final String KEY_METHOD = "method";
    private static final String KEY_URL = "url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_VERSION = "version";
    private static final String KEY_CALLBACK = "callback";
    private static final String KEY_TOPIC = "topic";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_PIC = "picUrl";
    private static final String KEY_BACKFUNNAME = "backFunName";
    private static final String KEY_IDENTIFYSIDE = "identifySide";//身份证正反面标识

    private static final String VALUE_WEBVIEW = "webview";
    private static final String VALUE_SCAN = "scan";
    private static final String VALUE_CLOSEWEBVIEW = "closeWebView";
    private static final String VALUE_SETTITLE = "setTitle";
    private static final String VALUE_SHOWCUSTOMERSERVICE = "showCustomerService";
    private static final String VALUE_SETAPPINFO = "setAppInfo";
    private static final String VALUE_GET_LOCATION = "getLocation";
    private static final String VALUE_GET_CACHE = "getCache";
    private static final String VALUE_CLEAN_CACHE = "cleanCache";
    private static final String VALUE_LOGOUT = "logout";
    private static final String VALUE_SHARE = "share";
    private static final String VALUE_SETBACK = "setBack";
    //身份证识别
    private static final String VALUE_IDCARDIDENTIFY = "IDCardIdentify";
    //银行卡识别
    private static final String VALUE_BANKCARDIDENTIFY = "bankCardIdentify";

    public static HashMap<String, String> getBMJSParams(String url) {
        String arg = url.substring(url.indexOf("?") + 1, url.length());
        String[] strs = arg.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for (int x = 0; x < strs.length; x++) {
            if (strs[x].indexOf(":") > 0) {
                String key = strs[x].substring(0, strs[x].indexOf(":"));
                String value = strs[x].substring(strs[x].indexOf(":") + 1);
                if (!StringUtils.isEmpty(key) && value != null) {
                    value = new String(Base64.decode(value.getBytes(), Base64.DEFAULT));
                    Log.d("jsConnect", "result =" + key + "=" + value);
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    public static HashMap<String, String> getUrlParams(String url) {
        String arg = url.substring(url.indexOf("?") + 1, url.length());
        String[] strs = arg.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for (int x = 0; x < strs.length; x++) {
            if (strs[x].indexOf("=") > 0) {
                String key = strs[x].substring(0, strs[x].indexOf("="));
                String value = strs[x].substring(strs[x].indexOf("=") + 1);
                if (!StringUtils.isEmpty(key) && value != null) {
                    Log.d("jsConnect", "result =" + key + "=" + value);
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    public static boolean isHideTitleByUrl(String url) {
        HashMap<String, String> map = getUrlParams(url);
        if ("true".equals(map.get("isHideTitle"))) {
            return true;
        }
        return false;
    }

    public static boolean jsConnect(String start, WebView view, String url, JsConnectCallBack
            callBack) {
//		Log.d("jsConnect", url);
        if (url.startsWith(start)) {
            if (callBack == null)
                return true;
            Map<String, String> map = getBMJSParams(url);
            String method = map.get(KEY_METHOD);
            String callBackName = map.get(KEY_CALLBACK);
            if (VALUE_WEBVIEW.equals(method)) {
                callBack.webView(view, map.get(KEY_URL), map.get(KEY_TITLE), map.get(KEY_CALLBACK));
            } else if (VALUE_SCAN.equals(method)) {
                callBack.scan(view, map.get(KEY_TITLE), callBackName);
            } else if (VALUE_CLOSEWEBVIEW.equals(method)) {
                callBack.closeWebView(view, callBackName);
            } else if (VALUE_SETTITLE.equals(method)) {
                callBack.setTitle(view, map.get(KEY_TITLE));
            } else if (VALUE_SHOWCUSTOMERSERVICE.equals(method)) {
                callBack.showCustomerService();
            } else if (VALUE_SETAPPINFO.equals(method)) {
                callBack.cleanCache(view, map.get(KEY_VERSION));
                loadJavascript(view, callBackName, callBack.getAppInfo());
            } else if (VALUE_GET_LOCATION.equals(method)) {
                callBack.getLoaction(view, callBackName);
            } else if (VALUE_GET_CACHE.equals(method)) {
                loadJavascript(view, callBackName, callBack.getCacheSize(view));
            } else if (VALUE_CLEAN_CACHE.equals(method)) {
                callBack.cleanCache(view);
            } else if (VALUE_LOGOUT.equals(method)) {
                callBack.logout(view);
            } else if (VALUE_SHARE.equals(method)) {
                callBack.share(view, map.get(KEY_TOPIC), map.get(KEY_CONTENT), map.get(KEY_PIC),
                        map.get(KEY_URL));
            } else if (VALUE_SETBACK.equals(method)) {
                callBack.setBack(view, map.get(KEY_BACKFUNNAME), callBackName);
            } else if (VALUE_IDCARDIDENTIFY.equals(method)) {
                callBack.scanIDCardIdentify(view, map.get(KEY_IDENTIFYSIDE), callBackName);
            } else if (VALUE_BANKCARDIDENTIFY.equals(method)) {
                callBack.scanBankCardIdentify(view, callBackName);
            }
            return true;
        }
        return false;
    }

    public static void loadJavascript(WebView view, String method, String param) {
        if (StringUtils.isEmpty(method)) return;
        if (StringUtils.isEmpty(param)) {
            param = "";
        } else {
            param = "'" + param + "'";
        }
        String url = "javascript:" + method + "(" + param + ")";
        view.loadUrl(url);
        Log.d("jsConnect", "result =" + url);
    }

    public static void loadJavascript(WebView view, String method) {
        loadJavascript(view, method, null);
    }

}
