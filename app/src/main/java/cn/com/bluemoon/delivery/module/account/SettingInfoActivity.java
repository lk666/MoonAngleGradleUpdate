package cn.com.bluemoon.delivery.module.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultVersionInfo;
import cn.com.bluemoon.delivery.app.api.model.Version;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.manager.CacheManager;
import cn.com.bluemoon.delivery.utils.manager.UpdateManager;
import cn.com.bluemoon.lib.utils.LibVersionUtils;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class SettingInfoActivity extends KJActivity {

	private String TAG = "SettingInfoActivity";
	@BindView(id = R.id.txt_cache)
	private TextView txtCache;
	@BindView(id = R.id.txt_check)
	private TextView txtCheck;
	@BindView(id = R.id.lin_general)
	private LinearLayout linGeneral;
	@BindView(id = R.id.lin_about)
	private LinearLayout linAbout;
	@BindView(id = R.id.re_clearCache, click = true)
	private RelativeLayout reClearCache;
	@BindView(id = R.id.re_check, click = true)
	private RelativeLayout reCheck;
	private String size;
	private String curVersion;
	private CommonProgressDialog progressDialog;
	private int mode;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		setMode();
		initCustomActionBar();
		setContentView(R.layout.account_setinfo);

	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);
		setLayout();
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {

			case R.id.re_clearCache:
				if ("0.0B".equalsIgnoreCase(txtCache.getText().toString().trim())) {
					PublicUtil.showCustomToast(aty, getString(R.string.no_cache));
					return;
				}
				new CommonAlertDialog.Builder(SettingInfoActivity.this)
						.setMessage(R.string.clear_dialog_msg)
						.setPositiveButton(R.string.btn_ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										CacheManager.clearBlueMoonCacheSize(aty);
										setCacheSize();
										PublicUtil
												.showToast(
														aty,
														getString(R.string.clear_cache_success));
									}
								}).setNegativeButton(R.string.btn_cancel, null)
						.show();
				break;

			case R.id.re_check:

				if (progressDialog != null)
					progressDialog.show();
				DeliveryApi.getLastVersion(checkVersionHandler);
				break;

		}
	}

	private void setMode() {
		Intent intent = getIntent();
		if (intent != null) {
			mode = intent.getIntExtra("mode", Constants.MODE_PERSON);
		} else {
			mode = Constants.MODE_PERSON;
		}
	}

	private void setLayout() {

		if (mode == Constants.MODE_GENERAL) {
			linGeneral.setVisibility(View.VISIBLE);
			setCacheSize();
		} else if (mode == Constants.MODE_CHECK) {
			linAbout.setVisibility(View.VISIBLE);
			curVersion = PublicUtil.getAppVersionNoSuffix(aty);
			String versionString = curVersion;
			if (!BuildConfig.RELEASE) {
				versionString = curVersion
						+ String.format(getString(R.string.test_name),
						BuildConfig.BUILD_CODE);
			}
			txtCheck.setText(versionString);
		}
	}

	private void setCacheSize() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					size = CacheManager.getBlueMoonCacheSize(aty);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Handler handler = new Handler(getMainLooper());
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						txtCache.setText(size);
					}
				});
			}
		}).start();
	}

	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onBtnLeft(View v) {
				// TODO Auto-generated method stub
				finish();
			}

			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				if (mode == Constants.MODE_GENERAL) {
					v.setText(R.string.user_general);
				} else if (mode == Constants.MODE_CHECK) {
					v.setText(R.string.user_about);
				} else {
					v.setText(R.string.user_settings);
				}

			}
		});

	}

	AsyncHttpResponseHandler checkVersionHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
							  String responseString) {
			if (progressDialog != null)
				progressDialog.dismiss();

			ResultVersionInfo result = JSON.parseObject(responseString,
					ResultVersionInfo.class);
			if (result != null && result.isSuccess) {
				String newVersion = "0.0.0";

				final Version resp = result.getItemList();
				if (resp.getVersion() != null) {
					newVersion = resp.getVersion();
				}
				if (LibVersionUtils.isNewerVersion(curVersion, newVersion)) {
					showUpdateDialog(resp);
				} else {
					PublicUtil.showCustomToast(aty,
							getString(R.string.new_version_alert_no_update));
				}

			} else {
				PublicUtil.showToast(aty, result.getResponseMsg());
			}

		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
							  String responseString, Throwable throwable) {
			if (progressDialog != null)
				progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(aty);
		}
	};

	private void showUpdateDialog(final Version result) {
		new CommonAlertDialog.Builder(aty)
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
								new UpdateManager(aty, result.getDownload(),
										result.getVersion(),
										new UpdateManager.UpdateCallback() {

											@Override
											public void onCancel() {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void onFailUpdate() {
												// TODO Auto-generated method
												// stub
												try {
													PublicUtil
															.openUrl(
																	aty,
																	result.getDownload());
												} catch (Exception e) {
													PublicUtil
															.showToast(
																	aty,
																	getString(R.string.new_version_error));
												}
											}
										}).showDownloadDialog();
							}

						}).setPositiveButton(R.string.new_version_no, null)
				.show();

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		if (progressDialog != null)
			progressDialog.dismiss();
	}

}
