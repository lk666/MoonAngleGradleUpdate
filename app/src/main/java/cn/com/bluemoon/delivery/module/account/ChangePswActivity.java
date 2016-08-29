package cn.com.bluemoon.delivery.module.account;

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
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class ChangePswActivity extends KJActivity {
	private String TAG = "ChangePswActivity";
	@BindView(id=R.id.current_password)
	private ClearEditText curPsw;
	@BindView(id=R.id.new_password)
	private ClearEditText newPsw;
	@BindView(id=R.id.confirm_password)
	private ClearEditText conPsw;
	@BindView(id=R.id.submit_btn,click=true)
	private Button submitBtn;
	private CommonProgressDialog progressDialog;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.account_set_change_psw);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);
		curPsw.setMaxLength(18);
		newPsw.setMaxLength(18);
		conPsw.setMaxLength(18);
	}


	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.submit_btn:
			submit();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return false;
	}
	
	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(),new IActionBarListener() {
			
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
				v.setText(R.string.user_changepwd);
			}
		});
		
	}

	public void submit() {
		
		String cuPsw = curPsw.getText().toString();
		String nePsw = newPsw.getText().toString();
		String coPsw = conPsw.getText().toString();
		if(StringUtils.isEmpty(cuPsw))
		{
			PublicUtil.showToast(aty, getString(R.string.cur_psw_hint));
			return;
		}
		if(cuPsw.length()<8)
		{
			PublicUtil.showToast(aty, getString(R.string.cur_psw_fail));
			return;
		}
		if(nePsw.length()<8)
		{
			PublicUtil.showToast(aty, getString(R.string.new_psw_tips));
			return;
		}
		if(!coPsw.equals(nePsw))
		{
			PublicUtil.showToast(aty, getString(R.string.register_same_pwd));
			return;
		}
		String token = ClientStateManager.getLoginToken(aty);
		if(!StringUtils.isEmpty(token)){
			DeliveryApi.updatePassword(token, cuPsw, nePsw, changePwdHandler);
		}
	}
	
	AsyncHttpResponseHandler changePwdHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"updatePassword result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString,ResultBase.class);
				if(baseResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					PublicUtil.showToast(aty, getString(R.string.change_pwd_success));
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
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG); 
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(TAG); 
	    if(progressDialog != null)
			progressDialog.dismiss();
	}

}
