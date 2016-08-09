package cn.com.bluemoon.delivery.sz.meeting;

import android.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.sz.view.calendar.CalendarCard;
import cn.com.bluemoon.delivery.sz.view.calendar.CalendarViewAdapter;
import cn.com.bluemoon.delivery.sz.view.calendar.CustomDate;
import cn.com.bluemoon.delivery.sz.view.calendar.DateUtil;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.ImageViewForClick;

public class SchedualActivity extends KJActivity implements CalendarCard.OnCellClickListener {
	private String TAG = SchedualActivity.class.getSimpleName();

	@BindView(id=R.id.tv_date,click=true)
	private TextView dateTv;
	@BindView(id=R.id.vp_calendar)
	private ViewPager viewPager;

	private ImageViewForClick backBtn,msgBtn,setBtn;
	private RadioButton meetingRbtn;

	private CommonProgressDialog progressDialog;
	private int mCurrentIndex = 498;
	private CalendarCard[] mShowViews;
	private CalendarViewAdapter<CalendarCard> adapter;
	private SildeDirection mDirection = SildeDirection.NO_SILDE;



	enum SildeDirection {
		RIGHT, LEFT, NO_SILDE;
	}

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_schedual);

	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);

		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(this, this);
		}
		adapter = new CalendarViewAdapter<CalendarCard>(views);
		setViewPager();


		dateTv.setText(DateUtil.getYear()+"年"+DateUtil.getMonth()+"月");
	}

	private void setViewPager() {
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(498);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				measureDirection(position);
				updateCalendarView(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 计算方向
	 *
	 * @param arg0
	 */
	private void measureDirection(int arg0) {

		if (arg0 > mCurrentIndex) {
			mDirection = SildeDirection.RIGHT;

		} else if (arg0 < mCurrentIndex) {
			mDirection = SildeDirection.LEFT;
		}
		mCurrentIndex = arg0;
	}

	// 更新日历视图
	private void updateCalendarView(int arg0) {
		mShowViews = adapter.getAllItems();
		if (mDirection == SildeDirection.RIGHT) {
			mShowViews[arg0 % mShowViews.length].rightSlide();
		} else if (mDirection == SildeDirection.LEFT) {
			mShowViews[arg0 % mShowViews.length].leftSlide();
		}
		mDirection = SildeDirection.NO_SILDE;
	}

	private void updateCalendarView(CustomDate date){
		mShowViews = adapter.getAllItems();
		mShowViews[mCurrentIndex % mShowViews.length].setShowDate(date);
		mDirection = SildeDirection.NO_SILDE;
	}


	@Override
	public void clickDate(CustomDate date) {
		dateTv.setText(date.getYear()+"年"+date.getMonth()+"月");
		PublicUtil.showToast(date.getYear()+"年"+date.getMonth()+"月"+date.getDay()+"日");
	}

	@Override
	public void changeDate(CustomDate date) {
		dateTv.setText(date.getYear()+"年"+date.getMonth()+"月");
	}


	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
			case R.id.submit_btn:
				//submit();
				break;
			case R.id.tv_date:
				CustomDate tempDate = new CustomDate(2016,12,1);
				updateCalendarView(tempDate);
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return false;
	}

	private void initCustomActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.top_meeting_bar);

		RadioGroup radioGroup = (RadioGroup) actionBar.getCustomView().findViewById(R.id.rgroup);
		backBtn = (ImageViewForClick) actionBar.getCustomView().findViewById(R.id.img_back);
		msgBtn = (ImageViewForClick) actionBar.getCustomView().findViewById(R.id.img_right2);
		setBtn = (ImageViewForClick) actionBar.getCustomView().findViewById(R.id.img_right);
		meetingRbtn = (RadioButton) actionBar.getCustomView().findViewById(R.id.rbtn2);
		final View line1 = actionBar.getCustomView().findViewById(R.id.line1);
		final View line2 = actionBar.getCustomView().findViewById(R.id.line2);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		msgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		setBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.rbtn1){
					line1.setVisibility(View.VISIBLE);
					line2.setVisibility(View.INVISIBLE);
				}else if(checkedId == R.id.rbtn2){
					line1.setVisibility(View.INVISIBLE);
					line2.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	public void submit() {

		String token = ClientStateManager.getLoginToken(aty);
		if(!StringUtils.isEmpty(token)){
			//DeliveryApi.updatePassword(token, cuPsw, nePsw, changePwdHandler);
		}

	}

	AsyncHttpResponseHandler changePwdHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"updatePassword result = " + responseString);
			if(progressDialog != null)
				progressDialog.dismiss();
			try {
				ResultBase baseResult = JSON.parseObject(responseString,ResultBase.class);
				if(baseResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					PublicUtil.showToast(aty, getString(R.string.change_pwd_success));
					finish();
				}else{
					PublicUtil.showErrorMsg(aty, baseResult);
				}
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				PublicUtil.showToastServerBusy(aty);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			if(progressDialog != null)
				progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(aty);
		}
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		if(progressDialog != null)
			progressDialog.dismiss();
	}

}
