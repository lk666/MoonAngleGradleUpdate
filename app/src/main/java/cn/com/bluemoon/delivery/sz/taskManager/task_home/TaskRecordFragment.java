package cn.com.bluemoon.delivery.sz.taskManager.task_home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.adapter.ScheduleAdapter;
import cn.com.bluemoon.delivery.sz.bean.SchedualCommonBean;
import cn.com.bluemoon.delivery.sz.meeting.SzMsgCountController;
import cn.com.bluemoon.delivery.sz.taskManager.AddTaskActivity;
import cn.com.bluemoon.delivery.sz.util.DateUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.view.calendar.CalendarCard;
import cn.com.bluemoon.delivery.sz.view.calendar.CalendarViewAdapter;
import cn.com.bluemoon.delivery.sz.view.calendar.CustomDate;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.TextViewForClick;

/**
 * Created by jiangyuehua on 16/9/8.
 */
public class TaskRecordFragment extends Fragment implements CalendarCard.OnCellClickListener,View.OnClickListener {

	private Context context=null;
	private TextView dateTv;

	private TextViewForClick tv_addTask;
	private ListView lv_taskRecord;

	/**header头部内容*/
	private ViewPager vp_calendar;
	private int mCurrentIndex = 498;
	private CalendarCard[] mShowViews;
	private CalendarViewAdapter<CalendarCard> adapter;
	private ScheduleAdapter scheduleAdapter;
	private SildeDirection mDirection = SildeDirection.NO_SILDE;
	private String currentDate;

	private View headerView=null;
	enum SildeDirection {
		RIGHT, LEFT, NO_SILDE;
	}
	private String chooseDate="";

	Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					dateTv.setText("11111111");
					break;
			}
		}
	};


	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		 View convertView=inflater.inflate(R.layout.activity_sz_task_record_fragment,null);
		return convertView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		context=getActivity();
		initView(view);
	}

	private void initView(View view) {
		// TODO Auto-generated method stub

//		ActivityManager.getInstance().pushOneActivity(this);//通知

		lv_taskRecord= (ListView) view.findViewById(R.id.lv_taskRecord);
		tv_addTask= (TextViewForClick) view.findViewById(R.id.tv_addTask);
		tv_addTask.setOnClickListener(this);

		LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerView = inflater.inflate(R.layout.sz_header_task,null);

		vp_calendar= (ViewPager) headerView.findViewById(R.id.vp_calendar);
		dateTv = (TextView) headerView.findViewById(R.id.tv_date);

		lv_taskRecord.addHeaderView(headerView);

		currentDate = new CustomDate().toString();
		LogUtil.i("当前的日期---------->："+currentDate);

		ArrayList<SchedualCommonBean> initdate = new ArrayList<SchedualCommonBean>();
		SchedualCommonBean temp = new SchedualCommonBean();
		temp.setAdjust("2");
		initdate.add(temp);

		scheduleAdapter = new ScheduleAdapter(getActivity(),initdate);
		lv_taskRecord.setAdapter(scheduleAdapter);

		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(context, this);
			views[i].setmCircleColor("#ff1fb8ff");//+getResources().getColor(R.color.title_background)
		}
		adjustViewPagerHeight(views[1].getCurrentRowNum());

		adapter = new CalendarViewAdapter<CalendarCard>(views);
		setViewPager();

		dateTv.setText(DateUtil.getYear()+"年"+ DateUtil.getMonth()+"月");


	}private void adjustViewPagerHeight(int rowNum){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int cellspace = width / 7;
		int height = cellspace * rowNum;
		vp_calendar.setLayoutParams(new LinearLayout.LayoutParams(width,height));
	}

	private void setViewPager() {
		vp_calendar.setAdapter(adapter);
		vp_calendar.setCurrentItem(498);
		vp_calendar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
//		dateTv.setText(date.getYear()+"年"+date.getMonth()+"月"+date.getDay()+"日");//子线程中无法更新UI
//		mHandler.sendEmptyMessage(1);
		currentDate=date.toString();
//		getuserSchDay(currentDate,currentNo);
		PublicUtil.showToast(currentDate);
	}

	@Override
	public void changeDate(CustomDate date) {
		dateTv.setText(date.getYear()+"年"+date.getMonth()+"月");
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_addTask:
				Bundle mBundle=new Bundle();
				mBundle.putString("currentDate",currentDate);
				PageJumps.PageJumps(context, AddTaskActivity.class,mBundle);
				break;
			default:
				break;
		}
	}



	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SzTaskActivity.class.getSimpleName());
		SzMsgCountController.getInstance().initMsgCount();
//		requestMsgNum();
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SzTaskActivity.class.getSimpleName());
	}

}
