package cn.com.bluemoon.delivery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baidu.location.LocationClientOption;
import com.igexin.sdk.PushManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HTTP;

import java.io.File;

import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.EasySSLSocketFactory;
import cn.com.bluemoon.delivery.app.api.model.ResultVersionInfo;
import cn.com.bluemoon.delivery.app.api.model.Version;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.delivery.module.card.alarm.Reminds;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.NetWorkUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.UpdateManager;
import cn.com.bluemoon.delivery.utils.service.LocationService;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;
import cn.com.bluemoon.lib.utils.LibFileUtil;
import cn.com.bluemoon.lib.utils.LibVersionUtils;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.liblog.NetLogUtils;

public class AppStartActivity extends Activity {

    private long splashScreenStartTime = 0;
    private SplashScreenTimerTask splashScreenTimerTask = null;
    private static Version lastSuccessfulCheckVersionResponse = null;
    private AppStartActivity main;
    private String view;
    private String url;
    private boolean isPause;
    public LocationService locationService = null;

    public static void actStart(Context context, String view, String url) {
        Intent intent = new Intent(context, AppStartActivity.class);
        intent.putExtra(Constants.PUSH_VIEW, view);
        if (Constants.PUSH_H5.equals(view) && !TextUtils.isEmpty(url)) {
            intent.putExtra(Constants.PUSH_URL, url);
        }
        context.startActivity(intent);
    }

    public static void actStart(Context context) {
        actStart(context, null, null);
    }

    public static Intent getStartIntent(Context context, String menuCode, String url) {
        Intent intent = new Intent(context, AppStartActivity.class);
        intent.putExtra(Constants.PUSH_VIEW, menuCode);
        if (Constants.PUSH_H5.equals(menuCode) && !TextUtils.isEmpty(url)) {
            intent.putExtra(Constants.PUSH_URL, url);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //暂时处理点击home键会重新启动问题
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        main = this;
        setContentView(R.layout.activity_start);
        init();
    }

    private void init() {

        FileUtil.init();
        //获取推送内容
        view = PublicUtil.getPushView(getIntent());
        url = PublicUtil.getPushUrl(getIntent());

        //推送SDK初始化
        initPush();

        //百度定位初始化，每5分钟定位一次
        locationService = new LocationService(getApplicationContext());
        LocationClientOption mOption = locationService.getDefaultLocationClientOption();
        locationService.setLocationOption(mOption);
        locationService.registerListener();
        locationService.start();

        initAlarm();

        initUserAgent();
    }

    private void initAlarm() {
        try {
            if (!StringUtil.isEmptyString(ClientStateManager.getLoginToken())) {
                Reminds.SynAlarm(this);
            }
        } catch (Exception ex) {
            LogUtils.e("AppContext", "Syn Alarms Error", ex);
        }
    }

    /**
     * 推送SDK初始化
     */
    private void initPush() {
        // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
        PackageManager pkgManager = getPackageManager();
        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission = pkgManager.checkPermission(android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(android.Manifest.permission.READ_PHONE_STATE,
                        getPackageName()) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 0);
        } else {
            // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
            PushManager.getInstance().initialize(this.getApplicationContext());
        }
    }

    private void initUserAgent(){
        if(TextUtils.isEmpty(ClientStateManager.getUserAgent())){
            ClientStateManager.setUserAgent(NetWorkUtil.getUserAgent(AppContext.getInstance()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //处理更新下载中...点home再返回重新更新下载的问题
        if (isPause) return;
        //计时器开始时间
        splashScreenStartTime = SystemClock.elapsedRealtime();
        if (canSkipCheckVersion(this)) {
            //已经check version，且间隔2小时以内的非强制更新
            gotoNextActivity();
        } else {
            if (lastSuccessfulCheckVersionResponse != null && SystemClock.elapsedRealtime()
                    - lastSuccessfulCheckVersionResponse.getTimestamp() < Constants
                    .FORCE_CHECK_VERSION_TIME) {
                LogUtils.i("Has checked version before, but are forced to update");
                //已经check version 属于强制更新 !
                showDialog(lastSuccessfulCheckVersionResponse);
            } else {
                DeliveryApi.getLastVersion(checkVersionHandler);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (splashScreenTimerTask != null && !splashScreenTimerTask.isCancelled()) {
            splashScreenTimerTask.cancel(false);
        }
    }

    public static boolean canSkipCheckVersion(Context ctx) {

        if (lastSuccessfulCheckVersionResponse != null) {
            // 时间间隔超过2 hours?
            boolean forceCheckVersionTimeOver = SystemClock.elapsedRealtime()
                    - lastSuccessfulCheckVersionResponse.getTimestamp() > Constants
                    .FORCE_CHECK_VERSION_TIME;

            if (!forceCheckVersionTimeOver) {
                String currentVersion = null;
                //获取当前版本
                try {
                    currentVersion = ctx.getPackageManager().getPackageInfo(
                            ctx.getPackageName(), 0).versionName;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 非强制更新？
                return !LibVersionUtils.isMustUpdateVersion(
                        currentVersion, lastSuccessfulCheckVersionResponse
                                .getVersion(), String
                                .valueOf(lastSuccessfulCheckVersionResponse
                                        .getMustUpdate()));
            } else {
                LogUtils.i("Has checked version before, but over 2 hours");
                return false;
            }
        } else {
            LogUtils.i("Has not check version before, can not skip");
            return false;
        }
    }

    AsyncHttpResponseHandler checkVersionHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d("appStartCheckVersion result = "
                    + responseString);
            try {
                ResultVersionInfo result = JSON.parseObject(responseString,
                        ResultVersionInfo.class);
                if (result != null && result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS
                        && result.getItemList() != null) {
                    lastSuccessfulCheckVersionResponse = result.getItemList();
                    lastSuccessfulCheckVersionResponse.setTimestamp(SystemClock.elapsedRealtime());
                    showDialog(lastSuccessfulCheckVersionResponse);
                } else {
                    gotoNextActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
                gotoNextActivity();
            }


        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {

            gotoNextActivity();

            LogUtils.d("appStartCheckVersion result = "
                    + responseString + throwable.getMessage());
        }
    };

    private void showDialog(Version response) {
        String tcurrentVersion = "0.0.0";
        String tnewVersion;
        try {
            tcurrentVersion = main.getPackageManager()
                    .getPackageInfo(main.getPackageName(), 0).versionName;
        } catch (Exception e) {
            if (!BuildConfig.RELEASE)
                e.printStackTrace();
        }

        tnewVersion = response.getVersion();
        if (!StringUtils.isNotBlank(tnewVersion)) {
            tnewVersion = tcurrentVersion;
            response.setVersion(tnewVersion);
            lastSuccessfulCheckVersionResponse = response;
        }
        LogUtils.d("isMustUpdate =" + response.getMustUpdate());
        LogUtils.d("tnewVersion =" + tnewVersion);
        LogUtils.d("tcurrentVersion =" + tcurrentVersion);
        final String currentVersion = tcurrentVersion;
        final String newVersion = tnewVersion;
        if (currentVersion.equals(newVersion)) {
            gotoNextActivity();
        } else if (LibVersionUtils
                .isMustUpdateVersion(currentVersion, response.getVersion(),
                        String.valueOf(response.getMustUpdate()))) {
            showUpdateDialog(response, true);
        } else if (LibVersionUtils.isNewerVersion(currentVersion, newVersion)) {
            showUpdateDialog(response, false);
        } else {
            gotoNextActivity();
        }
    }

    private void gotoNextActivity() {
        //不可取消！
        if (splashScreenTimerTask != null && !splashScreenTimerTask.isCancelled()) {
            splashScreenTimerTask.cancel(false);
        }
        if (!isFinishing()) {
            LogUtils.i("splashScreenTimerTask execute");
            splashScreenTimerTask = new SplashScreenTimerTask();
            splashScreenTimerTask.execute();
        }
    }

    private class SplashScreenTimerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... v) {
            //欢迎界面显示至少1s
            long waitTime = SystemClock.elapsedRealtime() - splashScreenStartTime;
            if (waitTime < Constants.SPLASH_SCREEN_MIN_SHOW_TIME) {
                try {
                    Thread.sleep(Constants.SPLASH_SCREEN_MIN_SHOW_TIME - waitTime);
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (!isCancelled() && !isFinishing()) {
                try {
                    String token = ClientStateManager.getLoginToken();
                    if (!StringUtil.isEmpty(token)) {
                        MainActivity.actStart(main, view, url);
                    } else {
                        LoginActivity.actStart(main, view, url);
                    }
                    if (!isFinishing())
                        finish();
                } catch (Exception ex) {

                    ViewUtil.toast(getString(R.string.start_error));
                    LogUtils.e("AppStartActivity", ex.toString());
                    main.finish();
                }
            }
        }
    }

    private void showUpdateDialog(final Version result,
                                  final boolean isMustUpdate) {
        isPause = true;
        new CommonAlertDialog.Builder(main)
                .setTitle(getString(R.string.new_version_alert_title))
                .setMessage(
                        StringUtil.getCheckVersionDescription(result
                                .getDescription()))
                .setCancelable(false)
                .setNegativeButton(R.string.new_version_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                new UpdateManager(main, result.getDownload(),
                                        result.getVersion(),
                                        new UpdateManager.UpdateCallback() {

                                            @Override
                                            public void onCancel() {
                                                isPause = false;
                                                // stub
                                                if (isMustUpdate) {
                                                    main.finish();
                                                } else {
                                                    gotoNextActivity();
                                                }
                                            }

                                            @Override
                                            public void onFailUpdate() {
                                                isPause = false;
                                                // stub
                                                try {
                                                    PublicUtil.openUrl(main, result.getDownload());
                                                    finish();
                                                } catch (Exception e) {
                                                    new Handler(getMainLooper()).post(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            ViewUtil.toast(getString(R.string
                                                                    .new_version_error));
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFinishUpdate() {
                                                isPause = false;
                                            }
                                        }).showDownloadDialog();
                            }

                        })
                .setPositiveButton(R.string.new_version_no,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                isPause = false;
                                if (isMustUpdate) {
                                    main.finish();
                                    System.exit(0);
                                } else {
                                    gotoNextActivity();
                                }
                                return;
                            }

                        }).show();

    }

}