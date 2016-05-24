package cn.com.bluemoon.delivery.ticket;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.AppointmentInfo;
import cn.com.bluemoon.delivery.app.api.model.ResultAppointmentInfo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultVenueInfo;
import cn.com.bluemoon.delivery.app.api.model.TicketType;
import cn.com.bluemoon.delivery.app.api.model.VenueInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class TicketChooseActivity extends KJActivity {

	private String TAG = "TicketCodeActivity";
	@BindView(id = R.id.layout_address, click = true)
	private LinearLayout layoutAddress;
	@BindView(id = R.id.layout_count, click = true)
	private LinearLayout layoutCount;
	@BindView(id = R.id.txt_count)
	private TextView txtCount;
	@BindView(id = R.id.txt_address)
	private TextView txtAddress;
	@BindView(id = R.id.btn_scan, click = true)
	private Button btnScan;
	private CommonProgressDialog progressDialog;
	private boolean isTimes;
	private VenueInfo item;
	private ResultVenueInfo venueItems;
	private ResultVenueInfo timesItems;
	private String token;
	private String result;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_ticket_choose);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(aty);
		token = ClientStateManager.getLoginToken(aty);
		if (StringUtils.isEmpty(token)) {
			PublicUtil.showMessageTokenExpire(aty);
			return;
		}
		setButtonState(false);
		progressDialog = new CommonProgressDialog(this);
		// progressDialog.setCancelable(false);
		isTimes = false;
		if (progressDialog != null) {
			progressDialog.show();
		}
		DeliveryApi.getVenueList(token, Constants.TYPE_VENUE, "",
				venueListHandler);
	}

	private void setButtonState(boolean isClickable) {
		if (isClickable) {
			btnScan.setClickable(true);
			btnScan.setBackgroundResource(R.drawable.btn_red_shape_large);
		} else {
			btnScan.setClickable(false);
			btnScan.setBackgroundResource(R.drawable.btn_red_shape_disable_large);
		}
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.btn_scan:
			openScan();
			break;
		case R.id.layout_address:
			if (venueItems != null) {
				toTicketCount(Constants.TYPE_VENUE, venueItems, 1);
			}
			break;
		case R.id.layout_count:
			if (timesItems != null) {
				toTicketCount(Constants.TYPE_TIMES, timesItems, 2);
			}
			break;
		}
	}

	private void toTicketCount(String type, ResultVenueInfo info,
			int requestCode) {
		Intent intent = new Intent();
		intent.setClass(aty, TicketCountActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("items", info);
		if(item!=null){
			intent.putExtra("title", item.getVenueSname());
		}
		startActivityForResult(intent, requestCode);
	}

	private void openScan() {
		if (item != null && item.getVenueSname() != null
				&& item.getTimesName() != null) {
			PublicUtil.openScanTicket(aty, item.getVenueSname(),
					DateUtil.getCurDate()+"  "+item.getTimesName(),
					Constants.REQUEST_SCAN, 4);
		}
	}

	private void setVenueItem(VenueInfo venueItem, String type) {
		if (venueItem == null) {
			return;
		}
//		item = venueItem;
		if (Constants.TYPE_VENUE.equals(type)) {
			item = venueItem;
			txtAddress.setText(item.getVenueSname());
			txtCount.setText(R.string.none);
			timesItems = null;
			setButtonState(false);
			isTimes = true;
			if(progressDialog!=null&&!progressDialog.isShowing()){
				progressDialog.show();
			}
			DeliveryApi.getVenueList(token, Constants.TYPE_TIMES,
					item.getVenueCode(), venueListHandler);	
		} else if (Constants.TYPE_TIMES.equals(type)) {
			if(item!=null&&(StringUtils.isEmpty(venueItem.getVenueCode())
					||StringUtils.isEmpty(venueItem.getVenueSname()))){
				item.setTimesCode(venueItem.getTimesCode());
				item.setTimesName(venueItem.getTimesName());
			}else{
				item = venueItem;
			}
			txtAddress.setText(item.getVenueSname());
			txtCount.setText(DateUtil.getCurDate()+"  "+item.getTimesName());
			if (!StringUtils.isEmpty(item.getVenueCode())
					&&!StringUtils.isEmpty(item.getTimesCode())) {
				setButtonState(true);
			}	
		}
	}

	private void showTicketDialog(AppointmentInfo info) {
		View view = LayoutInflater.from(aty).inflate(R.layout.appointment_msg,
				null);
		TextView txtVenue = (TextView) view.findViewById(R.id.txt_venue);
		TextView txtTimes = (TextView) view.findViewById(R.id.txt_times);
		if(info!=null&&!StringUtils.isEmpty(info.getVenueSname())){
			txtVenue.setText(info.getVenueSname());
		}else{
			txtVenue.setText(R.string.none);
		}
		if(info!=null&&!StringUtils.isEmpty(info.getTimesName())){
			txtTimes.setText(info.getTimesName());
		}else{
			txtTimes.setText(R.string.none);
		}	
		TicketType type = null;
		if(info!=null){
			for(TicketType t:TicketType.values()){
				if(t.getKey().equals(info.getTicketType())){
					type = t;
				}
			}
		}
		if(type==null){
			type = TicketType.TICKET;
		}
		CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
		dialog.setTitle(getString(type.getValue()));
		dialog.setView(view);
		dialog.setTitleIcon(getResources().getDrawable(type.getIcon()));
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.ticket_no_enter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						openScan();
					}
				});
		dialog.setNegativeButton(R.string.ticket_enter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (StringUtils.isEmpty(token)) {
							PublicUtil.showMessageTokenExpire(aty);
							return;
						}
						if (item!=null&&item.getTimesCode() != null
								&& item.getVenueCode() != null
								&& !StringUtils.isEmpty(result)) {
							if(progressDialog!=null){
								progressDialog.show();
							}
							DeliveryApi.comesInto(token, item.getVenueCode(),
									item.getTimesCode(), result,
									comesIntoHandler);
						} else {
							PublicUtil.showToastErrorData();
							openScan();
						}
					}
				});
		dialog.show();
	}

	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				v.setText(R.string.ticket_choose_title);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		if (resultCode == RESULT_OK) {
			if (data == null) {
				return;
			}
			switch (requestCode) {
			case Constants.REQUEST_SCAN:
				result = data.getStringExtra(LibConstants.SCAN_RESULT);
				if (StringUtils.isEmpty(result)) {
					PublicUtil.showToastErrorData();
					return;
				}
				if (progressDialog != null) {
					progressDialog.show();
				}
				DeliveryApi.checkScanCode(token, result, checkCodeHandler);
				break;
			case 1:
				VenueInfo itemVenue = (VenueInfo) data
						.getSerializableExtra("item");
				setVenueItem(itemVenue, Constants.TYPE_VENUE);
				break;
			case 2:
				VenueInfo itemTimes = (VenueInfo) data
						.getSerializableExtra("item");
				setVenueItem(itemTimes, Constants.TYPE_TIMES);
				break;
			}
		} else if (resultCode == 3) {
			openScan();
		} else if (resultCode == 4) {
			if (item != null && item.getVenueSname() != null
					&& item.getTimesName() != null) {
				Intent intent = new Intent(aty, TicketCodeActivity.class);
				intent.putExtra("item", item);
				startActivityForResult(intent, 3);
			}
		}
	}

	AsyncHttpResponseHandler venueListHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "getVenueList result = " + responseString);
			if (progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultVenueInfo venueInfoResult = JSON.parseObject(
						responseString, ResultVenueInfo.class);
				if (venueInfoResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					if (venueInfoResult.getItemList() != null
							&& venueInfoResult.getItemList().size() > 0) {
						VenueInfo info = null;
						for (int i = 0; i < venueInfoResult.getItemList().size(); i++) {
							if (venueInfoResult.getItemList().get(i).isDefault) {
								info = venueInfoResult.getItemList().get(i);
								break;
							}
						}
						if (info == null) {
							info = venueInfoResult.getItemList().get(0);
						}
						if (isTimes) {
							timesItems = venueInfoResult;
							setVenueItem(info, Constants.TYPE_TIMES);
						} else {
							venueItems = venueInfoResult;
							setVenueItem(info, Constants.TYPE_VENUE);
						}
					}
				} else {
					PublicUtil.showErrorMsg(aty, venueInfoResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if (progressDialog != null)
				progressDialog.dismiss();
			LogUtils.e(TAG, throwable.getMessage());
			PublicUtil.showToastServerOvertime();
		}
	};

	AsyncHttpResponseHandler checkCodeHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "checkScanCode result = " + responseString);
			if (progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultAppointmentInfo appointmentInfoResult = JSON.parseObject(
						responseString, ResultAppointmentInfo.class);
				if (appointmentInfoResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					showTicketDialog(appointmentInfoResult.getAppointmentInfo());
				} else {
					PublicUtil.showErrorMsg(aty, appointmentInfoResult);
					openScan();
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
				openScan();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if (progressDialog != null)
				progressDialog.dismiss();
			LogUtils.e(TAG, throwable.getMessage());
			PublicUtil.showToastServerOvertime();
			openScan();
		}
	};

	AsyncHttpResponseHandler comesIntoHandler = new TextHttpResponseHandler(
			HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "comesInto result = " + responseString);
			if (progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString,
						ResultBase.class);
				if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
//					PublicUtil.showToast(baseResult.getResponseMsg());
					PublicUtil.showToast(R.string.ticket_check_success);
				} else {
					PublicUtil.showErrorMsg(aty, baseResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
			openScan();
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if (progressDialog != null)
				progressDialog.dismiss();
			LogUtils.e(TAG, throwable.getMessage());
			PublicUtil.showToastServerOvertime();
			openScan();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (progressDialog != null)
			progressDialog.dismiss();
	}
}
