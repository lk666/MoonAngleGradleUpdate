package cn.com.bluemoon.delivery.sz.meeting;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Calendar;

import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.api.response.MsgMainTypeResponse;
import cn.com.bluemoon.delivery.sz.util.FileUtil;
import cn.com.bluemoon.delivery.sz.util.ViewUtil;
import cn.com.bluemoon.delivery.sz.view.datepicker.SzDatepickerDialog;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.delivery.sz.adapter.ScheduleAdapter;
import cn.com.bluemoon.delivery.sz.api.response.UserSchDayResponse;
import cn.com.bluemoon.delivery.sz.bean.SchedualCommonBean;
import cn.com.bluemoon.delivery.sz.util.AssetUtil;
import cn.com.bluemoon.delivery.sz.util.Constants;
import cn.com.bluemoon.delivery.sz.util.DateUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.view.datepicker.adapter.NumericWheelAdapter;
import cn.com.bluemoon.delivery.sz.view.datepicker.widget.WheelView;
import cn.com.bluemoon.delivery.sz.view.calendar.CalendarCard;
import cn.com.bluemoon.delivery.sz.view.calendar.CalendarViewAdapter;
import cn.com.bluemoon.delivery.sz.view.calendar.CustomDate;

import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.ImageViewForClick;

public class SzSchedualActivity extends KJActivity implements CalendarCard.OnCellClickListener {
	private String TAG = SzSchedualActivity.class.getSimpleName();


	private TextView dateTv;
	@BindView(id=R.id.img_add,click=true)
	private ImageViewForClick addBtn;

	@BindView(id=R.id.schedual_listview)
	private ListView listView;

	private LinearLayout subNoLlt;
	private TextView subNoTv;

	private ViewPager viewPager;

	private ImageViewForClick backBtn,msgBtn,setBtn;
	private RadioButton meetingRbtn;
	private TextView numTv;

	private CommonProgressDialog progressDialog;
	private int mCurrentIndex = 498;
	private CalendarCard[] mShowViews;
	private CalendarViewAdapter<CalendarCard> adapter;
	private ScheduleAdapter scheduleAdapter;
	private SildeDirection mDirection = SildeDirection.NO_SILDE;
	private String currentNo;
	private Boolean isMySelf;
	private String currentDate;
	private SzDatepickerDialog szDatepickerDialog;


	enum SildeDirection {
		RIGHT, LEFT, NO_SILDE;
	}

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_sz_schedual);

	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();

		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);
		szDatepickerDialog = new SzDatepickerDialog(this);
		szDatepickerDialog.setDateConfirmListeren(new SzDatepickerDialog.DateConfirmListeren() {
			@Override
			public void getDateTime(CustomDate tempDate) {
				updateCalendarView2(tempDate);
			}
		});

		View headerView = LayoutInflater.from(this).inflate( R.layout.header_schedual, null);
		listView.addHeaderView(headerView);
		dateTv = (TextView) headerView.findViewById(R.id.tv_date);
		viewPager = (ViewPager)headerView.findViewById(R.id.vp_calendar);
		subNoLlt = (LinearLayout)headerView.findViewById(R.id.sub_no_llt);
		subNoTv = (TextView)headerView.findViewById(R.id.sub_no_tv);

		currentNo = getIntent().getStringExtra("staffNo");
		if(StringUtil.isEmptyString(currentNo)){
			currentNo = ClientStateManager.getUserName();
			isMySelf = true;
			subNoLlt.setVisibility(View.GONE);
		}else{
			isMySelf = false;
			subNoLlt.setVisibility(View.VISIBLE);
		}

		ArrayList<SchedualCommonBean> initdate = new ArrayList<SchedualCommonBean>();
		SchedualCommonBean temp = new SchedualCommonBean();
		temp.setAdjust("2");
		initdate.add(temp);
		scheduleAdapter = new ScheduleAdapter(this,initdate);
		listView.setAdapter(scheduleAdapter);
		//test();
		currentDate = new CustomDate().toString();
		getuserSchDay(currentDate,currentNo);

		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(this, this);
		}
		adjustViewPagerHeight(views[1].getCurrentRowNum());
		adapter = new CalendarViewAdapter<CalendarCard>(views);
		setViewPager();

		dateTv.setText(DateUtil.getYear()+"年"+ DateUtil.getMonth()+"月");

		dateTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showDateDialog();
			}
		});

	}

	private void adjustViewPagerHeight(int rowNum){
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int cellspace = width / 7;
		int height = cellspace * rowNum;
		viewPager.setLayoutParams(new LinearLayout.LayoutParams(width,height));
	}


	private void test() {
		try {
			String responseString = AssetUtil.getContent(this,"userSchDay.txt");
			UserSchDayResponse response = JSON.parseObject(responseString,UserSchDayResponse.class);
			if(response.getResponseCode()== Constants.RESPONSE_RESULT_SUCCESS){
				scheduleAdapter.refresh(response.getData());

			}else{
				PublicUtil.showToast(response.getResponseMsg());
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
			//PublicUtil.showToastServerBusy(aty);
		}
	}

	public void getuserSchDay(String scheduleDay,String staffNum) {

		String token = ClientStateManager.getLoginToken(aty);
		String userNo = ClientStateManager.getUserName();
		if(!StringUtils.isEmpty(token)){
			String scheduleType = "-1";
			if(meetingRbtn.isChecked()){
				scheduleType = "1";
			}
			//先去SD卡读缓存
			String cacheSchedual = FileUtil.getSchedual(staffNum,scheduleDay,scheduleType);
			if(!StringUtil.isEmptyString(cacheSchedual)){
				updateSchedualList(cacheSchedual,false);
			}
			SchedualCommonBean itemBean = null;
			if(scheduleAdapter.getCount() > 0){
				itemBean = (SchedualCommonBean)scheduleAdapter.getItem(0);
			}
			//判读是否要更新
			if(FileUtil.isUpdateSchedual(staffNum,scheduleDay,scheduleType,itemBean)){
				SzApi.userSchDay(userNo,scheduleDay,scheduleType,staffNum,token,userSchDayHandler);
			}
		}

	}

	AsyncHttpResponseHandler userSchDayHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		public void onStart(){
			super.onStart();
			progressDialog.show();
		}

		public void onFinish(){
			super.onFinish();
			progressDialog.dismiss();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d(TAG,"userSchDayHandler = " + responseString);
			updateSchedualList(responseString,true);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			LogUtils.e(TAG, throwable.getMessage());
			//PublicUtil.showToastServerOvertime(aty);
			PublicUtil.showToast("API 错误："+statusCode);
		}
	};

	private void updateSchedualList(String responseString,boolean isUpdate){
		String scheduleType = "-1";
		if(meetingRbtn.isChecked()){
			scheduleType = "1";
		}
		try {
			UserSchDayResponse response = JSON.parseObject(responseString,UserSchDayResponse.class);
			if(response.getResponseCode()== Constants.RESPONSE_RESULT_SUCCESS){

				//是否更新缓存
				if(isUpdate){
					FileUtil.setSchedual(currentNo,currentDate,scheduleType,responseString);
				}
				scheduleAdapter.refresh(response.getData());
			}else{
				PublicUtil.showToast(response.getResponseMsg());
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
			FileUtil.deleteSchedual(currentNo,currentDate,scheduleType,responseString);
			//PublicUtil.showToastServerBusy(aty);
		}
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

		int currentRowNum = mShowViews[arg0 % mShowViews.length].getCurrentRowNum();
		adjustViewPagerHeight(currentRowNum);
	}

	private void updateCalendarView2(CustomDate date){
		mShowViews = adapter.getAllItems();
		CalendarCard.mSelectDate = new CustomDate(date.getYear(),date.getMonth(),date.getDay());
		mShowViews[mCurrentIndex % mShowViews.length].setShowDate(date);
		mDirection = SildeDirection.NO_SILDE;
		int currentRowNum = mShowViews[mCurrentIndex % mShowViews.length].getCurrentRowNum();
		adjustViewPagerHeight(currentRowNum);
		clickDate(date);
	}


	@Override
	public void clickDate(CustomDate date) {
		dateTv.setText(date.getYear()+"年"+date.getMonth()+"月");
		currentDate = date.toString();
		getuserSchDay(currentDate,currentNo);
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
			case R.id.img_add:
				if(meetingRbtn.isChecked()){
					Intent intent = new Intent(aty,SchedualAddMeetingActivity.class);
					startActivity(intent);
				}else{
					PublicUtil.showToast("正在开发中。");
				}
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
		numTv = (TextView)actionBar.getCustomView().findViewById(R.id.num_tv);
		final View line1 = actionBar.getCustomView().findViewById(R.id.line1);
		final View line2 = actionBar.getCustomView().findViewById(R.id.line2);

		ViewUtil.setTipsNum(numTv,0);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		msgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(aty,SzMsgActivity.class);
				startActivity(intent);
			}
		});

		setBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PageJumps.PageJumps(SzSchedualActivity.this,SchedualAddMeetingActivity.class,null);
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

	/**
	 * 请求未读信息
	 */
	private void requestMsgNum() {
		SzMsgCountController.getInstance().getMsgMainTypeCount(aty,false, false, new RequestListener() {
			@Override
			public void getCacheCallBack(String dataString) {
				updateMsgMainTypeCount(dataString,false);
			}

			@Override
			public void getHttpCallBack(String dataString) {
				updateMsgMainTypeCount(dataString,true);
			}

			@Override
			public void stopLoad() {

			}
		});
	}

	private void updateMsgMainTypeCount(String responseString,boolean isUpdate){
		try {
			MsgMainTypeResponse response = JSON.parseObject(responseString,MsgMainTypeResponse.class);
			if(response.getResponseCode()== cn.com.bluemoon.delivery.sz.util.Constants.RESPONSE_RESULT_SUCCESS){
				//是否更新缓存
				if(isUpdate){
					FileUtil.setMainMsgCount(ClientStateManager.getUserName(),responseString);
				}
				SzMsgCountController.getInstance().mergeMsgCount(response.getMainTypeNews());
				int num = SzMsgCountController.getInstance().caculateUnReadMsg();
				ViewUtil.setTipsNum(numTv,num);
			}else{
				PublicUtil.showToast(response.getResponseMsg());
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
			FileUtil.deleteMainMsgCount(ClientStateManager.getUserName());
		}
	}

	/**
	 * 显示日期
	 */
	private void showDateDialog() {
		szDatepickerDialog.showDateDialog();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		SzMsgCountController.getInstance().initMsgCount();
		requestMsgNum();
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		if(progressDialog != null)
			progressDialog.dismiss();
	}

}
