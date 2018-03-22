package cn.com.bluemoon.delivery.app.api;

import android.annotation.SuppressLint;
import android.os.Build;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;

public class ApiClientHelper {

    public static final String CLIENT = "android";
    private static final String FORMAT = "json";
    private static final String APP_TYPE = "moonAngel";

    @SuppressLint("DefaultLocale")
    public static String getUserAgent(AppContext appContext) {
        return String.format("BMhouse/%s_%d/android/%s/%s/%s", appContext.getPackageInfo()
                .versionName, appContext.getPackageInfo().versionCode, android.os.Build.VERSION
                .RELEASE, android.os.Build.MODEL, appContext.getAppId());
    }

    public static synchronized String getParamUrl() {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        List<NameValuePair> params = new ArrayList<>();
        String[] arrays = {Constants.PRIVATE_KEY, CLIENT,
                AppContext.getInstance().getAppId(), FORMAT, timeStamp,
                AppContext.getInstance().getPackageInfo().versionName,
                Constants.PRIVATE_KEY};
        String sign = PublicUtil.genApiSign(arrays);
        params.add(new BasicNameValuePair("client", CLIENT));
        params.add(new BasicNameValuePair("cuid", AppContext.getInstance()
                .getAppId()));
        params.add(new BasicNameValuePair("version", AppContext.getInstance()
                .getPackageInfo().versionName));
        params.add(new BasicNameValuePair("sysversion", Build.VERSION.RELEASE));
        params.add(new BasicNameValuePair("format", FORMAT));
        params.add(new BasicNameValuePair("time", timeStamp));
        params.add(new BasicNameValuePair("appType", APP_TYPE));
        params.add(new BasicNameValuePair("lng", ClientStateManager.getLongitude()));
        params.add(new BasicNameValuePair("lat=", ClientStateManager.getLatitude()));
        params.add(new BasicNameValuePair("hig", ClientStateManager.getAltitude()));
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
  
