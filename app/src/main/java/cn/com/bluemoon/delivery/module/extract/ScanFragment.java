package cn.com.bluemoon.delivery.module.extract;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfoPickup;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class ScanFragment extends Fragment implements OnClickListener {
	private String TAG = "ScanFragment";
	private ExtractTabActivity mContext;
	private Button btnScan;
	private Button btnSign;
	private ClearEditText etNumber;
	private LinearLayout numLayout;
	private CommonProgressDialog progressDialog;
	private String signType = "digital";

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = (ExtractTabActivity) activity;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		View v = inflater.inflate(R.layout.fragment_tab_extract_scan,
				container, false);
		progressDialog = new CommonProgressDialog(mContext);
		numLayout = (LinearLayout) v.findViewById(R.id.num_layout);
		btnScan = (Button) v.findViewById(R.id.btn_scan);
		btnSign = (Button) v.findViewById(R.id.btn_sign);
		etNumber = (ClearEditText) v.findViewById(R.id.et_number);
		btnScan.setOnClickListener(this);
		btnSign.setOnClickListener(this);
		numLayout.setOnClickListener(this);
		checkBtnState();
		etNumber.setCallBack(new CommonEditTextCallBack() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				super.afterTextChanged(s);
				checkBtnState();
			}
			
		});
		return v;
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
	
	private void initCustomActionBar() {
		CommonActionBar mActionbar = new CommonActionBar(mContext.getActionBar(), new
				IActionBarListener() {

			@Override
			public void onBtnRight(View v) {

			}

			@Override
			public void onBtnLeft(View v) {
				mContext.finish();
			}

			@Override
			public void setTitle(TextView v) {
				v.setText(getText(R.string.tab_extract_scan_code_title));
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED)
			return;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_SCAN:
				String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
				signType = "scan";
				DeliveryApi.getOrderInfo(ClientStateManager.getLoginToken(mContext), resultStr, getOrderInfoHandler);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnScan) {
			PublicUtil.openScanOrder(mContext, ScanFragment.this,
					getString(R.string.tab_extract_scan_code_title),
					getString(R.string.extract_input_code_btn),
					Constants.REQUEST_SCAN, 4);
		} else if (v == numLayout) {
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		} else {
			if (StringUtils.isNotBlank(etNumber.getText().toString())) {
				progressDialog.show();
				signType = "digital";
				DeliveryApi.getOrderInfo(ClientStateManager.getLoginToken(mContext), etNumber.getText().toString(), getOrderInfoHandler);
			} else {
				PublicUtil.showToast(mContext, getString(R.string.extract_scan_code_input_number));
			}
		}
	}
	
	AsyncHttpResponseHandler getOrderInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "getOrderInfoHandler result = " + responseString);
			progressDialog.dismiss();
			try {
				ResultOrderInfoPickup result = JSON.parseObject(responseString, ResultOrderInfoPickup.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					etNumber.setText("");
					Intent intent = new Intent(mContext, OrderDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("order", result);
					bundle.putString("signType", signType);
					intent.putExtras(bundle);
					ScanFragment.this.startActivity(intent);
				} else {
					PublicUtil.showErrorMsg(mContext, result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy(mContext);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

}
