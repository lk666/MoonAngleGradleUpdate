package cn.com.bluemoon.delivery;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;
import cn.com.bluemoon.delivery.account.LoginActivity;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultVersionInfo;
import cn.com.bluemoon.delivery.app.api.model.Version;
import cn.com.bluemoon.delivery.manager.UpdateManager;
import cn.com.bluemoon.delivery.service.LocationService;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibFileUtil;
import cn.com.bluemoon.lib.utils.LibVersionUtils;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

public class AppStartActivity extends Activity {

	private long splashScreenStartTime = 0;
	private SplashScreenTimerTask splashScreenTimerTask = null;
	private static Version lastSuccessfulCheckVersionResponse = null;
	// private RetrieveVersionInfo retrieveVersionInfo = null;
	private AppStartActivity main;
	private String jumpCode="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		init();
		LocationService locationService = ((AppContext) getApplication()).locationService;
		locationService.start();
		main = this;
	}

	private void init(){
		if(getIntent().hasExtra(Constants.KEY_JUMP)){
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
			if (result != null && result.isSuccess) {
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

	// private class RetrieveVersionInfo extends
	// AsyncTask<Void, Void, CheckVersionResponse> {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// }
	//
	// @Override
	// protected CheckVersionResponse doInBackground(Void... params) {
	//
	// CheckVersionJsonResult result = null;
	// CheckVersionResponse resp = null;
	// int retryCount = 0;
	// boolean needTry = true;
	// while (retryCount < 2 && needTry) {
	// try {
	//
	// String reqUrl = UrlString.CHECK_VERION_API + "?platform="
	// + Constants.OS_TYPE + "&nostr="
	// + UUID.randomUUID().toString();
	// LogUtils.d("RetrieveVersionInfo", reqUrl);
	// result = HttpUtil.executeHttpGet(reqUrl,
	// CheckVersionJsonResult.class);
	// // result = getVirtualData();
	// resp = result.itemList.get(0);
	// needTry = false;
	// resp.timestamp = SystemClock.elapsedRealtime();
	// resp.response = result.getCode();
	// return resp;
	//
	// } catch (Exception e) {
	// LogUtils.e("call check version api throws e = "
	// + e.getMessage());
	// retryCount++;
	// }
	// }
	//
	// return resp;
	//
	// }
	//
	// private CheckVersionJsonResult getVirtualData() {
	// CheckVersionJsonResult result = new CheckVersionJsonResult();
	// CheckVersionResponse res = result.new CheckVersionResponse();
	// result.setCode("success");
	// res.setVersion("3.0.0");
	// res.setMustUpdate("0");
	// res.setDescription("test1;test2;test3");
	// res.setDownload("http://openbox.mobilem.360.cn/index/d/sid/3146895");
	// List<CheckVersionResponse> itemList = new
	// ArrayList<CheckVersionResponse>();
	// itemList.add(res);
	// result.itemList = itemList;
	// return result;
	// }
	//
	// @Override
	// protected void onPostExecute(final CheckVersionResponse result) {
	// // super.onPostExecute(result);
	// if (!this.isCancelled()) {
	// if (result != null && "success".equals(result.response)) {
	// result.timestamp = SystemClock.elapsedRealtime();
	// lastSuccessfulCheckVersionResponse = result;
	// showDialog(lastSuccessfulCheckVersionResponse);
	// } else if (result != null) {
	// // resultcode != 0
	// // show detail message
	// gotoNextActivity();
	// } else {
	// // other errors
	// gotoNextActivity();
	// }
	// }
	// }
	// }

	private void showDialog(Version response) {
		String tcurrentVersion = "0.0.0";
		String tnewVersion = null;
		try {
			tcurrentVersion = AppStartActivity.this.getPackageManager()
					.getPackageInfo(AppStartActivity.this.getPackageName(), 0).versionName;
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
		LogUtils.d("test", "isMustUpdate =" + response.getMustUpdate());
		LogUtils.d("test", "tnewVersion =" + tnewVersion);
		LogUtils.d("test", "tcurrentVersion =" + tcurrentVersion);
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

//	private CheckVersionJsonResult getVirtualData() {
//		CheckVersionJsonResult result = new CheckVersionJsonResult();
//		CheckVersionResponse res = result.new CheckVersionResponse();
//		result.setCode("success");
//		res.setVersion("3.0.0");
//		res.setMustUpdate("0");
//		res.setDescription("test1;test2;test3");
//		res.setDownload("http://openbox.mobilem.360.cn/index/d/sid/3146895");
//		List<CheckVersionResponse> itemList = new ArrayList<CheckVersionResponse>();
//		itemList.add(res);
//		result.itemList = itemList;
//		return result;
//	}

	private void gotoNextActivity() {
		if (splashScreenTimerTask != null
				&& !splashScreenTimerTask.isCancelled())
			splashScreenTimerTask.cancel(false);
		if (!isFinishing()) {
			LogUtils.i("test", "splashScreenTimerTask execute");
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
							.getLoginToken(AppStartActivity.this);

					if (!StringUtils.isEmpty(token)) {
						Intent intent = new Intent();
						intent.setClass(AppStartActivity.this,
								MainActivity.class);
						if(!StringUtil.isEmpty(jumpCode)){
							intent.putExtra(Constants.KEY_JUMP,jumpCode);
						}
						startActivity(intent);
					} else {
						Intent intent = new Intent(AppStartActivity.this,
								LoginActivity.class);
						if(!StringUtil.isEmpty(jumpCode)){
							intent.putExtra(Constants.KEY_JUMP,jumpCode);
						}
						startActivity(intent);
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
						}
					}
					if (!isFinishing())
						finish();
				} catch (Exception ex) {

					PublicUtil.showToast(getString(R.string.start_error));
					LogUtils.e("AppStartActivity", ex.toString());
					AppStartActivity.this.finish();
				}
			}
		}
	}

	private void showUpdateDialog(final Version result,
			final boolean isMustUpdate) {

		new CommonAlertDialog.Builder(AppStartActivity.this)
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

								/*
								 * try {
								 * PublicUtil.openUrl(AppStartActivity.this,
								 * result.getDownload()); } catch (Exception e)
								 * { // TODO Auto-generated catch block
								 * e.printStackTrace();
								 * PublicUtil.showToast(AppStartActivity
								 * .this,getString(R.string.new_version_error));
								 * } AppStartActivity.this.finish();
								 */
								new UpdateManager(main, result.getDownload(),
										result.getVersion(),
										new UpdateManager.UpdateCallback() {

											@Override
											public void onCancel() {
												// TODO Auto-generated method
												// stub
												if (isMustUpdate) {
													main.finish();
												} else {
													gotoNextActivity();
												}
											}

											@Override
											public void onFailUpdate() {
												// TODO Auto-generated method
												// stub
												try {
													PublicUtil.openUrl(main, result.getDownload());
													finish();
												} catch (Exception e) {
													new Handler(getMainLooper()).post(new Runnable() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															PublicUtil.showToast(getString(R.string.new_version_error));
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
									AppStartActivity.this.finish();
									System.exit(0);
								} else {
									gotoNextActivity();
								}

								return;
							}

						}).show();

	}
}
