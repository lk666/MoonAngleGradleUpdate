package cn.com.bluemoon.delivery.module.card;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.WorkTask;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultShowPunchCardDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ImageRotateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PunchCardGetOffWordFragment extends Fragment implements OnClickListener{
	private String TAG = "PunchCardGetOffWordFragment";
	private CardTabActivity mContext;
	private ImageView imgBanner;
	private CommonProgressDialog progressDialog;
	private TagListView tagListView;
	private TextView txtStartTime;
	private TextView txtNameAndMobile;
	private TextView txtAddress;
	private TextView txtLogContent;
	private TextView txtDiaryContent;
	private TextView txtImgContent;
	private LinearLayout layoutWorkTask;
	private LinearLayout layoutWorkLog;
	private LinearLayout layoutWorkDiary;
	private LinearLayout layoutUploadImage;
	private View lineView;
	private ImageView imgDown;
	private Button btnPunchCard;
	public LocationClient mLocationClient = null;
	PunchCard punchCard;
	boolean hasWorkDiary = true;
	boolean hasWrokDaily = true;
	boolean hasImage = true;
	private List<Tag> mTags;
	private boolean control;

	public BDLocationListener myListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			LogUtils.d("location.getLatitude= " + location.getLatitude());
			LogUtils.d("location.getLongitude= " + location.getLongitude());
			LogUtils.d("location.getAltitude= " + location.getAltitude());
			mLocationClient.stop();
			if (location.getLocType() == BDLocation.TypeOffLineLocation) {
				punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
				punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
			} else {
				punchCard.setLatitude(location.getLatitude());
				punchCard.setLongitude(location.getLongitude());
			}

			//punchCard.setAltitude(location.getAltitude());
			progressDialog.show();
			DeliveryApi.addPunchCardOut(ClientStateManager.getLoginToken(),
					punchCard, CardUtils.getWorkTaskString(tagListView.getTagsChecked()), confirmAttendanceHandler);
		}
	};

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = (CardTabActivity) activity;
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		initCustomActionBar();
		View v = inflater.inflate(R.layout.get_off_work,container, false);
		tagListView = (TagListView) v.findViewById(R.id.tag_listview);
		txtStartTime = (TextView) v.findViewById(R.id.tv_start_time);
		txtNameAndMobile = (TextView) v.findViewById(R.id.txt_name_and_mobile);
		txtAddress = (TextView) v.findViewById(R.id.txt_address);
		txtLogContent = (TextView) v.findViewById(R.id.txt_log_content);
		txtDiaryContent = (TextView) v.findViewById(R.id.txt_diary_content);
		txtImgContent = (TextView) v.findViewById(R.id.txt_img_content);
		layoutWorkTask = (LinearLayout) v.findViewById(R.id.layout_work_task);
		layoutWorkLog = (LinearLayout) v.findViewById(R.id.layout_work_log);
		layoutWorkDiary = (LinearLayout) v.findViewById(R.id.layout_work_diary);
		layoutUploadImage= (LinearLayout) v.findViewById(R.id.layout_upload_image);
		btnPunchCard = (Button) v.findViewById(R.id.btn_punch_card);
		imgDown = (ImageView) v.findViewById(R.id.img_down);
		lineView =  v.findViewById(R.id.line_view);
		layoutWorkLog.setOnClickListener(this);
		layoutWorkDiary.setOnClickListener(this);
		layoutUploadImage.setOnClickListener(this);
		layoutWorkTask.setOnClickListener(this);
		btnPunchCard.setOnClickListener(this);
		mLocationClient = new LocationClient(mContext.getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		mLocationClient.setLocOption(option);


		progressDialog = new CommonProgressDialog(mContext);
		progressDialog.show();
		DeliveryApi.getPunchCard(ClientStateManager.getLoginToken(getActivity()), getPunchCardHandler);
		return v;
	}

	AsyncHttpResponseHandler confirmAttendanceHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG, "confirmAttendance result = " + responseString);
			control = false;
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString, ResultBase.class);
				if(baseResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					PublicUtil.showToast(baseResult.getResponseMsg());
					getActivity().finish();
				}else{
					PublicUtil.showErrorMsg(mContext, baseResult);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			control = false;
			if(progressDialog != null)
				progressDialog.dismiss();
			PublicUtil.showToastServerOvertime();
		}
	};

	AsyncHttpResponseHandler getPunchCardHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test", "getPunchCardHandler result = " + responseString);
			progressDialog.dismiss();
			try {
				ResultShowPunchCardDetail result = JSON.parseObject(responseString, ResultShowPunchCardDetail.class);
				if(null!=result && result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					//PublicUtil.showToast(result.getResponseMsg());
					punchCard =  result.getPunchCard();
					if (!(tagListView.getTags() != null && tagListView.getTags().size() > 0)) {
						setTags(result.getWorkTaskList());
					}
					txtStartTime.setText(DateUtil.getTime(punchCard.getPunchInTime(), "yyyy-MM-dd HH:mm"));
					txtNameAndMobile.setText(CardUtils.getCharge(punchCard));
					txtAddress.setText(CardUtils.getAddress(punchCard));
					if (punchCard.getHasWorkDiary()) {
						txtLogContent.setText(getString(R.string.work_log_show));
						hasWorkDiary = true;
					} else {
						hasWorkDiary = false;
						txtLogContent.setText(getString(R.string.input_today_log));
					}
					if (punchCard.getTotalBreedSalesNum() > 0) {
						hasWrokDaily = true;
						txtDiaryContent.setText(String.valueOf(punchCard.getTotalSalesNum()));
					} else {
						hasWrokDaily = false;
						txtDiaryContent.setText(getString(R.string.input_today_sales));
					}
					if (punchCard.getUploadImgNum() > 0) {
						hasImage = true;
						txtImgContent.setText(String.valueOf(punchCard.getUploadImgNum()));
					} else {
						hasImage = false;
						txtImgContent.setText(getString(R.string.upload_image));
					}

					//DeliveryApi.getPunchCardById(ClientStateManager.getLoginToken(getActivity()), Long.valueOf(punchCard.getPunchCardId()), getPunchCardByIdHandler);
				} else {
					progressDialog.dismiss();
					PublicUtil.showToast(result.getResponseMsg());
				}
			} catch (Exception e) {
				progressDialog.dismiss();
				PublicUtil.showToastServerBusy();
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			LogUtils.d("statusCode="+statusCode);
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime();
		}
	};

	private void setTags(List<WorkTask> workTasks){
		List<Tag> list = new ArrayList<>();
		if(workTasks!=null){
			for (int i = 0; i < workTasks.size(); i++) {
				Tag tag = new Tag();
				tag.setId(i);
				tag.setKey(workTasks.get(i).getTaskCode());
				tag.setChecked(workTasks.get(i).isSelected);
				tag.setTitle(workTasks.get(i).getTaskName());
				list.add(tag);
			}
		}
		tagListView.setTags(list);
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
				v.setText(getText(R.string.puncard_info));
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		control = false;
		if (resultCode == 1) {
			DeliveryApi.getPunchCard(ClientStateManager.getLoginToken(getActivity()), getPunchCardHandler);
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

	@Override
	public void onClick(View v) {
		if (!control) {
			control = true;
			if (v == layoutWorkDiary) {
				Intent intent = new Intent(mContext, WorkDiaryActivity.class);
				intent.putExtra("hasWrokDaily", hasWrokDaily);
				PunchCardGetOffWordFragment.this.startActivityForResult(intent,1);
			} else if (v == layoutUploadImage) {
				Intent intent = new Intent(mContext, UploadImageActivity.class);
				intent.putExtra("hasImage",hasImage);
				PunchCardGetOffWordFragment.this.startActivityForResult(intent,1);
			} else if (v==layoutWorkLog) {
				Intent intent = new Intent(mContext, LogActivity.class);
				intent.putExtra("hasWorkDiary",hasWorkDiary);
				PunchCardGetOffWordFragment.this.startActivityForResult(intent,1);
			} else if (v==layoutWorkTask) {
				if (tagListView.getVisibility() == View.VISIBLE) {
					ImageRotateUtil.rotateArrow(true, imgDown);
					tagListView.setVisibility(View.GONE);
					lineView.setVisibility(View.GONE);
				} else {
					ImageRotateUtil.rotateArrow(false, imgDown);
					tagListView.setVisibility(View.VISIBLE);
					lineView.setVisibility(View.VISIBLE);
				}
				control = false;
			} else if (v == btnPunchCard) {
				if (punchCard != null) {
					if (tagListView.getTagsChecked().size() == 0) {
						control = false;
						PublicUtil.showToast(getString(R.string.card_worktask_cannot_empty));
					} else if (!hasWorkDiary) {
						control = false;
						PublicUtil.showToast(getString(R.string.log_not_input));
					} else {
						new CommonAlertDialog.Builder(mContext).setMessage(getString(R.string.sure_get_off_work)).setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								progressDialog.show();
								mLocationClient.start();
							}
						}).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								control = false;
							}
						}).setCancelable(false).show();
					}
				} else {
					control = false;
				}

			}
		}

	}
}
