package cn.com.bluemoon.delivery;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.io.File;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultVersionInfo;
import cn.com.bluemoon.delivery.app.api.model.Version;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.delivery.utils.service.LocationService;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.UpdateManager;
import cn.com.bluemoon.lib.utils.LibFileUtil;
import cn.com.bluemoon.lib.utils.LibVersionUtils;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class AppStartActivity extends Activity {

    private long splashScreenStartTime = 0;
    private SplashScreenTimerTask splashScreenTimerTask = null;
    private static Version lastSuccessfulCheckVersionResponse = null;
    // private RetrieveVersionInfo retrieveVersionInfo = null;
    private AppStartActivity main;
    private String jumpCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        setContentView(R.layout.activity_start);
        init();
        LocationService locationService = ((AppContext) getApplication()).locationService;
        locationService.start();
    }

    private void init() {
        if (getIntent() != null && getIntent().hasExtra(Constants.KEY_JUMP)) {
            jumpCode = getIntent().getStringExtra(Constants.KEY_JUMP);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashScreenStartTime = SystemClock.elapsedRealtime();
        if (canSkipCheckVersion(this)
                && (lastSuccessfulCheckVersionResponse != null)) {
            gotoNextActivity();
            return;
        } else {
            if (lastSuccessfulCheckVersionResponse != null
                    && SystemClock.elapsedRealtime()
                    - lastSuccessfulCheckVersionResponse.getTimestamp() < Constants.FORCE_CHECK_VERSION_TIME) {
                LogUtils.d("test",
                        "timestamp < Constants.FORCE_CHECK_VERSION_TIME");
                showDialog(lastSuccessfulCheckVersionResponse);
            } else {
                // retrieveVersionInfo = new RetrieveVersionInfo();
                // retrieveVersionInfo.execute();

                DeliveryApi.getLastVersion(checkVersionHandler);

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // if (retrieveVersionInfo != null &&
        // !retrieveVersionInfo.isCancelled())
        // retrieveVersionInfo.cancel(false);
        if (splashScreenTimerTask != null
                && !splashScreenTimerTask.isCancelled())
            splashScreenTimerTask.cancel(false);

    }

    public static boolean canSkipCheckVersion(Context ctx) {

        String currentVersion = null;

        try {
            currentVersion = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lastSuccessfulCheckVersionResponse != null) {
            boolean forceCheckVersionTimeOver = SystemClock.elapsedRealtime()
                    - lastSuccessfulCheckVersionResponse.getTimestamp() > Constants.FORCE_CHECK_VERSION_TIME;

            boolean isMustUpdateVersion = LibVersionUtils.isMustUpdateVersion(
                    currentVersion, lastSuccessfulCheckVersionResponse
                            .getVersion(), String
                            .valueOf(lastSuccessfulCheckVersionResponse
                                    .getMustUpdate()));
            boolean skip = !forceCheckVersionTimeOver && !isMustUpdateVersion;
            LogUtils.d(
                    "test",
                    "forceCheckVersionTimeOver = "
                            + String.valueOf(forceCheckVersionTimeOver)
                            + ", isMustUpateVersion = "
                            + String.valueOf(isMustUpdateVersion) + "");
            return skip;
        } else {

            LogUtils.i("test", "Has not check version before, can not skip");
            return false;
        }
    }

    AsyncHttpResponseHandler checkVersionHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d("test", "appStartCheckVersion result = "
                    + responseString);

            ResultVersionInfo result = JSON.parseObject(responseString,
                    ResultVersionInfo.class);
            if (result != null && result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS
                    &&result.getItemList()!=null) {
                lastSuccessfulCheckVersionResponse = result.getItemList();
                lastSuccessfulCheckVersionResponse.setTimestamp(SystemClock.elapsedRealtime());
                showDialog(lastSuccessfulCheckVersionResponse);
            } else {
                gotoNextActivity();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {

            gotoNextActivity();

            LogUtils.d("test", "appStartCheckVersion result = "
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
        if (splashScreenTimerTask != null
                && !splashScreenTimerTask.isCancelled())
            splashScreenTimerTask.cancel(false);
        if (!isFinishing()) {
            LogUtils.i("splashScreenTimerTask execute");
            splashScreenTimerTask = new SplashScreenTimerTask();
            splashScreenTimerTask.execute();
        }
    }

    private class SplashScreenTimerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... v) {
            long waitTime = SystemClock.elapsedRealtime()
                    - splashScreenStartTime;
            if (waitTime < Constants.SPLASH_SCREEN_MIN_SHOW_TIME) {
                try {
                    Thread.sleep(Constants.SPLASH_SCREEN_MIN_SHOW_TIME
                            - waitTime);
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (!isCancelled() && !isFinishing()) {
                try {

                    String token = ClientStateManager
                            .getLoginToken();

                    if (!StringUtil.isEmpty(token)) {
                        MainActivity.actStart(main, jumpCode);
                    } else {
                        LoginActivity.actStart(main, jumpCode);
                        if (LibFileUtil.checkExternalSDExists()) {
                            File file = new File(Constants.PATH_PHOTO);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            File file1 = new File(Constants.PATH_TEMP);
                            if (!file1.exists()) {
                                file1.mkdirs();
                            }
                            File file2 = new File(Constants.PATH_CAMERA);
                            if (!file2.exists()) {
                                file2.mkdirs();
                            }
                            File file3 = new File(Constants.PATH_CACHE);
                            if (!file3.exists()) {
                                file3.mkdirs();
                            }
                        }
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
                                                // stub
                                                if (isMustUpdate) {
                                                    main.finish();
                                                } else {
                                                    gotoNextActivity();
                                                }
                                            }

                                            @Override
                                            public void onFailUpdate() {
                                                // stub
                                                try {
                                                    PublicUtil.openUrl(main, result.getDownload());
                                                    finish();
                                                } catch (Exception e) {
                                                    new Handler(getMainLooper()).post(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            ViewUtil.toast(getString(R.string.new_version_error));
                                                        }
                                                    });
                                                }
                                            }
                                        }).showDownloadDialog();
                            }

                        })
                .setPositiveButton(R.string.new_version_no,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
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
