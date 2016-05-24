package cn.com.bluemoon.delivery.ticket;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.DialogInterface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.AppointmentInfo;
import cn.com.bluemoon.delivery.app.api.model.ResultAppointmentInfo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.TicketType;
import cn.com.bluemoon.delivery.app.api.model.VenueInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class TicketCodeActivity extends KJActivity {

	private String TAG = "TicketCodeActivity";
	@BindView(id = R.id.txt_count)
	private TextView txtCount;
	@BindView(id = R.id.txt_address)
	private TextView txtAddress;
	@BindView(id = R.id.btn_scan, click = true)
	private Button btnScan;
	@BindView(id = R.id.btn_sign, click = true)
	private Button btnSign;
	@BindView(id = R.id.et_number, click = true)
	private ClearEditText etNumber;
	private CommonProgressDialog progressDialog;
	private VenueInfo item;
	private String token;
	private String result;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_ticket_code);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(aty);
		progressDialog = new CommonProgressDialog(this);
		checkBtnState();
		token = ClientStateManager.getLoginToken(aty);
		if(StringUtils.isEmpty(token)){
			PublicUtil.showMessageTokenExpire(aty);
			return;
		}
		if (getIntent() != null) {
			item = (VenueInfo) getIntent().getSerializableExtra("item");
		}
		if(item!=null){
			txtAddress.setText(item.getVenueSname());
			txtCount.setText(DateUtil.getCurDate()+"  "+item.getTimesName());
		}else{
			PublicUtil.showToast(aty, getString(R.string.ticket_get_data_fail));
		}
		etNumber.setCallBack(new CommonEditTextCallBack() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				super.afterTextChanged(s);
				checkBtnState();
			}
			
		});
		
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.btn_scan:
			setResult(3);
			finish();
			break;

		case R.id.btn_sign:
			result = etNumber.getText().toString();
			if (!StringUtils.isEmpty(result)) {
				LibViewUtil.hideIM(v);
				if(progressDialog!=null){
					progressDialog.show();
				}
				DeliveryApi.checkScanCode(token, result, checkCodeHandler);
			} else {
				PublicUtil.showToast(aty,getString(R.string.ticket_edit_text_empty));
			}

			break;
		}
	}
	
	private void checkBtnState(){
		if(etNumber.getText().toString().trim().length()>0){
			btnSign.setClickable(true);
			btnSign.setBackgroundResource(R.drawable.btn_blue_shape);
		}else{
			btnSign.setClickable(false);
			btnSign.setBackgroundResource(R.drawable.btn_blue_shape_disable);
		}
	}
	
	private void showTicketDialog(final AppointmentInfo info) {
		
		View view = LayoutInflater.from(aty).inflate(R.layout.appointment_msg,null);
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
		dialog.setPositiveButton(R.string.ticket_no_enter,null);
		dialog.setNegativeButton(R.string.ticket_enter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(StringUtils.isEmpty(token)){
							PublicUtil.showMessageTokenExpire(aty);
							return;
						}
						if(item!=null&&item.getTimesCode()!=null
								&&item.getVenueCode()!=null
								&&!StringUtils.isEmpty(result)){
							if(progressDialog!=null){
								progressDialog.show();
							}
							DeliveryApi.comesInto(token, 
									item.getVenueCode(),item.getTimesCode(), result, comesIntoHandler);
						}else{
							PublicUtil.showToastErrorData();
						}
					}
				});
		dialog.show();
	}
	
	AsyncHttpResponseHandler checkCodeHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "checkScanCode result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultAppointmentInfo appointmentInfoResult = JSON.parseObject(responseString,
						ResultAppointmentInfo.class);
				if (appointmentInfoResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					showTicketDialog(appointmentInfoResult.getAppointmentInfo());
				}else{
					PublicUtil.showErrorMsg(aty, appointmentInfoResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if(progressDialog != null)
				progressDialog.dismiss();
			LogUtils.e(TAG, throwable.getMessage());
			PublicUtil.showToastServerOvertime();
		}
	};
	
	AsyncHttpResponseHandler comesIntoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d(TAG, "comesInto result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString,
						ResultBase.class);
				if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
//					PublicUtil.showToast(baseResult.getResponseMsg());
					PublicUtil.showToast(R.string.ticket_check_success);
				}else{
					PublicUtil.showErrorMsg(aty, baseResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if(progressDialog != null)
				progressDialog.dismiss();
			LogUtils.e(TAG, throwable.getMessage());
			PublicUtil.showToastServerOvertime();
		}
	};


	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(), new IActionBarListener() {

			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				v.setText(R.string.ticket_check_title);
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
