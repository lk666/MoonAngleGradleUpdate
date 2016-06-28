package cn.com.bluemoon.delivery.app.api;  

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;

import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ApiHttpClient {

	public static String HOST;
	public static String API_URL;
	public static String MOCK_URL = "http://angelapi.bluemoon.com.cn:8884/%s";
	public static String MOCK_URL2 = "http://tmallapi.bluemoon.com.cn:9002/mockjsdata/5/%s";
	public static String ADDRESS_URL="http://mallapi.bluemoon.com.cn/%s";
	public static String PUNCH_DETAILDS_DOMAIN;

	static {
		if (BuildConfig.RELEASE) {
			HOST = "angel.bluemoon.com.cn";
			API_URL = "http://angel.bluemoon.com.cn/%s";
			PUNCH_DETAILDS_DOMAIN = "http://mallapi.bluemoon.com.cn/%s";
		} else {
			HOST = "angelapi.bluemoon.com.cn"; // tmallapi.bluemoon.com.cn
			API_URL = "http://angelapi.bluemoon.com.cn:8882/%s"; // 172.16.49.23
			PUNCH_DETAILDS_DOMAIN = "http://tmallapi.bluemoon.com.cn/%s";
		}
	}
	
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static AsyncHttpClient client;

	public ApiHttpClient() {
	}

	public static AsyncHttpClient getHttpClient() {
		return client;
	}

	public static void cancelAll(Context context) {
		client.cancelRequests(context, true);
	}

	public static void get(String partUrl, AsyncHttpResponseHandler handler) {
		client.get(getAbsoluteApiUrl(partUrl), handler);
		log(new StringBuilder("GET ").append(partUrl).toString());
	}

	public static void get(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.get(getAbsoluteApiUrl(partUrl), params, handler);
		log(new StringBuilder("GET ").append(partUrl).append("&")
				.append(params).toString());
	}

	public static String getAbsoluteApiUrl(String partUrl) {
		String url = String.format(API_URL, partUrl);
		// LogUtils.d("BASE_CLIENT", "request:" + url);
		return url;
	}
	
	public static String getMockUrl(String partUrl) {
		return String.format(MOCK_URL2, partUrl);
	}

	public static String getApiUrl() {
		return API_URL;
	}

	public static void getDirect(String url, AsyncHttpResponseHandler handler) {
		client.get(url, handler);
		log(new StringBuilder("GET ").append(url).toString());
	}

	public static void log(String log) {
		LogUtils.d("BaseApi", log);
	}

	public static void post(String partUrl, AsyncHttpResponseHandler handler) {
		client.post(getAbsoluteApiUrl(partUrl), handler);
		log(new StringBuilder("POST ").append(partUrl).toString());
	}

	public static void post(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {

		client.post(getAbsoluteApiUrl(partUrl), params, handler);
		log(new StringBuilder("POST ").append(partUrl).append("&")
				.append(params).toString());
	}

	public static void post(Context context, String partUrl, String jsonString,
			AsyncHttpResponseHandler handler) {

		ByteArrayEntity entity = null;
		try {
			entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {

			// TODO Auto-generated catch block
			log(new StringBuilder("POST UnsupportedEncodingException ")
					.append(partUrl).append("----->").append(jsonString)
					.toString());
		}
		client.post(context, getAbsoluteApiUrl(partUrl), entity,
				"application/json", handler);

		log(new StringBuilder("POST ").append(partUrl).append("----->")
				.append(jsonString).toString());
	}
	
	public static void postMock(Context context, String partUrl, String jsonString,
			AsyncHttpResponseHandler handler) {

		ByteArrayEntity entity = null;
		try {
			entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {

			// TODO Auto-generated catch block
			log(new StringBuilder("POST UnsupportedEncodingException ")
					.append(partUrl).append("----->").append(jsonString)
					.toString());
		}
		client.post(context, getMockUrl(partUrl), entity,
				"application/json", handler);

		log(new StringBuilder("POST ").append(partUrl).append("----->")
				.append(jsonString).toString());
	}

	public static void postDirect(Context context, String partUrl,
			String jsonString, AsyncHttpResponseHandler handler) {

		ByteArrayEntity entity = null;
		try {
			entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {

			// TODO Auto-generated catch block
			log(new StringBuilder("POST UnsupportedEncodingException ")
					.append(partUrl).append("----->").append(jsonString)
					.toString());
		}
		client.post(context, partUrl, entity, "application/json", handler);

		log(new StringBuilder("POST ").append(partUrl).append("----->")
				.append(jsonString).toString());
	}

	public static void postDirect(String url, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.post(url, params, handler);
		log(new StringBuilder("POST ").append(url).append("&").append(params)
				.toString());
	}

	public static void put(String partUrl, AsyncHttpResponseHandler handler) {
		client.put(getAbsoluteApiUrl(partUrl), handler);
		log(new StringBuilder("PUT ").append(partUrl).toString());
	}

	public static void put(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.put(getAbsoluteApiUrl(partUrl), params, handler);
		log(new StringBuilder("PUT ").append(partUrl).append("&")
				.append(params).toString());
	}

	public static void setApiUrl(String apiUrl) {
		API_URL = apiUrl;
	}

	public static void setHttpClient(AsyncHttpClient c) {
		client = c;
		client.addHeader("Accept-Language", Locale.getDefault().toString());
		client.addHeader("Host", HOST);
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
  
