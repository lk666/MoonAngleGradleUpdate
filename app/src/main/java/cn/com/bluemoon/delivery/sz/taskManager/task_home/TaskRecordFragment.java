package cn.com.bluemoon.delivery.sz.taskManager.task_home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.sz.adapter.TaskDateStatusAdapter;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.ReviewerBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyperformanceinfoResultBean;
import cn.com.bluemoon.delivery.sz.meeting.SzMsgCountController;
import cn.com.bluemoon.delivery.sz.taskManager.AddTaskActivity;
import cn.com.bluemoon.delivery.sz.taskManager.SzTaskOrEvaluateDetailActivity;
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
public class TaskRecordFragment extends BaseFragment
		implements CalendarCard.OnCellClickListener,View.OnClickListener {

	private Context context=null;
	private TextView dateTv;

	private ListView lv_taskRecord;
	private LinearLayout ll_task_listView;

	/**header头部内容*/
	private ViewPager vp_calendar;
	private int mCurrentIndex = 498;
	private CalendarCard[] mShowViews;
	private CalendarViewAdapter<CalendarCard> adapter;
	private SildeDirection mDirection = SildeDirection.NO_SILDE;
	private String currentDate;

	private View headerView=null;
	private View footerView=null;
	private LinearLayout ll_task_footer=null;
	private TextViewForClick tv_addTask=null;//外面添加按钮
	private TextViewForClick tv_footer_addTask=null;//多列表数据时 以addfooter 的形式来展示

	/*******通过日期获取的列表的adapter******/
	private TaskDateStatusAdapter taskDateStatusAdapter=null;
	private List<DailyPerformanceInfoBean> dailyPerformanceInfoBeanArrayList = new ArrayList<>();
	private List<AsignJobBean> asignJobs=null;


	@Override
	protected int getLayoutId() {
		return R.layout.activity_sz_task_record_fragment;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void initView() {
		context=getActivity();

		lv_taskRecord= (ListView)getMainView().findViewById(R.id.lv_taskRecord);
		ll_task_listView= (LinearLayout) getMainView().findViewById(R.id.ll_task_listView);
		tv_addTask= (TextViewForClick) getMainView().findViewById(R.id.tv_addTask);
		tv_addTask.setOnClickListener(this);
		initCalendarView();
		initFooterView();
		initAdapterView();
	}

	@Override
	public void initData() {

	}

	@Override
	public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

		DailyperformanceinfoResultBean resultBean=
				JSON.parseObject(jsonString,DailyperformanceinfoResultBean.class);

		String monthlyPer=resultBean.getMonthlyPer();

		DailyPerformanceInfoBean infoBean=resultBean.getJobsdata();
		dailyPerformanceInfoBeanArrayList.add(infoBean);
//		任务内容
		asignJobs=infoBean.getAsignJobs();

	}


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





	public void initCalendarView(){
		LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerView = inflater.inflate(R.layout.sz_header_task,null);
		vp_calendar= (ViewPager) headerView.findViewById(R.id.vp_calendar);
		dateTv = (TextView) headerView.findViewById(R.id.tv_date);
		lv_taskRecord.addHeaderView(headerView);

		currentDate = new CustomDate().toString();
		LogUtil.i("当前的日期---------->："+currentDate);

		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(context, this);
			views[i].setmCircleColor("#ff1fb8ff");//+getResources().getColor(R.color.title_background)
		}
		adjustViewPagerHeight(views[1].getCurrentRowNum());
		adapter = new CalendarViewAdapter<CalendarCard>(views);
		setViewPager();
		dateTv.setText(DateUtil.getYear()+"年"+ DateUtil.getMonth()+"月");
	}

	public void initFooterView(){
		LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerView = inflater.inflate(R.layout.activity_sz_task_record_add_footer,null);
		ll_task_footer= (LinearLayout) footerView.findViewById(R.id.ll_task_footer);
		tv_footer_addTask= (TextViewForClick) footerView.findViewById(R.id.tv_footer_addTask);
		tv_footer_addTask.setOnClickListener(this);
		lv_taskRecord.addFooterView(footerView);
	}


	public void initAdapterView() {
		//TODO 模拟数据

		DailyPerformanceInfoBean infoBean=new DailyPerformanceInfoBean();

		List<AsignJobBean> asignJobBeanList=new ArrayList<>();
		for (int i=0;i<3;i++){
			AsignJobBean asignJobBean=new AsignJobBean();
			asignJobBean.setProduce_cont("工作输出的内容。。。。");
			asignJobBean.setTask_cont("任务："+i);
			asignJobBean.setCreatetime("8:0"+i);
			asignJobBean.setEnd_time("9:00"+i);
			asignJobBean.setState(i+"");
			asignJobBeanList.add(asignJobBean);
		}
		ReviewerBean reviewerBean=new ReviewerBean();
		reviewerBean.setuID("00001");
		reviewerBean.setuName("张三三");
		infoBean.setReviewer(reviewerBean);

		infoBean.setAsignJobs(asignJobBeanList);
		infoBean.setCreatetime("2016-09-10");
		infoBean.setDay_valid_min("120");
		infoBean.setDay_score("9");

		dailyPerformanceInfoBeanArrayList.add(infoBean);

		taskDateStatusAdapter = new TaskDateStatusAdapter(getActivity(), dailyPerformanceInfoBeanArrayList);
		lv_taskRecord.setAdapter(taskDateStatusAdapter);
		initListener();

		if (asignJobBeanList.size()>3){
			setLinearLayoutWeight(ll_task_listView,0f);
		}else{//采用addFooterView的形式来展示
			setLinearLayoutWeight(ll_task_listView,1.0f);
			ll_task_footer.setVisibility(View.GONE);
		}

	}


	private void initListener() {
		lv_taskRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DailyPerformanceInfoBean dailyPerformanceInfoBean =
						(DailyPerformanceInfoBean) parent.getAdapter().getItem(position);

				Bundle mBundle=new Bundle();
				mBundle.putInt(SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE,0);
				mBundle.putSerializable(SzTaskOrEvaluateDetailActivity.ACTIVITY_BEAN_TAYE,
						dailyPerformanceInfoBean);

				PageJumps.PageJumps(context,SzTaskOrEvaluateDetailActivity.class,mBundle);
//				PublicUtil.showToast(""+position);


			}
		});
	}




	private void adjustViewPagerHeight(int rowNum){
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

		getData(currentDate);

	}

	private void getData(String date){
		if (TextUtils.isEmpty(date)) {
			PublicUtil.showToast("日期不可为空！");
			return;
		}
		showWaitDialog();
		SzApi.getJobsListAndMonthlyPerformanceApi(date,0,getNewHandler(0, ResultToken.class));
	}

	@Override
	public void changeDate(CustomDate date) {
		dateTv.setText(date.getYear()+"年"+date.getMonth()+"月");
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_addTask:
			case R.id.tv_footer_addTask:

				Bundle mBundle=new Bundle();
				mBundle.putString(AddTaskActivity.CURRENTDATA,currentDate);
				mBundle.putInt(AddTaskActivity.TASKOPERATETYPE,
						AddTaskActivity.TASKOPERATETYPE_ADD);
				PageJumps.PageJumps(context,AddTaskActivity.class,mBundle);
				break;
			default:
				break;
		}
	}

	/**无数据内容时放置固定底部  两条数据以上的跟随在后面，添加按钮放在中间*/
	public void setLinearLayoutWeight(LinearLayout linearLayoutWeight,float weight){
		LinearLayout.LayoutParams layoutParams=
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,weight);
		linearLayoutWeight.setLayoutParams(layoutParams);
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
