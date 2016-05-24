package cn.com.bluemoon.delivery.account;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends KJActivity {

	@BindView(id = R.id.re_general, click = true)
	private RelativeLayout reGeneral;
	@BindView(id = R.id.re_about, click = true)
	private RelativeLayout reAbout;
//	@BindView(id = R.id.re_call, click = true)
//	private RelativeLayout reCall;
	private String TAG = "SettingActivity";

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.account_set_main);
		ActivityManager.getInstance().pushOneActivity(this);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.re_general:
			toPersoninfo(Constants.MODE_GENERAL);
			break;
		case R.id.re_about:
			toPersoninfo(Constants.MODE_CHECK);
			break;
//		case R.id.re_call:
//			PublicUtil.showMessageService(aty);
//			break;
		}
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
