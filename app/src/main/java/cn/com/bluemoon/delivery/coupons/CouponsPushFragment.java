package cn.com.bluemoon.delivery.coupons;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultStorehouse;
import cn.com.bluemoon.delivery.app.api.model.Storehouse;
import cn.com.bluemoon.delivery.app.api.model.coupon.Coupon;
import cn.com.bluemoon.delivery.app.api.model.coupon.CouponAct;
import cn.com.bluemoon.delivery.app.api.model.coupon.ResultCouponAct;
import cn.com.bluemoon.delivery.app.api.model.coupon.ResultUserBase;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.card.CardTabActivity;
import cn.com.bluemoon.delivery.card.PunchCardOndutyActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateTimePikcerUtil;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.OrdersUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class CouponsPushFragment extends Fragment implements OnClickListener{
	private String TAG = "CouponsPushFragment";
	private CouponsTabActivity mContext;
	private List<Coupon> coupons;
	private List<CouponAct> activitys;
	private CouponAct activitySelect;
	private CommonProgressDialog progressDialog;
	private ListView listView;
	private LinearLayout layoutActivity;
	private LinearLayout layoutConsumer;
	private LinearLayout layoutNamePhone;
	private TextView txtActivity;
	private TextView txtRegisterTime;
	private TextView txtConsumerName;
	private TextView txtPhone;
	private Button btnCancel;
	private Button btnOk;
	private CouponAdapter couponAdapter;
	private boolean submitControl;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = (CouponsTabActivity) activity;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		progressDialog = new CommonProgressDialog(mContext);
		View v = inflater.inflate(R.layout.fragment_tab_coupons_push,
				container, false);
		listView = (ListView) v.findViewById(R.id.list_coupons);
		layoutActivity = (LinearLayout) v.findViewById(R.id.layout_activity);
		layoutConsumer = (LinearLayout) v.findViewById(R.id.layout_consumer);
		layoutNamePhone = (LinearLayout) v.findViewById(R.id.layout_name_phone);
		txtActivity = (TextView) v.findViewById(R.id.txt_activity_name);
		txtRegisterTime = (TextView) v.findViewById(R.id.txt_register_time);
		txtConsumerName = (TextView) v.findViewById(R.id.txt_customername);
		txtPhone = (TextView) v.findViewById(R.id.txt_phone);
		btnCancel = (Button) v.findViewById(R.id.btn_cancel);
		btnOk = (Button) v.findViewById(R.id.btn_ok);
		layoutActivity.setOnClickListener(this);
		layoutConsumer.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		progressDialog.show();
		DeliveryApi.getOwnAuthCouponAct(ClientStateManager.getLoginToken(mContext), getOwnAuthCouponActHandler);
		return v;
	}

	AsyncHttpResponseHandler getOwnAuthCouponActHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test","getOwnAuthCouponActHandler result = " + responseString);
			progressDialog.dismiss();
			try {
				ResultCouponAct result = JSON.parseObject(responseString, ResultCouponAct.class);
				if(result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					activitys = result.getActivitys();
					if (activitys != null && activitys.size() > 0) {
						String state = ClientStateManager.getActivityCode(mContext);
						if (!"0".equals(state)) {
							for (int i = 0; i < activitys.size(); i++) {
								CouponAct act = activitys.get(i);
								if (state.equals(act.getActivityCode())) {
									activitySelect = act;
									activitySelect.setSelect(true);
									activitys.set(i, activitySelect);
									initPage();
								}
							}
						}

					}

				}else{
					PublicUtil.showErrorMsg(mContext, result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime();
		}
	};

	AsyncHttpResponseHandler getCustomerInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test","getCustomerInfoHandler result = " + responseString);
			progressDialog.dismiss();
			try {
				ResultUserBase result = JSON.parseObject(responseString, ResultUserBase.class);
				if(result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					txtRegisterTime.setText(String.format(getString(R.string.coupons_record_register_time),
							DateUtil.getTime(result.getUserBase().getRegistTime(), "yyyy-MM-dd")));
					String nickName = PublicUtil.getStringByLengh(result.getUserBase().getNickName(), 9);
					txtConsumerName.setText(nickName);
					txtPhone.setText(result.getUserBase().getMobile());
					layoutNamePhone.setVisibility(View.VISIBLE);
				}else{
					PublicUtil.showErrorMsg(mContext, result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime();
		}
	};

	AsyncHttpResponseHandler mensendCouponHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test","mensendCouponHandler result = " + responseString);
			progressDialog.dismiss();
			try {
				ResultCouponAct result = JSON.parseObject(responseString, ResultCouponAct.class);
				if(result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					PublicUtil.showMessage(mContext, getString(R.string.coupons_push_success), result.getResponseMsg());
					clearData();
				}else{
					if (result.getResponseCode() == 7203) {
						PublicUtil.showMessage(mContext, getString(R.string.coupons_push_failed),result.getResponseMsg());
					} else {
						PublicUtil.showErrorMsg(mContext, result);
					}
					submitControl = false;
				}
			} catch (Exception e) {
				submitControl = false;
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			progressDialog.dismiss();
			submitControl = false;
			PublicUtil.showToastServerOvertime();
		}
	};

	private void initPage() {
		if (activitySelect != null) {
			txtActivity.setText(activitySelect.getActivitySName());
			coupons = activitySelect.getCoupons();
			if (coupons != null && coupons.size() > 0) {
				for (int i = 0; i < coupons.size(); i++) {
					Coupon coupon = coupons.get(i);
					coupon.setSelect(false);
					coupons.set(i,coupon);
				}
			}
			couponAdapter = new CouponAdapter(mContext, R.layout.item_push_coupon_list);
			listView.setAdapter(couponAdapter);
		}
	}


	private void initCustomActionBar() {
		new CommonActionBar(mContext.getActionBar(), new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {

			}

			@Override
			public void onBtnLeft(View v) {
				mContext.finish();
			}

			@Override
			public void setTitle(TextView v) {
				v.setText(getText(R.string.coupons_tab_push));
			}

		});
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED){
			return;
		}
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case Constants.REQUEST_SCAN:
					String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
					progressDialog.show();
					DeliveryApi.getCustomerInfo(ClientStateManager.getLoginToken(mContext), resultStr, getCustomerInfoHandler);
					break;
			}
		}
	}


	@Override
	public void onClick(View v) {
		if (v == layoutActivity) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.layout_coupon_activity, null);
			ListView listView = (ListView) view.findViewById(R.id.listview_activity) ;
			if (activitys != null && activitys.size() > 0) {
				for (int i = 0; i < activitys.size(); i++) {
					CouponAct couponAct = activitys.get(i);
					if (activitySelect != null && activitySelect.getActivityCode().equals(couponAct.getActivityCode())) {
						couponAct.setSelect(true);
					} else {
						couponAct.setSelect(false);
					}
					activitys.set(i,couponAct);
				}
			}

			ActivityAdapter adapter = new ActivityAdapter(mContext, R.layout.item_activity_list);
			listView.setAdapter(adapter);

			CommonAlertDialog.Builder builder = new CommonAlertDialog.Builder(mContext).setTitle(R.string.coupons_activity_title);
			builder.setView(view);
			builder.setClearPadding(true);
			builder.setCancelable(false);
			builder.setNegativeButton(R.string.btn_ok3, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					CouponAct act = null;
					if (activitys != null && activitys.size() > 0) {
						for (CouponAct couponAct : activitys) {
							if (couponAct.isSelect()) {
								act = couponAct;
								break;
							}
						}
						if (act != null) {
							activitySelect = act;
							ClientStateManager.setActivityCode(mContext, activitySelect.getActivityCode());
							initPage();
						}
					}
				}
			});
			builder.setPositiveButton(R.string.btn_cancel, null);
			builder.show();



		} else if (v == layoutConsumer) {
			PublicUtil.openScanCard(mContext, CouponsPushFragment.this,
					getString(R.string.coupons_scan_code_title),Constants.REQUEST_SCAN);

		} else if (v == btnCancel && !submitControl) {
			submitControl = true;
			clearData();
		} else if(v == btnOk  && !submitControl) {
			submitControl = true;
			if (StringUtils.isNotBlank(txtActivity.getText().toString())) {
				if (StringUtils.isNotBlank(txtRegisterTime.getText().toString())) {
					if (coupons != null && coupons.size() > 0) {
						List<Coupon> couponSelects = new ArrayList<Coupon>();
						for (Coupon c : coupons) {
							if (c.isSelect()) {
								couponSelects.add(c);
							}
						}
						if (couponSelects.size() > 0) {
							progressDialog.show();
							DeliveryApi.mensendCoupon(ClientStateManager.getLoginToken(mContext), txtPhone.getText().toString(), activitySelect.getActivityCode(), couponSelects, mensendCouponHandler);
						} else {
							submitControl = false;
							PublicUtil.showToast(getString(R.string.coupons_list_select_tips));
						}
					} else {
						submitControl = false;
						PublicUtil.showToast(getString(R.string.coupons_list_select_tips));
					}
				} else {
					submitControl = false;
					PublicUtil.showToast(getString(R.string.coupons_consumer_scan_code));
				}
			} else {
				submitControl = false;
				PublicUtil.showToast(getString(R.string.coupons_activity_name_hint));
			}
		}

	}

	private void clearData() {
		layoutNamePhone.setVisibility(View.GONE);
		txtRegisterTime.setText("");
		if (StringUtils.isNotBlank(txtActivity.getText().toString())
				&& coupons != null && coupons.size() >0 ) {
			for (int i = 0; i < coupons.size(); i++) {
				Coupon c = coupons.get(i);
				c.setSelect(false);
				coupons.set(i,c);
			}
			couponAdapter.notifyDataSetChanged();
		}
		submitControl = false;
	}

	class ActivityAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private int layoutID;
		private boolean firstLoad;

		public ActivityAdapter(Context context, int layoutID) {
			this.mInflater = LayoutInflater.from(context);
			this.layoutID = layoutID;
		}

		@Override
		public int getCount() {
			if (activitys == null) {
				return  0;
			}
			return activitys.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(layoutID, null);
			}
			LinearLayout layoutActivity = ViewHolder.get(convertView, R.id.layout_activity);
			TextView txtActivityName = ViewHolder.get(convertView, R.id.txt_activity_name);
			final CheckBox cb = ViewHolder.get(convertView, R.id.cb_select);

			final CouponAct act = activitys.get(position);
			cb.setChecked(act.isSelect());

			txtActivityName.setText(act.getActivitySName());
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v == cb && !cb.isChecked()) {
						cb.setChecked(true);
					}
					for (int i = 0; i< activitys.size(); i++) {
						CouponAct couponAct = activitys.get(i);
						couponAct.setSelect(i == position);
						activitys.set(i, couponAct);
					}
					notifyDataSetChanged();
				}
			};

			cb.setOnClickListener(listener);
			layoutActivity.setOnClickListener(listener);
			return convertView;
		}

	}

	class CouponAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private int layoutID;

		public CouponAdapter(Context context, int layoutID) {
			this.mInflater = LayoutInflater.from(context);
			this.layoutID = layoutID;
		}

		@Override
		public int getCount() {
			if (coupons == null) {
				return 0;
			}
			return coupons.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(layoutID, null);
			}
			LinearLayout layoutCoupon = ViewHolder.get(convertView, R.id.layout_coupon);
			TextView txtCouponName = ViewHolder.get(convertView, R.id.txt_coupon_name);
			final CheckBox cb = ViewHolder.get(convertView, R.id.cb_select);
			View lineDotted = ViewHolder.get(convertView, R.id.line_dotted);
			View lineSilde = ViewHolder.get(convertView, R.id.line_silde);
			//set default select
			final Coupon c = coupons.get(position);
			cb.setChecked(c.isSelect());
			if (position == coupons.size()-1) {
				lineDotted.setVisibility(View.GONE);
				lineSilde.setVisibility(View.VISIBLE);
			} else {
				lineDotted.setVisibility(View.VISIBLE);
				lineSilde.setVisibility(View.GONE);
			}

			txtCouponName.setText(c.getCouponSName());
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v != cb) {
						cb.setChecked(!cb.isChecked());
					}
					c.setSelect(cb.isChecked());
					coupons.set(position, c);
					notifyDataSetChanged();
				}
			};

			cb.setOnClickListener(listener);
			layoutCoupon.setOnClickListener(listener);
			return convertView;
		}
	}


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
