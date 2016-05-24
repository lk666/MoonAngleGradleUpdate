package cn.com.bluemoon.delivery.order;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.DialogInterface;
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
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.entity.OrderType;
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

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class PendingOrdersFragment extends Fragment {
	private String TAG = "PendingOrdersFragment";
	private CommonActionBar mActionbar;
	private PullToRefreshListView listView;
	private OrdersAdapter ordersAdapter;
	private List<OrderVo> orderList;
	private OrdersTabActivity mContext;
	private CommonProgressDialog progressDialog;
	private boolean lock;
	private OrderVo orderClicked;
	
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		this.mContext = (OrdersTabActivity) activity;
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		progressDialog = new CommonProgressDialog(mContext);
		View v = inflater.inflate(R.layout.fragment_tab_orders, container, false);
		listView = (PullToRefreshListView)v.findViewById(R.id.listview_orders);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				mContext.setAmountShow();
				DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGORDERS, getOrdersHandler);
			}
		});
		progressDialog.show();
		mContext.setAmountShow();
		DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGORDERS, getOrdersHandler);
		return v;
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
					ordersAdapter = new OrdersAdapter(mContext, R.layout.order_list_item);
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
	AsyncHttpResponseHandler accceptOrderHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test","accceptOrderHandler result = " + responseString);
			progressDialog.dismiss();
			lock = false;
			try {
				ResultOrderVo result = JSON.parseObject(responseString, ResultOrderVo.class);
				if(result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					mContext.setAmountShow();
					PublicUtil.showToast(mContext, result.getResponseMsg());
					orderList.remove(orderClicked);
					ordersAdapter.notifyDataSetChanged();
				} else if (result.getResponseCode() == 4102) {
					new CommonAlertDialog.Builder(mContext)
					.setMessage(result.getResponseMsg())
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									progressDialog.show();
									mContext.setAmountShow();
									DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGORDERS, getOrdersHandler);
								}
							}).show();
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

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); 
	}
	
	private void initCustomActionBar() {
		mActionbar = new CommonActionBar(mContext.getActionBar(), new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {

			}

			@Override
			public void onBtnLeft(View v) {
				mContext.finish();
			}

			@Override
			public void setTitle(TextView v) {
				v.setText(getText(R.string.tab_orders));
			}

		});
	}
	
	
	public class OrdersAdapter extends BaseAdapter {
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

				txtContent.setText(R.string.pending_order_get_null);
				return convertView;
			}
			final OrderVo order = orderList.get(position);
			TextView txtDispatchId = ViewHolder.get(convertView, R.id.txt_dispatchId);
			TextView txtAddress = ViewHolder.get(convertView, R.id.txt_address);
			TextView txtCateAmount = ViewHolder.get(convertView, R.id.txt_cateAmount);
			TextView txtTotalAmount = ViewHolder.get(convertView, R.id.txt_totalAmount);
			TextView txtTotalPrice = ViewHolder.get(convertView, R.id.txt_totalPrice);
			LinearLayout layoutDetail = ViewHolder.get(convertView, R.id.layout_detail);
			TextView txtPaytime = ViewHolder.get(convertView, R.id.txt_paytime);
			Button receivingOrders = ViewHolder.get(convertView, R.id.receiving_orders_action);
			txtPaytime.setText(String.format(getString(R.string.pending_order_pay_time), order.getPayOrderTime()));
			txtDispatchId.setText(order.getOrderId());
			txtAddress.setText(String.format("%s%s", order.getRegion(),order.getAddress()));
			txtCateAmount.setText(String.format(getString(R.string.pending_order_total_kinds),order.getCateAmount()));
			txtTotalAmount.setText(String.format(getString(R.string.pending_order_total_amount),order.getTotalAmount()));
			txtTotalPrice.setText(String.format(getString(R.string.pending_order_total_price), StringUtil.formatPrice(order.getTotalPrice())));
			
			layoutDetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PublicUtil.showOrderDetailView(mContext, order.getOrderId());
				}
			});
			receivingOrders.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!lock) {
						lock = true;
						orderClicked = order;
						progressDialog.show();
						DeliveryApi.acceptOrder(ClientStateManager.getLoginToken(mContext), 
								order.getOrderId(), accceptOrderHandler);
					}
				}
			});
			return convertView;
		}
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG); 
	}

}
