package cn.com.bluemoon.delivery.module.account;

import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultVailCode;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class ResetPswActivity extends KJActivity {
	private String TAG = "ResetPswActivity";
	@BindView(id = R.id.reset_yzm_btn, click = true)
	private Button btnYzm;
	@BindView(id = R.id.submit_btn, click = true)
	private Button btnSubmit;
	private String userid;
	private String phone;
	@BindView(id = R.id.reset_yzm_edit)
	private EditText edYzm;
	@BindView(id = R.id.reset_password)
	private ClearEditText edPassword;
	@BindView(id = R.id.reset_confirm_password)
	private ClearEditText edCoPassword;
	private CommonProgressDialog progressDialog;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.password_reset);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();
		getData();
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(aty);
		edPassword.setMaxLength(18);
		edCoPassword.setMaxLength(18);
		progressDialog = new CommonProgressDialog(this);
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.reset_yzm_btn:
			if (!PublicUtil.hasIntenet(aty)) {
				PublicUtil.showMessageNoInternet(aty);
				return;
			}
			if(StringUtils.isEmpty(userid)||StringUtils.isEmpty(phone)){
				PublicUtil.showToastErrorData(aty);
				return;
			}
			if (progressDialog != null)
				progressDialog.show();
			DeliveryApi.getVerifyCode(phone, userid, smsHandler);
			break;
		case R.id.submit_btn:
			submit();
			break;
		}
	}

	private void getData() {
		int delayTime = 0;
		if (getIntent() != null) {
			userid = getIntent().getStringExtra("id");
			phone = getIntent().getStringExtra("phone");
			delayTime = getIntent().getIntExtra("time", 0);
		}
		if (phone == null || userid == null) {
			PublicUtil.showToast(aty,
					getString(R.string.register_get_phone_fail));
			setResult(RESULT_CANCELED, null);
			this.finish();
		}
		startTime(delayTime);
	}

	private void startTime(int delayTime) {
		TimeCount time = new TimeCount(delayTime * 1000, 1000);
		time.start();
		btnYzm.setBackgroundResource(R.drawable.btn_disable_shape);
	}

	public void btnBack(View v) { 
		setResult(RESULT_CANCELED, null);
		this.finish();
	}

	private void submit() {

		String code = edYzm.getText().toString().trim();
		String pass = edPassword.getText().toString();
		String copass = edCoPassword.getText().toString();
		if (StringUtils.isEmpty(code) || code.length() != 4) {
			PublicUtil.showToast(aty, getString(R.string.register_right_code));
		} else if ("".equals(pass) || "".equals(copass)) {
			PublicUtil.showToast(aty, getString(R.string.register_not_empty));
		} else if (pass.length() < 6) {
			PublicUtil.showToast(aty, getString(R.string.register_right_pwd));
		} else if (!pass.equals(copass)) {
			PublicUtil.showToast(aty, getString(R.string.register_same_pwd));
		} else if (!PublicUtil.hasIntenet(this)) {
			PublicUtil.showMessageNoInternet(aty);
		} else {
			if (progressDialog != null)
				progressDialog.show();
			DeliveryApi.resetPassword(phone, code, pass, resetHandler);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { 
			setResult(RESULT_CANCELED, null);
			this.finish();
		}
		return false;
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			btnYzm.setText(getString(R.string.register_check_again));
			btnYzm.setBackgroundResource(R.drawable.btn_blue_shape);
			btnYzm.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// ��ʱ������ʾ
			btnYzm.setClickable(false);
			btnYzm.setText(String.format("(%d)%s", millisUntilFinished / 1000, getString(R.string
					.register_second)));
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
				 v.setText(R.string.user_resetpwd);
			}
		});
	}

	AsyncHttpResponseHandler resetHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"resetPassword result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString,ResultBase.class);
				if(baseResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					ClientStateManager.setUserName(userid);
					PublicUtil.showToast(aty, getString(R.string.reset_success));
					setResult(RESULT_OK, null);
					finish();
				}else{
					PublicUtil.showErrorMsg(aty, baseResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy(aty);
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, 
				Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if(progressDialog != null)
				progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(aty);
		}
	};
	
	AsyncHttpResponseHandler smsHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"getVerifyCode result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultVailCode vailCodeResult = JSON.parseObject(responseString,ResultVailCode.class);
				if(vailCodeResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS
						||vailCodeResult.getResponseCode() == Constants.RESPONSE_RESULT_SMS_VAILD){
					if(StringUtils.isEmpty(vailCodeResult.getResponseMsg())){
						PublicUtil.showToast(aty, getString(R.string.reset_send_sms_success));
					}else{
						PublicUtil.showToast(aty,vailCodeResult.getResponseMsg());
					}
					startTime(vailCodeResult.getTime());
				}else{
					PublicUtil.showErrorMsg(aty, vailCodeResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy(aty);
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, 
				Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if(progressDialog != null)
				progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(aty);
		}
	};

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
