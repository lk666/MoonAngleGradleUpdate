package cn.com.bluemoon.delivery.module.account.view;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import cn.com.bluemoon.delivery.app.api.model.ResultVailCode;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class InputPhoneActivity extends KJActivity {
	private String TAG = "InputPhoneActivity";
	private String userid;
	private String phone;
	@BindView(id = R.id.ed_userid)
	private ClearEditText edUserId;
	@BindView(id = R.id.ed_phone)
	private ClearEditText edPhone;
	@BindView(id = R.id.btn_regist, click = true)
	private Button btnReset;
	private CommonProgressDialog progressDialog;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.inputphone);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(this);
		edUserId.setMaxLength(9);
		edPhone.setMaxLength(11);
		userid = getIntent().getStringExtra("id");
		if (userid != null) {
			edUserId.setText(userid);
			edUserId.setSelection(userid.length());
		}
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.btn_regist:
			resetPwd();
			break;

		}
	}

	public void resetPwd() {
		userid = edUserId.getText().toString().trim();
		phone = edPhone.getText().toString().trim();

		if (StringUtils.isEmpty(userid)) {
			PublicUtil.showToast(aty, getString(R.string.reset_id_not_empty));
			return;
		}
		if (StringUtils.isEmpty(phone)) {
			PublicUtil.showToast(aty, getString(R.string.reset_phone_not_empty));
			return;
		}
		if (!PublicUtil.isPhone(phone)) {
			PublicUtil.showToast(aty, getString(R.string.reset_check_phone));
			return;
		}
		if (!PublicUtil.hasIntenet(aty)) {
			PublicUtil.showMessageNoInternet(aty);
			return;
		}
		btnReset.setClickable(false);
		if (progressDialog != null)
			progressDialog.show();
		DeliveryApi.getVerifyCode(phone, userid, smsHandler);
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
				v.setText(R.string.user_findpwd);
			}
		});
	}
	
	AsyncHttpResponseHandler smsHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"getVerifyCode result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			btnReset.setClickable(true);
			try {
				ResultVailCode vailCodeResult = JSON.parseObject(responseString,ResultVailCode.class);
				if(vailCodeResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS
						||vailCodeResult.getResponseCode() == Constants.RESPONSE_RESULT_SMS_VAILD){
					if(StringUtils.isEmpty(vailCodeResult.getResponseMsg())){
						PublicUtil.showToast(aty, getString(R.string.reset_send_sms_success));
					}else{
						PublicUtil.showToast(aty,vailCodeResult.getResponseMsg());
					}
					Intent intent = new Intent(aty,ResetPswActivity.class);
					intent.putExtra("id", userid);
					intent.putExtra("phone", phone);
					intent.putExtra("time", vailCodeResult.getTime());
					startActivityForResult(intent, 0);
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
			btnReset.setClickable(true);
			PublicUtil.showToastServerOvertime(aty);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { 
			setResult(RESULT_CANCELED, null);
			finish();
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			setResult(RESULT_OK, null);
			finish();
			break;
		}

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
