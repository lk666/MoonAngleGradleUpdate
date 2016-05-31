/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/1/26
 */
package cn.com.bluemoon.delivery.extract;


import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.adapter.OrderProductAdapter;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfoPickup;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.LinearLayoutForListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.OrdersUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
public class OrderDetailActivity extends Activity implements OnClickListener {
	private String TAG = "OrderDetailActivity";
	private CommonActionBar mActionbar;
	private View popStart;
	private LinearLayoutForListView listviewProduct;
	private Button btnSign;
	private TextView tvTotalPrice;
	private TextView tvOrderNumber;
	private TextView tvStorehouse;
	private TextView tvOrderUserName;
	private TextView tvOrderUserPhone;
	private TextView tvOrderPayTime;
	private LinearLayout layoutStorehouse;
	private OrderDetailActivity mContext;
	private ResultOrderInfoPickup pickupInfo;
	private CommonProgressDialog progressDialog;
	private String signType;
	private boolean lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extract_order_detail);
		ActivityManager.getInstance().pushOneActivity(this);
		mContext = this;
		initCustomActionBar();
		progressDialog = new CommonProgressDialog(mContext);
		progressDialog.setCancelable(false);
		popStart = (View) findViewById(R.id.view_pop_start);
		listviewProduct = (LinearLayoutForListView) findViewById(R.id.listview_product);
		btnSign = (Button) findViewById(R.id.btn_sign);
		tvOrderNumber = (TextView) findViewById(R.id.tv_order_number);
		tvStorehouse = (TextView) findViewById(R.id.tv_storehouse);
		tvOrderUserName = (TextView) findViewById(R.id.tv_order_username);
		tvOrderUserPhone = (TextView) findViewById(R.id.tv_order_userphone);
		tvOrderPayTime = (TextView) findViewById(R.id.tv_order_pay_time);
		tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
		layoutStorehouse = (LinearLayout) findViewById(R.id.layout_storehouse);
		btnSign.setOnClickListener(this);
		layoutStorehouse.setOnClickListener(this);
		pickupInfo = (ResultOrderInfoPickup) getIntent().getSerializableExtra("order");
		signType = getIntent().getStringExtra("signType");

		if (null == pickupInfo) {
			finish();
		} else {
			// test
			tvTotalPrice.setText(String.format(
					getString(R.string.extract_order_total_pay),
					StringUtil.formatPrice(pickupInfo.getTotalPrice())));

			tvStorehouse.setText(OrdersUtils.getShString(pickupInfo));
			tvOrderNumber.setText(pickupInfo.getOrderId());
			tvOrderUserName.setText(pickupInfo.getNickName());
			tvOrderUserPhone.setText(pickupInfo.getMobilePhone());
			tvOrderPayTime.setText(pickupInfo.getOrderCreateTime());
			OrderProductAdapter productAdapter = new OrderProductAdapter(mContext, pickupInfo.getProductList());
			listviewProduct.setAdapter(productAdapter);
//			LibViewUtil.setListViewHeight2(listviewProduct);	
		}
	}
	
	private void initCustomActionBar() {
		mActionbar = new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {

			}

			@Override
			public void onBtnLeft(View v) {
				CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
						OrderDetailActivity.this);
				dialog.setMessage(getString(R.string.extract_cacle_comfire_txt));
				dialog.setPositiveButton(R.string.extract_ok_btn,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								OrderDetailActivity.this.finish();
							}

						});
				dialog.setNegativeButton(R.string.extract_cancle_btn,
						null);
				dialog.show();
			}

			@Override
			public void setTitle(TextView v) {
				v.setText(getText(R.string.extract_order_info_title));
			}
		});
	}
	
	AsyncHttpResponseHandler pickupOrderHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "pickupOrderHandler result = " + responseString);
			if(progressDialog!=null) progressDialog.dismiss();

			try {
				ResultBase result = JSON.parseObject(responseString, ResultBase.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					PublicUtil.showCustomToast(mContext, result.getResponseMsg(), true);
					finish();
				} else {
					lock = false;
					PublicUtil.showErrorMsg(mContext, result);
				}
			} catch (Exception e) {
				lock = false;
				PublicUtil.showToastServerBusy(mContext);
			}

		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if(progressDialog!=null) progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(mContext);
			lock = false;
		}
	};
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	public void onClick(View v) {

		if (v == btnSign) {
			if (null != pickupInfo&&!lock) {
				lock = true;
				if(progressDialog!=null) progressDialog.show();
				DeliveryApi.pickupOrder(signType, ClientStateManager.getLoginToken(mContext), pickupInfo, pickupOrderHandler);
			}

		} else if (v == layoutStorehouse) {
			StoreHouseDetailWindow popupWindow = new StoreHouseDetailWindow(this, pickupInfo);
			popupWindow.showPopwindow(popStart);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
					OrderDetailActivity.this);
			dialog.setMessage(getString(R.string.extract_cacle_comfire_txt));
			dialog.setPositiveButton(R.string.extract_ok_btn,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							OrderDetailActivity.this.finish();
						}

					});
			dialog.setNegativeButton(R.string.extract_cancle_btn, null);
			dialog.show();
		}
		return super.onKeyDown(keyCode, event);
	}

}
