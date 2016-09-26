package cn.com.bluemoon.delivery.sz.api;

import java.util.HashMap;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.common.ClientStateManager;

/**
 * Created by Wan.N
 * Date       2016/9/10
 * Desc       ${TODO}
 */
public class SzApiClientHelper {

    public static String CLIENT = "android";
    private static String FORMAT = "json";
    private static String APP_TYPE = "moonAngel";

    /**
     * 默认请求参数
     * appType [必填] app的类型 string
     * appversion	[必填] 版本号	string
     * deviceId	[必填] 设备号	string
     * hig	[选填] 高度	string
     * lat	[选填] 纬度	string
     * lng	[选填] 经度	string
     * platform	[必填] ios,android	string
     * token   [必填]
     */
    public static synchronized HashMap<String, String> getParamUrl() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appType", "moonAngel");
        params.put("appversion", AppContext.getInstance()
                .getPackageInfo().versionName);
        params.put("deviceId", AppContext.getInstance().getAppId());
        params.put("hig", ClientStateManager.getAltitude(AppContext.getInstance().getApplicationContext()));
        params.put("lat", ClientStateManager.getLatitude(AppContext.getInstance().getApplicationContext()));
        params.put("lng", ClientStateManager.getLongitude(AppContext.getInstance().getApplicationContext()));
        params.put("platform", CLIENT);
        params.put("token", ClientStateManager.getLoginToken());
        return params;
    }
}

