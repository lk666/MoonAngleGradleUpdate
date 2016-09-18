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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.sz.adapter.TaskDateStatusAdapter;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyperformanceinfoResultBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoBean;
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
	private LinearLayout ll_task_content;
	private LinearLayout ll_add_taskView;

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
	private List<AsignJobBean> asignJobs=new ArrayList<>();


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
		ll_task_content= (LinearLayout) getMainView().findViewById(R.id.ll_task_content);
		ll_add_taskView= (LinearLayout) getMainView().findViewById(R.id.ll_add_taskView);
		tv_addTask= (TextViewForClick) getMainView().findViewById(R.id.tv_addTask);
		tv_addTask.setOnClickListener(this);
		initCalendarView();
		initFooterView();
		initAdapterView();
	}

	@Override
	public void initData() {

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
		//*/获取当天的数据
		getData(tranDateToTime(chooseDate)+"");



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
		ll_task_footer.setVisibility(View.GONE);
		tv_footer_addTask= (TextViewForClick) footerView.findViewById(R.id.tv_footer_addTask);
		tv_footer_addTask.setOnClickListener(this);
		lv_taskRecord.addFooterView(footerView);
	}


	public void initAdapterView() {
		//TODO 模拟数据
//		initMockData();
		taskDateStatusAdapter = new TaskDateStatusAdapter(getActivity(), dailyPerformanceInfoBeanArrayList);
		lv_taskRecord.setAdapter(taskDateStatusAdapter);
		initListener();

	}


	private void initMockData(){
		DailyPerformanceInfoBean infoBean=new DailyPerformanceInfoBean();
		List<AsignJobBean> asignJobBeanList=new ArrayList<>();
		AsignJobBean asignJobBean=new AsignJobBean();
		asignJobBean.setBegin_time("08:00");
		asignJobBean.setEnd_time("09:00");
		asignJobBean.setProduce_cont("工作输出的内容。。。。");
		asignJobBean.setTask_cont("任务1：");
		asignJobBean.setState("0");
		asignJobBeanList.add(asignJobBean);
		AsignJobBean asignJobBean2=new AsignJobBean();
		asignJobBean2.setBegin_time("09:01");
		asignJobBean2.setEnd_time("10:00");
		asignJobBean2.setProduce_cont("工作输出的内容。。。。");
		asignJobBean2.setTask_cont("任务2：");
		asignJobBean2.setState("1");
		asignJobBeanList.add(asignJobBean2);
		AsignJobBean asignJobBean3=new AsignJobBean();
		asignJobBean3.setBegin_time("10:00");
		asignJobBean3.setEnd_time("10:30");
		asignJobBean3.setProduce_cont("工作输出的内容。。。。");
		asignJobBean3.setTask_cont("任务3：");
		asignJobBean3.setState("2");
		asignJobBeanList.add(asignJobBean3);
		AsignJobBean asignJobBean4=new AsignJobBean();
		asignJobBean4.setBegin_time("10:31");
		asignJobBean4.setEnd_time("15:00");
		asignJobBean4.setProduce_cont("工作输出的内容。。。。");
		asignJobBean4.setTask_cont("任务4：");
		asignJobBean4.setState("2");
		asignJobBeanList.add(asignJobBean4);
		AsignJobBean asignJobBean5=new AsignJobBean();
		asignJobBean5.setBegin_time("16:00");
		asignJobBean5.setEnd_time("17:00");
		asignJobBean5.setProduce_cont("工作输出的内容。。。。");
		asignJobBean5.setTask_cont("任务5：");
		asignJobBean5.setState("0");
		asignJobBeanList.add(asignJobBean5);

		UserInfoBean reviewerBean=new UserInfoBean();
		reviewerBean.setUID("00001");
		reviewerBean.setUName("张三三");
		infoBean.setReviewer(reviewerBean);

		infoBean.setAsignJobs(asignJobBeanList);
		infoBean.setCreatetime("2016-09-10");
		infoBean.setDay_valid_min("120");
		infoBean.setDay_score("9");

		dailyPerformanceInfoBeanArrayList.add(infoBean);
	}

	private void initListener() {
		lv_taskRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DailyPerformanceInfoBean dailyPerformanceInfoBean =
						(DailyPerformanceInfoBean) parent.getAdapter().getItem(position);

				Bundle mBundle=new Bundle();
				mBundle.putInt(SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE,0);
				mBundle.putSerializable(SzTaskOrEvaluateDetailActivity.ACTIVITY_EXTAR_DATA,
						dailyPerformanceInfoBean);

				PageJumps.PageJumps(context,SzTaskOrEvaluateDetailActivity.class,mBundle);
//				PublicUtil.showToast(""+position);


			}
		});
	}




	private void adjustViewPagerHeight(int rowNum){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int cellspace = width / 6;
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
//		mHandler.sendEmptyMessage(1);
		currentDate=date.toString();
			getData(String.valueOf(
					tranDateToTime(currentDate,"yyyy-MM-dd")));
//		searchByKeyword("国");
		LogUtil.i(tranTimeToDate(tranDateToTime(currentDate,"yyyy-MM-dd")+""));

	}

	private void getData(String date){
		if (TextUtils.isEmpty(date)) {
			PublicUtil.showToast("日期不可为空！");
			return;
		}
		showWaitDialog();
		SzApi.getWorkDetailsApi(date,0,getNewHandler(0, ResultToken.class));
	}


	/**毫秒转日期*/
	public  String tranTimeToDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(Long.valueOf(time)));
	}

	public  String tranTimeToDate(String time,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(time)));
	}

	/**日期转毫秒*/
	public long tranDateToTime(String date){
		long times=0;
		try {
			DateFormat dm= new SimpleDateFormat("HH:mm");
			times=dm.parse(date.toString()).getTime();
			LogUtil.i("times:"+times+"/ currentDate:"+currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return times;
	}

	/**日期转毫秒*/
	public long tranDateToTime(String date,String format){
		long times=0;
		try {
			DateFormat dm= new SimpleDateFormat(format);
			times=dm.parse(date.toString()).getTime();
			LogUtil.i("times:"+times+"/ currentDate:"+currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return times;
	}


	@Override
	public void onSuccessException(int requestCode, Throwable t) {
		LogUtil.e("onSuccessException requestCode:"+requestCode);
		if (!dailyPerformanceInfoBeanArrayList.isEmpty()){
			dailyPerformanceInfoBeanArrayList.clear();
		}
		taskDateStatusAdapter.notifyDataSetChanged();
		PublicUtil.showToast("无任务数据！");
//		super.onSuccessException(requestCode, t);
	}

	@Override
	public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
		LogUtil.i("requestCode:---->"+requestCode+"/jsonString:"+jsonString);
		DailyperformanceinfoResultBean resultBean=
				JSON.parseObject(jsonString,DailyperformanceinfoResultBean.class);
		String monthlyPer=resultBean.getMonthlyPer();

		DailyPerformanceInfoBean infoBean=resultBean.getJobsdata();

		if (!dailyPerformanceInfoBeanArrayList.isEmpty()){
				dailyPerformanceInfoBeanArrayList.clear();
			}
			LogUtil.i("DailyPerformanceInfoBean"+infoBean.getAsignJobs());
			dailyPerformanceInfoBeanArrayList.add(infoBean);
			taskDateStatusAdapter.notifyDataSetChanged();
	//		任务内容
			asignJobs=infoBean.getAsignJobs();

		showAddTv();


	}


	@Override
	public void onErrorResponse(int requestCode, ResultBase result) {
		LogUtil.e("onErrorResponse requestCode:"+requestCode);

	}

	@Override
	public void onFailureResponse(int requestCode, Throwable t) {
		LogUtil.e("onFailureResponse requestCode:"+requestCode);
		showAddTv();

	}

	public void showAddTv(){
			if (asignJobs.size()>3){
				setLinearLayoutWeight(ll_task_content,0f);//占满显示footerview
				ll_add_taskView.setVisibility(View.GONE);
				ll_task_footer.setVisibility(View.VISIBLE);
			}else if(asignJobs.size()>=0 ||asignJobs.size()<=3){
				//采用addFooterView的形式来展示
				LogUtil.i("000000000000000");
				setLinearLayoutWeight(ll_task_content,1.0f);//
				ll_add_taskView.setVisibility(View.VISIBLE);
				ll_task_footer.setVisibility(View.GONE);
			}
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
