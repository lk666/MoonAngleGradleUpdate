package cn.com.bluemoon.delivery.order;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.app.api.model.Storehouse;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.async.listener.IOrderChoiceDateListener;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.OrdersUtils;
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

public class PendingAppointmentFragment extends Fragment {
	private String TAG = "PendingAppointmentFragment";
	private CommonActionBar mActionbar;
	private PullToRefreshListView listView;
	private OrdersAdapter ordersAdapter;
	private List<OrderVo> orderList;
	private OrdersTabActivity mContext;
	private CommonProgressDialog progressDialog;
	private boolean lock;
	private OrderVo orderCancel;
	private OrderVo orderAppointment;
	private OrderVo orderEdit;
	View popStart;

	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		this.mContext = (OrdersTabActivity) activity;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		progressDialog = new CommonProgressDialog(mContext);
		View v = inflater.inflate(R.layout.fragment_tab_appointment, container,
				false);
		listView = (PullToRefreshListView) v.findViewById(R.id.listview_orders);
		popStart = (View) v.findViewById(R.id.view_pop_start);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				mContext.setAmountShow();
				DeliveryApi.getOrdersByType(
						ClientStateManager.getLoginToken(mContext),
						OrderType.PENDINGAPPOINTMENT, getOrdersHandler);
			}
		});
		progressDialog.show();
		mContext.setAmountShow();
		DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext),
				OrderType.PENDINGAPPOINTMENT, getOrdersHandler);
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

	private void initCustomActionBar() {
		mActionbar = new CommonActionBar(mContext.getActionBar(),
				new IActionBarListener() {

					@Override
					public void onBtnRight(View v) {

					}

					@Override
					public void onBtnLeft(View v) {
						mContext.finish();
					}

					@Override
					public void setTitle(TextView v) {
						v.setText(getText(R.string.tab_appointment));
					}

				});
	}

	class OrdersAdapter extends BaseAdapter {
		private OrdersTabActivity mContext;
		private LayoutInflater mInflater;
		private int layoutID;

		public OrdersAdapter(OrdersTabActivity context, int layoutID) {
			this.mInflater = LayoutInflater.from(context);
			this.layoutID = layoutID;
			this.mContext = context;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null
					|| (orderList != null && orderList.size() == 0)) {
				convertView = mInflater.inflate(layoutID, null);
			}
			if (orderList != null && orderList.size() == 0) {
				TextView txtContent = ViewHolder.get(convertView,
						R.id.txt_content);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, listView.getHeight());
				convertView.setLayoutParams(params);

				txtContent.setText(R.string.pending_order_appointment_null);
				return convertView;
			}
			final OrderVo order = orderList.get(position);
			TextView txtDispatchId = ViewHolder.get(convertView,
					R.id.txt_dispatchId);
			TextView txtAddress = ViewHolder.get(convertView, R.id.txt_address);
			TextView txtCateAmount = ViewHolder.get(convertView,
					R.id.txt_cateAmount);
			TextView txtTotalAmount = ViewHolder.get(convertView,
					R.id.txt_totalAmount);
			TextView txtTotalPrice = ViewHolder.get(convertView,
					R.id.txt_totalPrice);
			TextView txtCustomerName = ViewHolder.get(convertView,
					R.id.txt_customerName);
			final TextView txtMobilePhone = ViewHolder.get(convertView,
					R.id.txt_mobilePhone);
			TextView txtPayOrderTime = ViewHolder.get(convertView,
					R.id.txt_paytime);
			final LinearLayout layoutDetail = ViewHolder.get(convertView,
					R.id.layout_detail);
			final LinearLayout layoutStorehouse = ViewHolder.get(convertView,
					R.id.layout_storehouse);
			TextView txtStorehouse = ViewHolder.get(convertView,
					R.id.txt_storehouse);
			final Button appointmentOrder = ViewHolder.get(convertView,
					R.id.appointment_action);

			final TextView txtCancleOrder = ViewHolder.get(convertView,
					R.id.txt_cancle_order);
			txtCancleOrder.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			txtCancleOrder.getPaint().setAntiAlias(true);
			txtCustomerName.setText(OrdersUtils.formatLongString(
					order.getCustomerName(), txtCustomerName));
			txtMobilePhone.setText(order.getMobilePhone());
			txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			txtMobilePhone.getPaint().setAntiAlias(true);
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!lock) {
						lock = true;
						if (v == txtMobilePhone) {
							PublicUtil.showCallPhoneDialog(mContext, order.getMobilePhone());
							lock = false;
						} else if (v == appointmentOrder) {
							orderAppointment = order;
							ChoiceOrderDatePopupWindow popupWindow = new ChoiceOrderDatePopupWindow(
									mContext, orderAppointment,
									orderChoiceDateListener);
							popupWindow.showPopwindow(popStart);
							lock=false;
						} else if (v == layoutStorehouse) {
							orderEdit = order;
							Intent intent = new Intent();
							intent.setClass(mContext,
									SelectStoreHouseActivity.class);
							intent.putExtra("dispatchId", order.getDispatchId());
							intent.putExtra("code", order.getStorehouseCode());
							PendingAppointmentFragment.this.startActivityForResult(intent, 0);
							lock = false;
						} else if (v == layoutDetail) {
							PublicUtil.showOrderDetailView(mContext, order.getOrderId());
							lock = false;
						} else if (v == txtCancleOrder) {
							new CommonAlertDialog.Builder(mContext)
							.setMessage(R.string.pending_order_get_or_not)
							.setNegativeButton(R.string.yes,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											orderCancel = order;
											progressDialog.show();
											DeliveryApi.cancelAppointmentOrder(
													ClientStateManager.getLoginToken(mContext),
													order.getOrderId(), cancelOrderHandler);
										}
									}).setPositiveButton(R.string.no, null)
							.show();
							lock = false;
						} else {
							lock = false;
						}
					}
				}
			};
			txtCancleOrder.setOnClickListener(listener);
			txtMobilePhone.setOnClickListener(listener);
			appointmentOrder.setOnClickListener(listener);
			txtPayOrderTime.setText(String.format(
					getString(R.string.pending_order_pay_time),
					order.getPayOrderTime()));
			txtStorehouse.setText(OrdersUtils.getStorehouseString(order,
					mContext));
			layoutStorehouse.setOnClickListener(listener);

			txtDispatchId.setText(order.getOrderId());
			txtAddress.setText(String.format("%s%s", order.getRegion(),
					order.getAddress()));
			txtCateAmount.setText(String.format(
					getString(R.string.pending_order_total_kinds),
					order.getCateAmount()));
			txtTotalAmount.setText(String.format(
					getString(R.string.pending_order_total_amount),
					order.getTotalAmount()));
			txtTotalPrice.setText(String.format(
					getString(R.string.pending_order_total_price),
					StringUtil.formatPrice(order.getTotalPrice())));

			layoutDetail.setOnClickListener(listener);

			return convertView;

		}
	}

	AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "getOrdersHandler result = " + responseString);
			progressDialog.dismiss();
			listView.onRefreshComplete();
			try {
				ResultOrderVo result = JSON.parseObject(responseString,
						ResultOrderVo.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					orderList = result.getItemList();
					ordersAdapter = new OrdersAdapter(mContext,
							R.layout.order_list_item2);
					listView.setAdapter(ordersAdapter);
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
			LogUtils.d("test", "getOrdersHandler result failed. statusCode="
					+ statusCode);
			progressDialog.dismiss();
			listView.onRefreshComplete();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

	AsyncHttpResponseHandler cancelOrderHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "cancelOrderHandler result = " + responseString);
			lock = false;
			progressDialog.dismiss();
			try {
				ResultOrderVo result = JSON.parseObject(responseString,
						ResultOrderVo.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					PublicUtil.showToast(mContext, result.getResponseMsg());
					mContext.setAmountShow();
					orderList.remove(orderCancel);
					ordersAdapter.notifyDataSetChanged();
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
			lock = false;
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	IOrderChoiceDateListener orderChoiceDateListener = new IOrderChoiceDateListener() {

		@Override
		public void Choise(OrderVo orderVo,long time,String formatTime) {

			progressDialog.show();
			DeliveryApi.updateOrAppointmentDeliveryTime(ClientStateManager.getLoginToken(mContext), orderVo.getOrderId(),time, 
					OrderType.PENDINGAPPOINTMENT.getType(), appointOrderHandler);
			// TODO Auto-generated method stub

		}
	};
	
	
	
	AsyncHttpResponseHandler appointOrderHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "appointOrderHandler result = " + responseString);
			
			progressDialog.dismiss();
			try {
				ResultBase result = JSON.parseObject(responseString,
						ResultOrderVo.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					PublicUtil.showToast(mContext, result.getResponseMsg());
					mContext.setAmountShow();
					orderList.remove(orderAppointment);
					ordersAdapter.notifyDataSetChanged();
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
			lock = false;
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

}
