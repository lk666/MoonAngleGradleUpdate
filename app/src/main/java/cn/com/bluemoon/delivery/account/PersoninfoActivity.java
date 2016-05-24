package cn.com.bluemoon.delivery.account;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultUser;
import cn.com.bluemoon.delivery.app.api.model.User;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class PersoninfoActivity extends KJActivity {

	private String TAG = "PersoninfoActivity";
	@BindView(id = R.id.txt_userid)
	private TextView txtUserid;
	@BindView(id = R.id.txt_username)
	private TextView txtUsername;
	@BindView(id = R.id.txt_phone)
	private TextView txtPhone;
	@BindView(id = R.id.re_changepwd, click = true)
	private RelativeLayout reChangePwd;
	@BindView(id= R.id.btn_loginout,click = true)
	private Button btnLoginout;
	public static User user;
	private CommonProgressDialog progressDialog;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.account_set_personinfo);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);
		setUserInfo();
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.re_changepwd:
			Intent intent = new Intent(this, ChangePswActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_loginout:
			String token = ClientStateManager.getLoginToken(aty);
			if(StringUtils.isEmpty(token)){
				loginout();
			}else{
				if (progressDialog!=null)
					progressDialog.show();
				DeliveryApi.logout(token, loginoutHandler);
			}
			break;
		}
	}

	private void setUserInfo() {
		txtUserid.setText(ClientStateManager.getUserName(aty));
		if (user != null) {
			txtUserid.setText(user.getAccount());
			txtUsername.setText(user.getRealName());
			txtPhone.setText(user.getMobileNo());
		} else {
			String token = ClientStateManager.getLoginToken(aty);
			if(!StringUtil.isEmpty(token)){
				DeliveryApi.getUserInfo(token, userInfoHandler);
			}
		}

	}
	
	private void loginout(){
		PublicUtil.showToast(aty, getString(R.string.loginout_success));
		Intent i = new Intent();
		i.setClass(this, LoginActivity.class);
		startActivity(i);
		finish();
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
				v.setText(R.string.user_personinfo);
			}
		});
	}
	
	AsyncHttpResponseHandler userInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"getVerifyCode result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultUser userResult = JSON.parseObject(responseString,ResultUser.class);
				if(userResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					user = userResult.getUser();
					if (user != null){
						txtUserid.setText(user.getAccount());
						txtUsername.setText(user.getRealName());
						txtPhone.setText(user.getMobileNo());
					}
				}else{
					PublicUtil.showErrorMsg(aty, userResult);
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
	
	AsyncHttpResponseHandler loginoutHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"logout result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString,ResultUser.class);
				if(baseResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS
						||baseResult.getResponseCode()==Constants.RESPONSE_RESULT_TOKEN_EXPIRED
						||baseResult.getResponseCode()==Constants.RESPONSE_RESULT_TOKEN_NOT_EXIST){
					loginout();
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
