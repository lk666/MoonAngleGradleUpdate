/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/1/26
 */
package cn.com.bluemoon.delivery.module.extract;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderInfo;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.adapter.OrderProductAdapter;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class HistoryOrderDetailActivity extends Activity {
	private String TAG = "HistoryOrderDetailActivity";
	private ListView listviewProduct;
	private TextView tvTotalPrice;
	private TextView tvOrderNumber;
	private TextView tvStorehouse;
	private TextView tvStorechargename;
	private TextView tvStorechargePhone;
	private TextView tvOrderUserName;
	private TextView tvOrderUserPhone;
	private TextView tvOrderPayTime;
	private TextView tvOrderSignTime;
	private String orderId;
	private OrderInfo item;
	private HistoryOrderDetailActivity main;
	private CommonProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extract_history_order_detail);
		main = this;
		ActivityManager.getInstance().pushOneActivity(this);
		initCustomActionBar();
		progressDialog = new CommonProgressDialog(main);
		listviewProduct = (ListView) findViewById(R.id.listview_product);
		listviewProduct.setFocusable(false);
		tvOrderNumber = (TextView) findViewById(R.id.tv_order_number);
		tvStorehouse = (TextView) findViewById(R.id.tv_storehouse);
		tvStorechargename = (TextView) findViewById(R.id.tv_storechargename);
		tvStorechargePhone = (TextView) findViewById(R.id.tv_storechargephone);
		tvOrderUserName = (TextView) findViewById(R.id.tv_order_username);
		tvOrderUserPhone = (TextView) findViewById(R.id.tv_order_userphone);
		tvOrderPayTime = (TextView) findViewById(R.id.tv_order_pay_time);
		tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
		tvOrderSignTime = (TextView) findViewById(R.id.tv_order_sign_time);
		init();
		String token = ClientStateManager.getLoginToken(main);
		if (StringUtils.isEmpty(orderId) || StringUtil.isEmpty(token)) {
			PublicUtil.showToastErrorData(main);
			return;
		}

		if (progressDialog != null) {
			progressDialog.show();
		}
		DeliveryApi.getOrderDetailByOrderId(token, orderId, orderDetailHandler);

	}

	public void init() {
		if (getIntent() != null) {
			orderId = getIntent().getStringExtra("orderId");
		}
		if (orderId == null) {
			finish();
			PublicUtil.showToast(main,
					getString(R.string.return_change_get_detail_fail));
			return;
		}
	}

	public void setLayoutData() {
		if (item != null) {
			tvTotalPrice.setText(String.format(
					getString(R.string.extract_order_total_pay),
					PublicUtil.getPriceFrom(item.getTotalPrice())));
			tvStorehouse.setText(String.format("%s-%s", 
					item.getStorehouseCode(),item.getStorehouseName()));
			tvStorechargename.setText(item.getStorechargeName());
			tvStorechargePhone.setText(item.getStorechargeMobileno());
			tvOrderNumber.setText(item.getOrderId());
			tvOrderUserName.setText(item.getNickName());
			tvOrderUserPhone.setText(item.getNickPhone());
			tvOrderPayTime.setText(item.getPayOrderTime());
			tvOrderSignTime.setText(item.getSignTime());
			if (item.getProductList() != null) {
				OrderProductAdapter productAdapter = new OrderProductAdapter(
						this, item.getProductList());
				listviewProduct.setAdapter(productAdapter);
				LibViewUtil.setListViewHeight2(listviewProduct);
			} else {
				PublicUtil.showToast(main,
						getString(R.string.return_change_get_detail_fail));
			}
		} else {
			PublicUtil.showToast(main,
					getString(R.string.return_change_get_detail_fail));
		}
	}

	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(),
				new IActionBarListener() {

					@Override
					public void setTitle(TextView v) {
						// TODO Auto-generated method stub
						v.setText(getResources().getString(
								R.string.extract_order_info_title));
					}

					@Override
					public void onBtnRight(View v) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onBtnLeft(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
	}

	AsyncHttpResponseHandler orderDetailHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "getOrderDetailByOrderId result = "
					+ responseString);
			if (progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultOrderInfo orderInfoResult = JSON.parseObject(
						responseString, ResultOrderInfo.class);
				if (orderInfoResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					item = orderInfoResult.getOrderInfo();
					setLayoutData();
				} else {
					PublicUtil.showErrorMsg(main, orderInfoResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if (progressDialog != null)
				progressDialog.dismiss();
			PublicUtil.showToastServerOvertime();
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

}
