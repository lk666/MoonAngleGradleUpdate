package cn.com.bluemoon.delivery;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.WindowManager;

import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import java.util.Properties;
import java.util.UUID;

import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.common.AppConfig;
import cn.com.bluemoon.delivery.utils.service.LocationService;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;

public class AppContext extends BaseApplication {

    public static final int PAGE_SIZE = 10;

    private static AppContext instance;
    public LocationService locationService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        initLogin();
    }

    public static AppContext getInstance() {
        return instance;
    }

    private void initLogin() {

    }

    private void init() {

        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        client.setConnectTimeout(20000);
        client.setResponseTimeout(20000);
        ApiHttpClient.setHttpClient(client);
        ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));

        locationService = new LocationService(getApplicationContext());
        locationService.stop();
        LocationClientOption mOption = locationService.getDefaultLocationClientOption();
        locationService.setLocationOption(mOption);
        locationService.registerListener();

        shareInit();

        ImageLoaderUtil.init(this, Constants.PATH_CACHE, !BuildConfig.RELEASE);
    }

    private void shareInit() {

    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }


    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }


    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtil.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    public int getDisplayWidth() {
        return ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

}
  
