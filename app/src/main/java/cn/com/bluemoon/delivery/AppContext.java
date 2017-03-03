package cn.com.bluemoon.delivery;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.WindowManager;

import com.baidu.location.LocationClientOption;
import com.bluemoon.umengshare.ShareHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.util.Properties;
import java.util.UUID;

import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.common.AppConfig;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.card.alarm.Reminds;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.download.BMDownloadManager;
import cn.com.bluemoon.delivery.utils.service.LocationService;
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
        initAlarm();
        ShareHelper.iniShare(this);
        initX5Environment();
    }

    private void initX5Environment() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //TbsDownloader.needDownload(getApplicationContext(), false);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                LogUtils.e("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub

            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                LogUtils.d("app","onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                LogUtils.d("app","onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                LogUtils.d("app","onDownloadProgress:"+i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    public static AppContext getInstance() {
        return instance;
    }

    private void initLogin() {

    }
    private void initAlarm() {
        try {
            if(!StringUtil.isEmptyString(ClientStateManager.getLoginToken())) {
                Reminds.SynAlarm(this);
            }
        }catch (Exception ex){
            LogUtils.e("AppContext","Syn Alarms Error",ex);
        }
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

        ImageLoaderUtil.init(this, FileUtil.getPathCache(), !BuildConfig.RELEASE);
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
  
