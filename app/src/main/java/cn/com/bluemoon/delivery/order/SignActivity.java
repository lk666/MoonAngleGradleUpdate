/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/1/13
 */
package cn.com.bluemoon.delivery.order;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class SignActivity extends Activity implements OnClickListener{
	private String TAG = "SignActivity";
	private CommonActionBar mActionbar;
	private Button btnScan;
	private Button btnSign;
	private ClearEditText etNumber;
	private CommonProgressDialog progressDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(this);
		initCustomActionBar();
		btnScan = (Button) findViewById(R.id.btn_scan);
		btnSign = (Button) findViewById(R.id.btn_sign);
		etNumber = (ClearEditText) findViewById(R.id.et_number);
		btnScan.setOnClickListener(this);
		btnSign.setOnClickListener(this);
		checkBtnState();
		etNumber.setCallBack(new CommonEditTextCallBack() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				super.afterTextChanged(s);
				checkBtnState();
			}
			
		});
	}
	
	private void checkBtnState(){
		if(etNumber.getText().toString().trim().length()>0){
			btnSign.setClickable(true);
			btnSign.setBackgroundResource(R.drawable.btn_blue_shape);
		}else{
			btnSign.setClickable(false);
			btnSign.setBackgroundResource(R.drawable.btn_blue_shape_disable);
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v == btnScan) {
			setResult(3);
			finish();
		} else {
			if(getIntent()!=null){
				String orderId = getIntent().getStringExtra("orderId");
				if (StringUtils.isNotBlank(etNumber.getText().toString())) {
					progressDialog.show();
					DeliveryApi.orderSign(ClientStateManager.getLoginToken(this), orderId, "digital", etNumber.getText().toString(), orderSignHandler);
				} else {
					PublicUtil.showToast(this, getString(R.string.scan_code_input_number));
				}
			}
			
		}
	}
	
	AsyncHttpResponseHandler orderSignHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "OrderSignHandler result = " + responseString);
			progressDialog.dismiss();
			try {
				ResultBase result = JSON.parseObject(responseString, ResultBase.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					PublicUtil.showToast(result.getResponseMsg());
					setResult(2);
					SignActivity.this.finish();
				} else {
					PublicUtil.showErrorMsg(SignActivity.this, result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy(SignActivity.this);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(SignActivity.this);
		}
	};
	
	private void initCustomActionBar() {
		mActionbar = new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {

			}

			@Override
			public void onBtnLeft(View v) {
				SignActivity.this.finish();
			}

			@Override
			public void setTitle(TextView v) {
				v.setText(getText(R.string.pending_order_receive_sign_title));
			}

		});
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
