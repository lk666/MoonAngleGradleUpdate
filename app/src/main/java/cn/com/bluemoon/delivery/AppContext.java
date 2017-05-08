package cn.com.bluemoon.delivery;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.WindowManager;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.Properties;
import java.util.UUID;

import cn.com.bluemoon.delivery.common.AppConfig;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.StringUtil;

public class AppContext extends BaseApplication {

    public static final int PAGE_SIZE = 10;

    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        initLogin();
      //

        PlatformConfig.setWeixin("wx3b6e66b753fd84c2", "DSF23FewrwerE2342378934ds4879877");
        PlatformConfig.setSinaWeibo("4090679472", "e8d1ffe1012a89cb7e34a353d3693990","");
        PlatformConfig.setQQZone("1104979860", "Qkg4yWZ5Gr07K0K5");
        UMShareAPI.get(this);
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

    private void init() {

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
}
  
