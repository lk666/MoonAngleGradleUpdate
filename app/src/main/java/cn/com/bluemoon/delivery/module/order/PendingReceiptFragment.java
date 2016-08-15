package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenu;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenuCreator;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenuItem;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenuListView;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenuListView.OnMenuItemClickListener;
import cn.com.bluemoon.lib.swipe.refresh.DTBaseAdapter;
import cn.com.bluemoon.lib.swipe.refresh.DTListViewListener;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

@SuppressWarnings("rawtypes")
public class PendingReceiptFragment extends Fragment implements
		DTListViewListener {
	private static final String TAG = "PendingReceiptFragment";

	private OrdersTabActivity mContext;
	private CommonProgressDialog progressDialog;
	private SwipeMenuListView listView;
	private List<OrderVo> orderList;
	private OrdersAdapter ordersAdapter;
	private int REDIRECT_TO_RETURN_ACTIVITY = 1;
	private OrderVo orderClicked;
	View popStart;

	// private List<OrderResponse> orderList = new
	// ArrayList<OrderJsonResult.OrderResponse>();

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = (OrdersTabActivity) activity;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		progressDialog = new CommonProgressDialog(mContext);
		View v = inflater.inflate(R.layout.fragment_tab_receipt, container,
				false);
		popStart = v.findViewById(R.id.view_pop_start);
		listView = (SwipeMenuListView) v.findViewById(R.id.listview_orders);
		listView.setPullRefreshEnable(true);
		listView.setXListViewListener(this);
		//initList();
		progressDialog.show();
		mContext.setAmountShow();
		DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext),
				OrderType.PENDINGRECEIPT, getOrdersHandler);
		return v;
	}

	private void initList(boolean isEmpty) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem returnGoodsItem = new SwipeMenuItem(mContext);
				returnGoodsItem.setBackground(R.color.line_solid_deep_bg);
				returnGoodsItem.setWidth(OrdersUtils.dp2px(70, mContext));
				returnGoodsItem.setTitle(R.string.pending_order_return_btn);
				returnGoodsItem.setTitleSize(16);
				returnGoodsItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(returnGoodsItem);
			}
		};
		if (!isEmpty) {
			// set creator
			listView.setMenuCreator(creator);

			// step 2. listener item click event
			listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(int position, SwipeMenu menu,
						int index) {
					switch (index) {
					case 0:
						orderClicked = orderList.get(position);

						Intent intent = new Intent(mContext,
								ReturnOrderActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("order", orderClicked);

						intent.putExtras(bundle);
						PendingReceiptFragment.this.startActivityForResult(intent,
								REDIRECT_TO_RETURN_ACTIVITY);

						break;
					case 1:
						break;
					}
					return false;
				}
			});
		} else {
			listView.setMenuCreator(null);
		}
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REDIRECT_TO_RETURN_ACTIVITY){
			if(resultCode == Activity.RESULT_OK){

				PublicUtil.showToast(getString(R.string.pending_order_return_sucessful));
				mContext.setAmountShow();
				orderList.remove(orderClicked);
				if (orderList != null && orderList.size() > 0) {
					ordersAdapter.notifyDataSetChanged();
				} else {
					initList(true);
					OrdersEmptyAdapter ordersEmptyAdapter = new OrdersEmptyAdapter(mContext);
					listView.setAdapter(ordersEmptyAdapter);
				}
				
			}
		}else if(requestCode == Constants.REQUEST_SCAN){
			if(resultCode == Activity.RESULT_CANCELED){
				return;
			}else if(resultCode == Activity.RESULT_OK){

				String resultStr = data
						.getStringExtra(LibConstants.SCAN_RESULT);
				DeliveryApi.orderSign(ClientStateManager.getLoginToken(mContext), orderClicked.getOrderId(), "scan", resultStr, orderSignHandler);
				//PublicUtil.showToast(resultStr);
			}else if(resultCode == 3){
				PublicUtil.openScanOrder(getActivity(), PendingReceiptFragment.this,
						getString(R.string.pending_order_receive_sign_title),
						getString(R.string.pending_order_receive_sign_scan_btn),
						Constants.REQUEST_SCAN, 4);
			}else if(resultCode == 4){

				Intent intent = new Intent(getActivity(),SignActivity.class);
				intent.putExtra("orderId", orderClicked.getOrderId());
				PendingReceiptFragment.this.startActivityForResult(intent, Constants.REQUEST_SCAN);
			} else if (resultCode == 2) {
				mContext.setAmountShow();
				orderList.remove(orderClicked);
				if (orderList != null && orderList.size() > 0) {
					ordersAdapter.notifyDataSetChanged();
				} else {
					initList(true);
					OrdersEmptyAdapter ordersEmptyAdapter = new OrdersEmptyAdapter(mContext);
					listView.setAdapter(ordersEmptyAdapter);
				}
			}
		} 
	};
	
	AsyncHttpResponseHandler orderSignHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "OrderSignHandler result = " + responseString);
			try {
				ResultBase result = JSON.parseObject(responseString, ResultBase.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					PublicUtil.showToast(result.getResponseMsg());
					mContext.setAmountShow();
					orderList.remove(orderClicked);
					if (orderList != null && orderList.size() > 0) {
						ordersAdapter.notifyDataSetChanged();
					} else {
						initList(true);
						OrdersEmptyAdapter ordersEmptyAdapter = new OrdersEmptyAdapter(mContext);
						listView.setAdapter(ordersEmptyAdapter);
					}
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
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

	AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "getOrdersHandler result = " + responseString);
			progressDialog.dismiss();
			listView.stopRefresh();
			try {
				ResultOrderVo result = JSON.parseObject(responseString,
						ResultOrderVo.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					orderList = result.getItemList();
					if (orderList != null && orderList.size() > 0) {
						initList(false);
						ordersAdapter = new OrdersAdapter(mContext, R.layout.order_list_item4);
						listView.setAdapter(ordersAdapter);
					} else {
						initList(true);
						OrdersEmptyAdapter ordersEmptyAdapter = new OrdersEmptyAdapter(mContext);
						listView.setAdapter(ordersEmptyAdapter);
					}
					
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
			listView.stopRefresh();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

	private void initCustomActionBar() {
		CommonActionBar mActionbar = new CommonActionBar(mContext.getActionBar(),
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
						v.setText(getText(R.string.tab_receipt));
					}

				});
	}
	
	private class OrdersEmptyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		public OrdersEmptyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.layout_no_data, null);
			TextView txtContent = ViewHolder.get(convertView, R.id.txt_content);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, listView.getHeight());
			ViewGroup.LayoutParams p = txtContent.getLayoutParams();
			p.height = listView.getHeight(); 
			txtContent.setLayoutParams(p);
			//convertView.setLayoutParams(p);

			txtContent.setText(R.string.pending_order_sign_null);
			return convertView;
		}
		
	}

	private class OrdersAdapter extends DTBaseAdapter<OrderVo> {
		private Context mContext;
		private LayoutInflater mInflater;
		private int layoutID;

		public OrdersAdapter(Context context, int layoutID) {
			this.mInflater = LayoutInflater.from(context);
			this.layoutID = layoutID;
			this.mContext = context;
		}

		@Override
		public int getCount() {
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
			LinearLayout layoutDetail = ViewHolder.get(convertView,
					R.id.layout_detail);

			TextView txtPayTime = ViewHolder.get(convertView, R.id.txt_paytime);
			TextView txtSubscribeTime = ViewHolder.get(convertView,
					R.id.txt_subscribe_time);
			TextView txtCustomerName = ViewHolder.get(convertView,
					R.id.txt_customerName);
			TextView txtMobilePhone = ViewHolder.get(convertView,
					R.id.txt_mobilePhone);
			TextView txtStorehouse = ViewHolder.get(convertView,
					R.id.txt_storehouse);
			TextView txtEditTime = ViewHolder.get(convertView,
					R.id.txt_edit_appointment_time);
			Button btnAction = ViewHolder.get(convertView, 
					R.id.delivery_action);

			txtCustomerName.setText(OrdersUtils.formatLongString(
					order.getCustomerName(), txtCustomerName));
			txtPayTime.setText(String.format(
					mContext.getString(R.string.pending_order_pay_time),
					order.getPayOrderTime()));
			txtSubscribeTime.setText(String.format(
					mContext.getString(R.string.pending_order_subscribe_time),
					order.getSubscribeTime()));
			txtMobilePhone.setText(order.getMobilePhone());
			txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			txtMobilePhone.getPaint().setAntiAlias(true);
			txtStorehouse.setText(OrdersUtils.getStorehouseString(order,
					mContext));

			txtDispatchId.setText(order.getOrderId());
			txtAddress.setText(String.format("%s%s", order.getRegion(),
					order.getAddress()));
			txtCateAmount.setText(String.format(
					mContext.getString(R.string.pending_order_total_kinds),
					order.getCateAmount()));
			txtTotalAmount.setText(String.format(
					mContext.getString(R.string.pending_order_total_amount),
					order.getTotalAmount()));
			txtTotalPrice.setText(String.format(
					mContext.getString(R.string.pending_order_total_price),
					StringUtil.formatPrice(order.getTotalPrice())));
			txtEditTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			txtEditTime.getPaint().setAntiAlias(true);
			txtEditTime.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					orderClicked = order;
					ChoiceOrderDatePopupWindow popupWindow = new ChoiceOrderDatePopupWindow(
							mContext, order, orderChoiceDateListener);
					popupWindow.showPopwindow(popStart);

				}
			});

			txtMobilePhone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PublicUtil.showCallPhoneDialog(getActivity(), order.getMobilePhone());
				}
			});

			layoutDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PublicUtil.showOrderDetailView(mContext, order.getOrderId());
				}
			});
			
			btnAction.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					orderClicked = order;
					PublicUtil.openScanOrder(getActivity(), PendingReceiptFragment.this,
							getString(R.string.pending_order_receive_sign_title),
							getString(R.string.pending_order_receive_sign_scan_btn),
							Constants.REQUEST_SCAN, 4);
				}
			});

			return convertView;
		}

	}

	@Override
	public void onDTListLoadMore() {

	}

	@Override
	public void onDTListRefresh() {
		mContext.setAmountShow();
		DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext),
				OrderType.PENDINGRECEIPT, getOrdersHandler);
	}

	IOrderChoiceDateListener orderChoiceDateListener = new IOrderChoiceDateListener() {

		@Override
		public void Choise(OrderVo orderVo, long time, String formatTime) {

			progressDialog.show();

			orderVo.setSubscribeTime(formatTime);
			DeliveryApi
					.updateOrAppointmentDeliveryTime(
							ClientStateManager.getLoginToken(mContext),
							orderVo.getOrderId(), time,
							OrderType.PENDINGAPPOINTMENT.getType(),
							appointOrderHandler);
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
			boolean lock = false;
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(mContext);
		}
	};

}
