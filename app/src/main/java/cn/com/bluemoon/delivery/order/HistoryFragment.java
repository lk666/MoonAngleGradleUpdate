package cn.com.bluemoon.delivery.order;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.HistoryOrderType;
import cn.com.bluemoon.delivery.app.api.model.Order;
import cn.com.bluemoon.delivery.app.api.model.ResultOrder;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.extract.HistoryOrderDetailActivity;
import cn.com.bluemoon.delivery.order.TimerFilterWindow.TimerFilterListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase.Mode;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SimpleDateFormat")
public class HistoryFragment extends Fragment {
	private String TAG = "HistoryFragment";
	private HistoryAdapter adapter;
	private FragmentActivity main;
	private CommonProgressDialog progressDialog;
	private PullToRefreshListView listView;
	private LinearLayout layoutDate;
	private TextView txtCount;
	private TextView txtPrice;
	private ResultOrder item;
	private boolean isEnd;
	public static HistoryOrderType ordertype;
	View popStart;
	private boolean pullDown;
	private boolean pullUp;
	private long startTime = 0;
	private long endTime = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		main = getActivity();
		View v = inflater.inflate(R.layout.fragment_tab_history, container,
				false);
		popStart = (View) v.findViewById(R.id.view_pop_start);
		layoutDate = (LinearLayout) v.findViewById(R.id.layout_date);
		txtCount = (TextView) v.findViewById(R.id.txt_count);
		txtPrice = (TextView) v.findViewById(R.id.txt_price);
		setCountAndPrice(0, null);
		listView = (PullToRefreshListView) v
				.findViewById(R.id.listView_history);
		progressDialog = new CommonProgressDialog(main);
		adapter = new HistoryAdapter(main);
		layoutDate.setOnClickListener(onClicker);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				pullDown = true;
				pullUp = false;
				getItem();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				pullUp = true;
				pullDown = false;
				getItem();
			}

		});
		pullDown = false;
		pullUp = false;
		getItem();
		return v;
	}

	private void getItem() {
		String token = ClientStateManager.getLoginToken(main);
		if (!pullDown && !pullUp && progressDialog != null) {
			progressDialog.show();
		}
		long timestamp = 0;
		if (!pullUp) {
			isEnd = false;
		} else if (item != null) {
			timestamp = item.getTimestamp();
		}
		DeliveryApi.getHistoryOrders(token, ordertype.toString(),
				AppContext.PAGE_SIZE, timestamp, startTime, endTime, historyHandler);
	}

	private void setData(ResultOrder result) {
		if (result == null) {
			if (!pullUp) {
				PublicUtil.showToastErrorData(main);
			}
			return;
		}
		if (item == null || !pullUp) {
			item = result;
		} else {
			List<Order> list = result.getItemList();
			if (list == null) {
				list = new ArrayList<Order>();
			}
			if (list.size() < AppContext.PAGE_SIZE) {
				isEnd = true;
			} else {
				isEnd = false;
			}
			item.getItemList().addAll(list);
			item.setTotalAmount(result.getTotalAmount());
			item.setTotalCount(result.getTotalCount());
			item.setTimestamp(result.getTimestamp());
		}
		setCountAndPrice(item.getTotalCount(), item.getTotalAmount());
		List<Order> list = item.getItemList();
		if (list == null) {
			list = new ArrayList<Order>();
		}
		if (list.size() < AppContext.PAGE_SIZE) {
			isEnd = true;
		}
		adapter.setList(list);
		if (!pullUp) {
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	OnClickListener onClicker = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (PublicUtil.isFastDoubleClick()) {
				return;
			}
			LibViewUtil.hideIM(v);
			if (v == layoutDate) {
				//showDatePickerDialog();
				TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new TimerFilterListener() {
					@Override
					public void callBack(long startDate, long endDate) {
						startTime = startDate;
						endTime = endDate;
						pullDown = false;
						pullUp = false;
						getItem();
					}
				});
				popupWindow.showPopwindow(popStart);
			}
		}
	};

	private void initCustomActionBar() {
		new CommonActionBar(getActivity().getActionBar(),
				new IActionBarListener() {

					@Override
					public void onBtnRight(View v) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onBtnLeft(View v) {
						// TODO Auto-generated method stub
						getActivity().finish();
					}

					@Override
					public void setTitle(TextView v) {
						// TODO Auto-generated method stub
						v.setText(R.string.tab_history);
					}
				});
	}

	@SuppressLint("InflateParams")
	class HistoryAdapter extends BaseAdapter {

		private Context context;
		private List<Order> lists;

		public HistoryAdapter(Context context) {
			this.context = context;
		}

		public void setList(List<Order> list) {
			this.lists = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (lists.size() == 0 || isEnd) {
				listView.setMode(Mode.PULL_FROM_START);
				return lists.size() + 1;
			}
			listView.setMode(Mode.BOTH);
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflate = LayoutInflater.from(context);
			if (lists.size() == 0) {
				View viewEmpty = inflate.inflate(R.layout.layout_no_data, null);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, listView.getHeight());
				viewEmpty.setLayoutParams(params);
				return viewEmpty;
			}
			if (isEnd && position == lists.size()) {
				View viewEnd = inflate
						.inflate(R.layout.list_notmore_item, null);
				return viewEnd;
			}
			// if (convertView == null) {
			convertView = inflate.inflate(R.layout.order_history_item, null);
			// }

			TextView txtOrderid = (TextView) convertView
					.findViewById(R.id.txt_orderid);
			TextView txtAddress = (TextView) convertView
					.findViewById(R.id.txt_address);
			TextView txtPrice = (TextView) convertView
					.findViewById(R.id.txt_price);
			TextView txtSignDate = (TextView) convertView
					.findViewById(R.id.txt_sign_date);
			txtOrderid.setText(lists.get(position).getOrderId());
			txtPrice.setText(getString(R.string.order_money_sign) + lists.get(position).getTotalPrice());
			String date = DateUtil.getTimeStringByCustTime(lists.get(position).getSignTime(), "yyyy-MM-dd HH:mm:ss");
			txtSignDate.setText(getString(R.string.history_order_receipt_date) + date);
			if (HistoryFragment.ordertype.equals(HistoryOrderType.dispatch)) {
				txtAddress.setText(lists.get(position).getAddress());
			} else if (HistoryFragment.ordertype
					.equals(HistoryOrderType.pickup)) {
				txtSignDate.setVisibility(View.GONE);
				txtAddress.setText(getString(R.string.history_order_scene_receipt_date) + LibDateUtil.getTimeStringByCustTime(lists.get(position).getSignTime(),"yyyy-MM-dd HH:mm:ss"));
			}

			int index = position % 2;
			if (index == 1) {
				convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
			} else {
				convertView
						.setBackgroundResource(R.drawable.list_item_white_bg);
				// if(position == lists.size()-1){
				// convertView.findViewById(R.id.line_end).setVisibility(View.VISIBLE);
				// }
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (ordertype.equals(HistoryOrderType.dispatch)) {
						PublicUtil.showOrderDetailView(main, lists
								.get(position).getOrderId());
					} else if (ordertype.equals(HistoryOrderType.pickup)) {
						Intent intent = new Intent();
						intent.setClass(main, HistoryOrderDetailActivity.class);
						intent.putExtra("orderId", lists.get(position)
								.getOrderId());
						startActivity(intent);
					}
				}
			});
			return convertView;
		}

	}

	AsyncHttpResponseHandler historyHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "getHistoryOrder result = " + responseString);
			if (progressDialog != null)
				progressDialog.dismiss();
			listView.onRefreshComplete();
			try {
				ResultOrder orderResult = JSON.parseObject(responseString,
						ResultOrder.class);
				if (orderResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					setData(orderResult);
				} else {
					PublicUtil.showErrorMsg(main, orderResult);
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
			listView.onRefreshComplete();
			// setData(getResult(AppContext.PAGE_SIZE));
			PublicUtil.showToastServerOvertime();
		}
	};
	
	private void setCountAndPrice(int size,String price){
		String count = "0";
		if (size > 99) {
			count = "99+";
		} else {
			count = String.valueOf(size);
		}
		count = String.format(getString(R.string.order_history_totalcount), count);
		if (StringUtils.isEmpty(price)||"0".equals(price)
				||"0.0".equals(price)) {
			price = "0.00";
		}else if (price.length() > 9) {
			price = "999999.99+";
		}
		price = String.format(getString(R.string.order_history_price), price);
		txtCount.setText(count);
		txtPrice.setText(price);
	}

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

}
