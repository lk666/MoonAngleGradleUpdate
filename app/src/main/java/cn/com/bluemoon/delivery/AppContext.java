package cn.com.bluemoon.delivery;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.Properties;
import java.util.UUID;

import bluemoon.com.lib_x5.X5SDk;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.app.api.EasySSLSocketFactory;
import cn.com.bluemoon.delivery.common.AppConfig;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.db.manager.DBHelper;
import cn.com.bluemoon.delivery.module.track.TrackManager;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.NetWorkUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;
import cn.com.bluemoon.lib_iflytek.utils.SpeechUtil;
import cn.com.bluemoon.liblog.NetLogUtils;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;

public class AppContext extends BaseApplication {

    public static final int PAGE_SIZE = 10;

    private static AppContext instance;

    /**
     * 当前Acitity个数
     */
    private int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    public static AppContext getInstance() {
        return instance;
    }


    private void init() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        FileUtil.init();

        //初始化数据库
        DBHelper.getInstance();

        //监听Activity的进程
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

        PlatformConfig.setWeixin("wx3b6e66b753fd84c2", "DSF23FewrwerE2342378934ds4879877");
        PlatformConfig.setSinaWeibo("4090679472", "e8d1ffe1012a89cb7e34a353d3693990","");
        PlatformConfig.setQQZone("1104979860", "Qkg4yWZ5Gr07K0K5");
        UMShareAPI.get(this);


        //X5内核WebView初始化
        X5SDk.init(this, BuildConfig.RELEASE);
        //初始化科大讯飞语音合成sdk
        SpeechUtil.init(this,FileUtil.getPathCache(),BuildConfig.RELEASE);

        ImageLoaderUtil.init(AppContext.getInstance(), FileUtil.getPathCache(), !BuildConfig.RELEASE);

        // Log
        cn.com.bluemoon.liblog.LogUtils.init("MoonAngle", 5, !BuildConfig.RELEASE);
        NetLogUtils.init(FileUtil.getPathTemp(), !BuildConfig.RELEASE);

        SchemeRegistry supportedSchemes = new SchemeRegistry();
        // Register the "http" and "https" protocol schemes, they are
        // required by the default operator to look up socket factories.
        supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        supportedSchemes.register(new Scheme("https", new EasySSLSocketFactory(), 443));
        AsyncHttpClient client = new AsyncHttpClient(supportedSchemes);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(AppContext.getInstance());
        client.setCookieStore(myCookieStore);
        client.setConnectTimeout(20000);
        client.setResponseTimeout(20000);
        ApiHttpClient.setHttpClient(client);
        ApiHttpClient.setCookie(ApiHttpClient.getCookie(AppContext.getInstance()));

        initUserAgent();
    }

    private void initUserAgent(){
        if(TextUtils.isEmpty(ClientStateManager.getUserAgent())){
            ClientStateManager.setUserAgent(NetWorkUtil.getUserAgent(this));
        }
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
    public int getDisplayHeight() {
        return ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();
    }

    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            /*if (activityCount == 0) {
                //app回到前台
                LogUtils.d("=====>onActivityStarted==>"+activity.getLocalClassName());
            }*/
            activityCount++;

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCount--;
            if (activityCount == 0) {
                //app退出前台
//				LogUtils.d("=====>onActivityStopped==>" + activity.getLocalClassName());
//                保存待上传埋点数据
                TrackManager.uploadNewTracks();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            /*if (activityCount == 0) {
                //app退出
                LogUtils.d("=====>onActivityDestroyed==>"+activity.getLocalClassName());
            }*/
        }
    };
}
  
