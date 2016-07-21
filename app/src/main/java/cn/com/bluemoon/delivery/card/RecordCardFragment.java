package cn.com.bluemoon.delivery.card;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.ResultPunchCardList;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.MonthDatePickerDialog;

public class RecordCardFragment extends Fragment {
	private String TAG = "PunchCardFragment";
	private CardTabActivity mContext;
	private CommonProgressDialog progressDialog;
	private PullToRefreshListView listView;
	private TextView txtTime;
	private TextView txtCount;
	private String mYear;
	private String mMon;
	private MonthDatePickerDialog monthDatePickerDialog;
	private CardRecordAdapter cardRecordAdapter;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = (CardTabActivity) activity;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initCustomActionBar();
		View v = inflater.inflate(R.layout.fragment_tab_card_record,
				container, false);
		progressDialog = new CommonProgressDialog(mContext);
		txtCount = (TextView) v.findViewById(R.id.txt_count);
		txtTime = (TextView) v.findViewById(R.id.txt_time);
		listView = (PullToRefreshListView) v.findViewById(R.id.listView_record);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				DeliveryApi.getPunchCardList(ClientStateManager.getLoginToken(mContext), getMonth(), getPunchCardListHandler);
			}
		});
		setCurDate();
		if(progressDialog!=null){
			progressDialog.show();
		}
		DeliveryApi.getPunchCardList(ClientStateManager.getLoginToken(mContext),getMonth(),getPunchCardListHandler);
		return v;
	}

	private long getMonth(){
		if (StringUtils.isEmpty(mYear)||StringUtils.isEmpty(mMon)){
			setCurDate();
		}
		if(Integer.valueOf(mMon)<=8){
			return Long.valueOf(mYear+"0"+(Integer.valueOf(mMon)+1));
		}else{
			return Long.valueOf(mYear+(Integer.valueOf(mMon)+1));
		}
	}

	private void initCustomActionBar() {

		CommonActionBar actionBar =   new CommonActionBar(getActivity().getActionBar(),
				new IActionBarListener() {

					@Override
					public void onBtnRight(View v) {
						// TODO Auto-generated method stu
						showMonthDatePickerDialog();
					}

					@Override
					public void onBtnLeft(View v) {
						// TODO Auto-generated method stub
						getActivity().finish();
					}

					@Override
					public void setTitle(TextView v) {
						// TODO Auto-generated method stub
						v.setText(R.string.tab_bottom_punch_record_text);
					}
				});

		actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
		actionBar.getTvRightView().setCompoundDrawablePadding(10);

		Drawable drawableFillter=getResources().getDrawable(R.mipmap.icon_filter);
		drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
		actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
		actionBar.getTvRightView().setVisibility(View.VISIBLE);

	}

	public void setCurDate() {
		Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
		mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
		mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
		refreshMonthTxt(dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH));
	}

	private void refreshMonthTxt(int year,int month){
		String text = year + "-" + (month+1);
		if(month<=8){
			text = mYear + "-0" + (month+1);
		}
		txtTime.setText(text);
	}

	private void showMonthDatePickerDialog(){
		if(StringUtils.isEmpty(mYear)||StringUtils.isEmpty(mMon)){
			setCurDate();
		}
		if(monthDatePickerDialog==null){
			monthDatePickerDialog = new MonthDatePickerDialog(mContext,onDateSetListener,Integer.valueOf(mYear),Integer.valueOf(mMon),1);
			monthDatePickerDialog.show();
		}else{
			if(!monthDatePickerDialog.isShowing()){
				monthDatePickerDialog.updateDate(Integer.valueOf(mYear), Integer.valueOf(mMon), 1);
				monthDatePickerDialog.show();
			}
		}
	}

	MonthDatePickerDialog.OnDateSetListener onDateSetListener = new MonthDatePickerDialog.OnDateSetListener(){

		@Override
		public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int day) {
			mYear = String.valueOf(year);
			mMon = String.valueOf(monthOfYear);
			refreshMonthTxt(year, monthOfYear);
			if(progressDialog!=null) progressDialog.show();
			DeliveryApi.getPunchCardList(ClientStateManager.getLoginToken(mContext), getMonth(), getPunchCardListHandler);
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED)
			return;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_SCAN:
				String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
				PublicUtil.showToast(resultStr);
				break;
			}
		}
	}

	AsyncHttpResponseHandler getPunchCardListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG, "getPunchCardList result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			listView.onRefreshComplete();
			try {
				ResultPunchCardList punchCardListResult = JSON.parseObject(responseString, ResultPunchCardList.class);
				if(punchCardListResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					txtCount.setText(String.format(getString(R.string.card_record_count),punchCardListResult.getTotalCount()));
					cardRecordAdapter = new CardRecordAdapter(mContext);
					cardRecordAdapter.setList(punchCardListResult.getPunchCardList());
					listView.setAdapter(cardRecordAdapter);
				}else{
					PublicUtil.showErrorMsg(mContext, punchCardListResult);
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
			listView.onRefreshComplete();
			PublicUtil.showToastServerOvertime();
		}
	};

	class CardRecordAdapter extends BaseAdapter {

		private Context context;
		private List<PunchCard> lists;

		public CardRecordAdapter(Context context) {
			this.context = context;
		}

		public void setList(List<PunchCard> list) {
			this.lists = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (lists==null||lists.size() == 0) {
				return 1;
			}
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
			if (lists==null||lists.size() == 0) {
				View viewEmpty = inflate.inflate(R.layout.layout_no_data, null);
				TextView txtContent = (TextView) viewEmpty.findViewById(R.id.txt_content);
				txtContent.setText(context.getString(R.string.card_record_empty));
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.MATCH_PARENT, listView.getHeight());
				viewEmpty.setLayoutParams(params);
				return viewEmpty;
			}
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.item_card_record, null);
			 }

			TextView txtTimeStart = (TextView) convertView
					.findViewById(R.id.txt_time_start);
			TextView txtTimeEnd = (TextView) convertView
					.findViewById(R.id.txt_time_end);
			TextView txtCharge = (TextView) convertView
					.findViewById(R.id.txt_charge);
			TextView txtCardAddress = (TextView) convertView
					.findViewById(R.id.txt_card_address);
			View view = convertView.findViewById(R.id.view_line);
			if(position == lists.size()-1){
				view.setVisibility(View.GONE);
			}else{
				view.setVisibility(View.VISIBLE);
			}
			final PunchCard punchCard = lists.get(position);
			txtTimeStart.setText(String.format(getString(R.string.card_record_start_time),
					DateUtil.getTime(punchCard.getPunchInTime(),"yyyy-MM-dd HH:mm")));
			txtTimeEnd.setText(String.format(getString(R.string.card_record_end_time),
					DateUtil.getTime(punchCard.getPunchOutTime(), "yyyy-MM-dd HH:mm")));
			txtCharge.setText(CardUtils.getChargeNoPhone(punchCard));
			txtCardAddress.setText(CardUtils.getAddress(punchCard));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//跳转网页
					String url = String.format(BuildConfig.PUNCH_DETAILDS_DOMAIN, "angel/#/punchDetails?token="
							+ClientStateManager.getLoginToken(mContext)+"&punchCardId="+punchCard.getPunchCardId());
					PublicUtil.openWebView(context,url,null,true,false,false,false);
				}
			});
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
