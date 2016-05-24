package cn.com.bluemoon.delivery.app.api;  

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;

public class ApiClientHelper {

	public static String CLIENT = "android";
	private static String FORMAT = "json";
	private static String APP_TYPE = "moonAngel";

	public static String getUserAgent(AppContext appContext) {
		StringBuilder ua = new StringBuilder("BMhouse");
		ua.append('/' + appContext.getPackageInfo().versionName + '_'
				+ appContext.getPackageInfo().versionCode);
		ua.append("/android");
		ua.append("/" + android.os.Build.VERSION.RELEASE);
		ua.append("/" + android.os.Build.MODEL);
		ua.append("/" + appContext.getAppId());
		return ua.toString();
	}

	

	public static synchronized String getParamUrl() {
		String timeStamp = PublicUtil.getCurrentTimeStamp();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String[] arrays = { Constants.PRIVATE_KEY ,CLIENT, 
				AppContext.getInstance().getAppId(),FORMAT,timeStamp,
				AppContext.getInstance().getPackageInfo().versionName,
				 Constants.PRIVATE_KEY };
		String sign = PublicUtil.genApiSign(arrays);
		params.add(new BasicNameValuePair("client", CLIENT));
		params.add(new BasicNameValuePair("cuid", AppContext.getInstance()
				.getAppId()));
		params.add(new BasicNameValuePair("version", AppContext.getInstance()
				.getPackageInfo().versionName));
		params.add(new BasicNameValuePair("format", FORMAT));
		params.add(new BasicNameValuePair("time", timeStamp));
		params.add(new BasicNameValuePair("appType", APP_TYPE));
		params.add(new BasicNameValuePair("lng", ClientStateManager.getLongitude(AppContext.getInstance().getApplicationContext())));
		params.add(new BasicNameValuePair("lat", ClientStateManager.getLatitude(AppContext.getInstance().getApplicationContext())));
		params.add(new BasicNameValuePair("hig", ClientStateManager.getAltitude(AppContext.getInstance().getApplicationContext())));
		return GetUrlParam(params, sign);
	}

	private static String GetUrlParam(List<NameValuePair> params, String sign) {
		StringBuilder sb = new StringBuilder("?");

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("sign=");
		sb.append(sign);

		return sb.toString();
	}
	
}
  
