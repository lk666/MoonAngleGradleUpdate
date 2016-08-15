package cn.com.bluemoon.delivery.module.account;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;

public class SettingActivity extends KJActivity {

	@BindView(id = R.id.re_general, click = true)
	private RelativeLayout reGeneral;
	@BindView(id = R.id.re_about, click = true)
	private RelativeLayout reAbout;
	private String TAG = "SettingActivity";

	@Override
	public void setRootView() {
		initCustomActionBar();
		setContentView(R.layout.account_set_main);
	}

	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.re_general:
			toPersoninfo(Constants.MODE_GENERAL);
			break;
		case R.id.re_about:
			toPersoninfo(Constants.MODE_CHECK);
			break;
		}
	}

	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {
			}

			@Override
			public void onBtnLeft(View v) {
				finish();
			}

			@Override
			public void setTitle(TextView v) {
				v.setText(getText(R.string.user_settings));
			}
		});
	}

	private void toPersoninfo(int mode) {
		Intent intent = new Intent();
		intent.setClass(this, SettingInfoActivity.class);
		intent.putExtra("mode", mode);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return false;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

}
