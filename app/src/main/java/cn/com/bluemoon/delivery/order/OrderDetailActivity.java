package cn.com.bluemoon.delivery.order;

import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.adapter.OrderProductAdapter;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderInfo;
import cn.com.bluemoon.delivery.app.api.model.OrderState;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class OrderDetailActivity extends KJActivity {

	private String TAG = "OrderDetailActivity";
	@BindView(id = R.id.listview_product)
	private ListView listviewProduct;
	@BindView(id = R.id.txt_orderid)
	private TextView txtOrderid;
	@BindView(id = R.id.txt_source)
	private TextView txtSource;
	@BindView(id = R.id.txt_payOrdertime)
	private TextView txtPayOrderTime;
	@BindView(id = R.id.txt_subscribetime)
	private TextView txtSubscribeTime;
	@BindView(id = R.id.txt_deliverytime)
	private TextView txtDeliveryTime;
	@BindView(id = R.id.txt_signtime)
	private TextView txtSignTime;
	@BindView(id = R.id.txt_warehouse)
	private TextView txtWarehouse;
	@BindView(id = R.id.layout_subscribe)
	private LinearLayout layoutSubscribe;
	@BindView(id = R.id.layout_delivery)
	private LinearLayout layoutDelivery;
	@BindView(id = R.id.layout_sign)
	private LinearLayout layoutSign;
	@BindView(id = R.id.layout_warehouse)
	private LinearLayout layoutWarehouse;
	@BindView(id = R.id.txt_customername)
	private TextView txtCustomerName;
	@BindView(id = R.id.txt_phone)
	private TextView txtPhone;
	@BindView(id = R.id.txt_address)
	private TextView txtAddress;
	@BindView(id = R.id.txt_totalprice)
	private TextView txtTotalPrice;
	private CommonProgressDialog progressDialog;
	private String orderId;
	private OrderInfo item;
	private OrderProductAdapter adapter;

	@Override
	public void setRootView() {
		initCustomActionBar();
		setContentView(R.layout.order_details);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);
		if (getIntent() != null) {
			orderId = getIntent().getStringExtra("orderId");
		}
		String token = ClientStateManager.getLoginToken(aty);
		if (StringUtils.isEmpty(orderId)||StringUtil.isEmpty(token)) {
			PublicUtil.showToastErrorData(aty);
			return;
		}
		DeliveryApi.getOrderDetailByOrderId(token, orderId, orderDetailHandler);
	}


	public void setLayoutData() {
		if(item==null){
			return;
		}
		setLayout(item.getDispatchStatus());
		txtPayOrderTime.setText(item.getPayOrderTime());
		txtSubscribeTime.setText(item.getSubscribeTime());
		txtDeliveryTime.setText(item.getDeliveryTime());
		txtSignTime.setText(item.getSignTime());
		txtOrderid.setText(item.getOrderId());
		txtSource.setText(item.getSource());
		txtWarehouse.setText(getWarehouseStr());
		txtCustomerName.setText(item.getCustomerName());
		txtPhone.setText(item.getMobilePhone());
		txtAddress.setText(item.getRegion()+item.getAddress());
		txtTotalPrice.setText(String.format(
				getString(R.string.extract_order_total_pay), 
				PublicUtil.getPriceFrom(item.getTotalPrice())));
		if (item.getProductList() != null) {
			adapter = new OrderProductAdapter(aty, item.getProductList());
			listviewProduct.setAdapter(adapter);
		} else {
			PublicUtil.showToast(R.string.return_change_get_detail_fail);
		}
		LibViewUtil.setListViewHeight2(listviewProduct);
	}
	
	public void setTxtPhoneStyle(){
		txtPhone.setTextColor(getResources().getColor(R.color.text_blue));
		txtPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtPhone.getPaint().setAntiAlias(true);
		txtPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PublicUtil.showCallPhoneDialog(aty, txtPhone.getText().toString());
			}
		});
	}

	public void setLayout(String state) {
		if(OrderState.ACCEPT.toString().equals(state)){
			layoutWarehouse.setVisibility(View.GONE);
		}else if(OrderState.APPOINTMENT.toString().equals(state)){
			setTxtPhoneStyle();
		}else if(OrderState.DELIVERY.toString().equals(state)){
			layoutSubscribe.setVisibility(View.VISIBLE);
		}else if(OrderState.SIGN.toString().equals(state)){
			layoutSubscribe.setVisibility(View.VISIBLE);
			layoutDelivery.setVisibility(View.VISIBLE);
			setTxtPhoneStyle();
		}else if(OrderState.HISTORY.toString().equals(state)
				||OrderState.PICKUP.toString().equals(state)){
			layoutSubscribe.setVisibility(View.VISIBLE);
			layoutDelivery.setVisibility(View.VISIBLE);
			layoutSign.setVisibility(View.VISIBLE);
		}
	}

	private String getWarehouseStr() {
		String str = getString(R.string.none);
		if (item != null) {
			if (item.getStorehouseCode() == null
					&& item.getStorehouseName() == null
					&& item.getStorechargeName() == null) {
				str = getString(R.string.none);
			} else {
				StringBuffer strBuffer = new StringBuffer();
				String storehouseCode = StringUtil.isEmptyString(item
						.getStorehouseCode()) ? "" : item.getStorehouseCode();
				String storehouseName = StringUtil.isEmptyString(item
						.getStorehouseName()) ? "" : item.getStorehouseName();
				String storechargeName = StringUtil.isEmptyString(item
						.getStorechargeName()) ? "" : item.getStorechargeName();
				// String storechargeMobileno =
				// StringUtil.isEmptyString(item.getStorechargeMobileno())?"":item.getStorechargeMobileno();
				strBuffer.append(storehouseCode);
				if (!StringUtils.isEmpty(storehouseName))
					strBuffer.append("-").append(storehouseName);
				if (!StringUtils.isEmpty(storechargeName))
					strBuffer.append("-").append(storechargeName);
				// if(!StringUtils.isEmpty(storechargeMobileno))
				// strBuffer.append("\n").append(storechargeMobileno);
				str = strBuffer.toString();
			}
		}
		return str;
	}

	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				v.setText(getResources().getString(R.string.tab_detail));
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
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"getOrderDetailByOrderId result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultOrderInfo orderInfoResult = JSON.parseObject(responseString,ResultOrderInfo.class);
				if(orderInfoResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					item = orderInfoResult.getOrderInfo();
					setLayoutData();
				}else{
					PublicUtil.showErrorMsg(aty, orderInfoResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, 
				Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if(progressDialog != null)
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
