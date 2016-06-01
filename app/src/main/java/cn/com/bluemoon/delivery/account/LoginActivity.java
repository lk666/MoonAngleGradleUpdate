package cn.com.bluemoon.delivery.account;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends KJActivity {

	private String TAG = "LoginActivity";
	@BindView(id = R.id.login_user_edit)
	private ClearEditText loginUserEdit;
	@BindView(id = R.id.login_passwd_edit)
	private ClearEditText loginPwdEdit;
	@BindView(id = R.id.login_btn, click = true)
	private Button loginBtn;
	@BindView(id = R.id.forget_psw_btn, click = true)
	private TextView forgetBtn;
	private CommonProgressDialog progressDialog;
	private ActivityManager manager;
	private String account;
	private String jumpCode="";

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.login);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ClientStateManager.clearData(aty);
		if(getIntent()!=null&&getIntent().hasExtra(Constants.KEY_JUMP)){
			jumpCode = getIntent().getStringExtra(Constants.KEY_JUMP);
		}
		manager = ActivityManager.getInstance();
		manager.pushOneActivity(aty);
		progressDialog = new CommonProgressDialog(aty);
		loginUserEdit.setMaxLength(9);
		loginPwdEdit.setMaxLength(16);

		loginUserEdit.setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				char[] numberChars ={'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m',
						'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'
						,'1','2','3','4','5','6','7','8','9','0'};

				return numberChars;
			}

			@Override
			public int getInputType() {
				return InputType.TYPE_CLASS_TEXT;
			}
		});
		setUserInfo();
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.login_btn:
			login();
			break;

		case R.id.forget_psw_btn:
			String username = loginUserEdit.getText().toString().trim();
			Intent intent = new Intent();
			intent.setClass(aty, InputPhoneActivity.class);
			intent.putExtra("id", username);
			startActivityForResult(intent, 0);
			break;

		}
	}

	private void setUserInfo() {
		String username = ClientStateManager.getUserName(aty);
		loginUserEdit.setText(username);
		loginUserEdit.setSelection(loginUserEdit.length());
		loginUserEdit.updateCleanable(0, false);
	}

	private void login() {
		account = loginUserEdit.getText().toString().trim();
		String password = loginPwdEdit.getText().toString();

		if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
			PublicUtil.showToast(aty, getString(R.string.register_not_empty));
			return;
		}
		if (progressDialog != null) {
			progressDialog.show();
		}
		DeliveryApi.ssoLogin(account, password, loginHandler);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent!=null&&intent.hasExtra(Constants.KEY_JUMP)){
			jumpCode = intent.getStringExtra(Constants.KEY_JUMP);
		}
	}

	AsyncHttpResponseHandler loginHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "ssoLogin result = " + responseString);
			if (progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultToken tokenResult = JSON.parseObject(responseString,
						ResultToken.class);
				if (tokenResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					ClientStateManager.setLoginToken(aty, tokenResult.getToken());
					ClientStateManager.setUserName(aty, account);
					MobclickAgent.onProfileSignIn(account);
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					if(!StringUtil.isEmpty(jumpCode)){
						intent.putExtra(Constants.KEY_JUMP,jumpCode);
					}
					startActivity(intent);
					LoginActivity.this.finish();
				} else {
					PublicUtil.showErrorMsg(aty, tokenResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy(aty);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if (progressDialog != null)
				progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(aty);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			new CommonAlertDialog.Builder(aty)
					.setTitle(R.string.app_name)
					.setMessage(R.string.exit_app_msg)
					.setPositiveButton(R.string.btn_ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									LoginActivity.this.finish();
									manager.finishAllActivity();
								}
							}).setNegativeButton(R.string.btn_cancel, null)
					.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			setUserInfo();
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
