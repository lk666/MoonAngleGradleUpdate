package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.other.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrderVo;
import cn.com.bluemoon.delivery.app.api.model.other.Storehouse;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PendingDeliveryFragment extends Fragment {
	private String TAG = "PendingDeliveryFragment";
	private PullToRefreshListView listView;
	private OrdersAdapter ordersAdapter;
	private List<OrderVo> orderList;
	private OrdersTabActivity mContext;
	private CommonProgressDialog progressDialog;
	private boolean lock;
	private OrderVo orderClicked;
	private OrderVo orderEdit;
	
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		this.mContext = (OrdersTabActivity) activity;
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		progressDialog = new CommonProgressDialog(mContext);
		View v = inflater.inflate(R.layout.fragment_tab_delivery, container, false);
		listView = (PullToRefreshListView) v.findViewById(R.id.listview_orders);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				mContext.setAmountShow();
				DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGDELIVERY, getOrdersHandler);
			}
		});
		progressDialog.show();
		mContext.setAmountShow();
		DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGDELIVERY, getOrdersHandler);
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			int index = orderList.indexOf(orderEdit);
			Storehouse storehouse = (Storehouse) data.getSerializableExtra("storehouse");
			if (orderEdit != null && storehouse != null) {
				orderEdit.setStorechargeCode(storehouse.getStorechargeCode());
				orderEdit.setStorechargeName(storehouse.getStorechargeName());
				orderEdit.setStorehouseCode(storehouse.getStorehouseCode());
				orderEdit.setStorehouseName(storehouse.getStorehouseName());
				orderList.set(index, orderEdit);
				ordersAdapter.notifyDataSetChanged();
			}
		}
	}
	
	AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test","getOrdersHandler result = " + responseString);
			progressDialog.dismiss();
			listView.onRefreshComplete();
			try {
				ResultOrderVo result = JSON.parseObject(responseString, ResultOrderVo.class);
				if(result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					orderList = result.getItemList();
					ordersAdapter = new OrdersAdapter(mContext, R.layout.order_list_item3);
					listView.setAdapter(ordersAdapter);
				}else{
					PublicUtil.showErrorMsg(mContext, result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy(mContext);
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, 
				Throwable throwable) {
			LogUtils.d("test","getOrdersHandler result failed. statusCode="+statusCode);
			progressDialog.dismiss();
			listView.onRefreshComplete();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};
	
	AsyncHttpResponseHandler deliveryHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test","deliveryHandler result = " + responseString);
			progressDialog.dismiss();
			lock = false;
			try {
				ResultBase result = JSON.parseObject(responseString, ResultBase.class);
				if(result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					mContext.setAmountShow();
					PublicUtil.showToast(mContext, result.getResponseMsg());
					orderList.remove(orderClicked);
					ordersAdapter.notifyDataSetChanged();
				}else{
					PublicUtil.showErrorMsg(mContext, result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy(mContext);
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, 
				Throwable throwable) {
			lock = false;
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

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
				v.setText(getText(R.string.tab_delivery));
			}

		});
	}
	
	class OrdersAdapter extends BaseAdapter {
		private Context mContext;
	    private LayoutInflater mInflater;  
	    private int layoutID;
	    
	    public OrdersAdapter(Context context, int layoutID) { 
	        this.mInflater = LayoutInflater.from(context);  
	        this.layoutID = layoutID;  
	        this.mContext=context;
	    }
	    
		@Override
		public int getCount() {
			if (orderList != null && orderList.size() == 0) {
				this.layoutID = R.layout.layout_no_data;
				return 1;
			}
			return orderList.size();
		}

		@Override
		public OrderVo getItem(int position) {
			return orderList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null || (orderList != null && orderList.size() == 0)) {
				convertView = mInflater.inflate(layoutID, null);
			}
			if (orderList != null && orderList.size() == 0) {
				TextView txtContent = ViewHolder.get(convertView, R.id.txt_content);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, listView.getHeight());
				convertView.setLayoutParams(params);

				txtContent.setText(R.string.pending_order_delivery_null);
				return convertView;
			}
			final OrderVo order = orderList.get(position);
			TextView txtDispatchId = ViewHolder.get(convertView, R.id.txt_dispatchId);
			TextView txtAddress = ViewHolder.get(convertView, R.id.txt_address);
			TextView txtCateAmount = ViewHolder.get(convertView, R.id.txt_cateAmount);
			TextView txtTotalAmount = ViewHolder.get(convertView, R.id.txt_totalAmount);
			TextView txtTotalPrice = ViewHolder.get(convertView, R.id.txt_totalPrice);
			final LinearLayout layoutDetail = ViewHolder.get(convertView, R.id.layout_detail);

			TextView txtPayTime =  ViewHolder.get(convertView, R.id.txt_paytime);
			TextView txtSubscribeTime =  ViewHolder.get(convertView, R.id.txt_subscribe_time);
			TextView txtCustomerName = ViewHolder.get(convertView, R.id.txt_customerName);
			TextView txtMobilePhone = ViewHolder.get(convertView, R.id.txt_mobilePhone);
			TextView txtStorehouse = ViewHolder.get(convertView, R.id.txt_storehouse);
			final Button btndelivery = ViewHolder.get(convertView, R.id.delivery_action);
			final LinearLayout layoutStorehouse = ViewHolder.get(convertView, R.id.layout_storehouse);
			
			txtCustomerName.setText(OrdersUtils.formatLongString(
					order.getCustomerName(), txtCustomerName));
			txtPayTime.setText(String.format(mContext.getString(R.string.pending_order_pay_time), order.getPayOrderTime()));
			txtSubscribeTime.setText(String.format(mContext.getString(R.string.pending_order_subscribe_time), order.getSubscribeTime()));
			txtMobilePhone.setText(order.getMobilePhone());
			txtStorehouse.setText(OrdersUtils.getStorehouseString(order,mContext));

			txtDispatchId.setText(order.getOrderId());
			txtAddress.setText(String.format("%s%s", order.getRegion(), order.getAddress()));
			txtCateAmount.setText(String.format(mContext.getString(R.string.pending_order_total_kinds),order.getCateAmount()));
			txtTotalAmount.setText(String.format(mContext.getString(R.string.pending_order_total_amount),order.getTotalAmount()));
			txtTotalPrice.setText(String.format(mContext.getString(R.string.pending_order_total_price), StringUtil.formatPrice(order.getTotalPrice())));
			
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (!lock) {
						lock = true;
						if (v == layoutDetail) {
							PublicUtil.showOrderDetailView(mContext, order.getOrderId());
							lock = false;
						} else if (v == btndelivery) {
							new CommonAlertDialog.Builder(mContext)
							.setMessage(R.string.pending_order_delivery_or_not)
							.setNegativeButton(R.string.yes,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											progressDialog.show();
											orderClicked = order;
											DeliveryApi.toDelivery(ClientStateManager.getLoginToken(mContext), order.getOrderId(), deliveryHandler);
										}
									}).setPositiveButton(R.string.no, null)
							.show();
							lock = false;
						} else if (v == layoutStorehouse) {
							orderEdit = order;
							Intent intent = new Intent();
							intent.setClass(mContext, SelectStoreHouseActivity.class);
							intent.putExtra("dispatchId", order.getDispatchId());
							intent.putExtra("code", order.getStorehouseCode());
							PendingDeliveryFragment.this.startActivityForResult(intent, 0);
							lock = false;
						} else {
							lock = false;
						}
					}
					
				}
			};
			
			
			layoutDetail.setOnClickListener(listener);
			btndelivery.setOnClickListener(listener);
			layoutStorehouse.setOnClickListener(listener);
			
			return convertView;

		}
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
