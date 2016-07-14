package cn.com.bluemoon.delivery.ticket;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.zxing.Result;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.AppointmentInfo;
import cn.com.bluemoon.delivery.app.api.model.ResultAppointmentInfo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.TicketType;
import cn.com.bluemoon.delivery.app.api.model.VenueInfo;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.qrcode.CaptureActivity;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class TicketScanActivity extends CaptureActivity{
	
	private String TAG = "ScanActivity";
	private TicketScanActivity main;
	private CommonProgressDialog progressDialog;
	private VenueInfo item;
	private String resultCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		main = this;
	}

	@Override
	public void handleDecode(Result result, Bitmap barcode) {
		// TODO Auto-generated method stub
		super.handleDecode(result, barcode);
		String resultString = result.getText().toString();
		if (StringUtils.isEmpty(resultString)) {
			PublicUtil.showToastErrorData();
			return;
		}
		String token = ClientStateManager.getLoginToken(main);
		onPause();
		if (progressDialog == null) {
			progressDialog = new CommonProgressDialog(main);
		}
		progressDialog.show();
		DeliveryApi.checkScanCode(token, resultString, checkCodeHandler);
		
	}
	
	private void reStart(){
		onResume();
	}
	
	private void showTicketDialog(final AppointmentInfo info) {
		View view = LayoutInflater.from(main).inflate(R.layout.appointment_msg,
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
		CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(main);
		dialog.setTitle(getString(type.getValue()));
		dialog.setView(view);
		dialog.setTitleIcon(getResources().getDrawable(type.getIcon()));
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.ticket_no_enter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						reStart();
					}
				});
		dialog.setNegativeButton(R.string.ticket_enter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String token = ClientStateManager.getLoginToken(main);
						if (StringUtils.isEmpty(token)) {
							PublicUtil.showMessageTokenExpire(main);
							return;
						}
						if (item!=null&&item.getTimesCode() != null
								&& item.getVenueCode() != null
								&& !StringUtils.isEmpty(resultCode)) {
							if(progressDialog!=null){
								progressDialog.show();
							}
							DeliveryApi.comesInto(token, item.getVenueCode(),
									item.getTimesCode(), resultCode,
									comesIntoHandler);
						} else {
							PublicUtil.showToastErrorData();
							reStart();
						}
					}
				});
		dialog.show();
	}
	
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
					PublicUtil.showErrorMsg(main, appointmentInfoResult);
					reStart();
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
				reStart();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if (progressDialog != null)
				progressDialog.dismiss();
			LogUtils.e(TAG, throwable.getMessage());
			PublicUtil.showToastServerOvertime();
			reStart();
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
					PublicUtil.showErrorMsg(main, baseResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy();
			}
			reStart();
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			if (progressDialog != null)
				progressDialog.dismiss();
			LogUtils.e(TAG, throwable.getMessage());
			PublicUtil.showToastServerOvertime();
			reStart();
		}
	};
}
