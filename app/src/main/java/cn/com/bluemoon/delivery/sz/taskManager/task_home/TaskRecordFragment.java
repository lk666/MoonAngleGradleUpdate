package cn.com.bluemoon.delivery.sz.taskManager.task_home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.sz.adapter.TaskDateStatusAdapter;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyperformanceinfoResultBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyperformanceinfoResultBeanList;
import cn.com.bluemoon.delivery.sz.meeting.SzMsgCountController;
import cn.com.bluemoon.delivery.sz.taskManager.AddTaskActivity;
import cn.com.bluemoon.delivery.sz.taskManager.SzTaskOrEvaluateDetailActivity;
import cn.com.bluemoon.delivery.sz.util.CacheServerResponse;
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
	private TextView tv_task_point=null;
	private LinearLayout ll_task_footer=null;
	private TextViewForClick tv_addTask=null;//外面添加按钮
//	private TextViewForClick tv_footer_addTask=null;//多列表数据时 以addfooter 的形式来展示

	/*******通过日期获取的列表的adapter******/
	private TaskDateStatusAdapter taskDateStatusAdapter=null;
	private List<DailyPerformanceInfoBean> dailyPerformanceInfoBeanArrayList = new ArrayList<>();
	private List<AsignJobBean> asignJobs=new ArrayList<>();

	/**全局联网得到的任务数据*/
	private DailyPerformanceInfoBean dailyInfoBean=null;
//	private DailyperformanceinfoResultBean resultBean=null;
	/**1:添加，0：修改，2：查看*/
	private int showModleType=-1;


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
		EventBus.getDefault().register(this);

		lv_taskRecord= (ListView)getMainView().findViewById(R.id.lv_taskRecord);
		ll_task_content= (LinearLayout) getMainView().findViewById(R.id.ll_task_content);
		ll_add_taskView= (LinearLayout) getMainView().findViewById(R.id.ll_add_taskView);
		tv_addTask= (TextViewForClick) getMainView().findViewById(R.id.tv_addTask);
		ll_add_taskView.setOnClickListener(this);
		tv_addTask.setOnClickListener(this);
		initCalendarView();
//		initFooterView();
		initAdapterView();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void getModifyTaskSuccess(EventMessageBean messageBean){
		LogUtil.v("getModifyTaskSuccess"+messageBean.toString());
		if (messageBean.getEventMsgAction().equals("101")){
//			重新获取当前的日期数据
			getData(String.valueOf(
					DateUtil.tranDateToTime(currentDate,"yyyy-MM-dd")));
		}
	}

	@Override
	public void initData() {

	}

	enum SildeDirection {
		RIGHT, LEFT, NO_SILDE;
	}

	public void initCalendarView(){
		LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerView = inflater.inflate(R.layout.sz_header_task,null);
		vp_calendar= (ViewPager) headerView.findViewById(R.id.vp_calendar);
		dateTv = (TextView) headerView.findViewById(R.id.tv_date);
		tv_task_point = (TextView) headerView.findViewById(R.id.tv_task_point);
		lv_taskRecord.addHeaderView(headerView);

		currentDate = new CustomDate().toString();
		LogUtil.i("当前的日期---------->："+currentDate);
		//*/获取当天的数据
		getData(String.valueOf(DateUtil.tranDateToTime(currentDate,"yyyy-MM-dd")));

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
//		tv_footer_addTask= (TextViewForClick) footerView.findViewById(R.id.tv_footer_addTask);
//		tv_footer_addTask.setOnClickListener(this);
//		lv_taskRecord.addFooterView(footerView);
	}


	public void initAdapterView() {
		//TODO 模拟数据
//		initMockData();
		taskDateStatusAdapter = new TaskDateStatusAdapter(getActivity(), dailyPerformanceInfoBeanArrayList);
		lv_taskRecord.setAdapter(taskDateStatusAdapter);
		initListener();
	}

	private void initListener() {
		lv_taskRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (dailyInfoBean!=null){
//					intentDetailActivity(dailyInfoBean);

					Bundle mBundle=new Bundle();
					LogUtil.i("跳转的类型showModleType："+showModleType);
					mBundle.putInt(SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE,showModleType);
					mBundle.putSerializable(SzTaskOrEvaluateDetailActivity.ACTIVITY_EXTAR_DATA,
							dailyInfoBean);
					PageJumps.PageJumps(context,SzTaskOrEvaluateDetailActivity.class,mBundle);

				}

			}
		});
	}

	private void intentDetailActivity(DailyPerformanceInfoBean dailyPerformanceInfoBean){
//		任务内容入口处修改时间
		List<AsignJobBean> asignJobs=dailyPerformanceInfoBean.getAsignJobs();
		//任务内容转成日期格式
		for (AsignJobBean asignJobBean:asignJobs) {
			LogUtil.e("跳转至详情，时间转换后：----------》"+asignJobBean.toString());
		}

		Bundle mBundle=new Bundle();
		//*/传不同的值 0：任务修改详情，1:添加，2：任务查看详情
		if (showModleType==0){
			mBundle.putInt(AddTaskActivity.TASKOPERATETYPE,
					AddTaskActivity.TASKOPERATETYPE_MODIFY);
			mBundle.putSerializable(AddTaskActivity.DATABEAN, dailyPerformanceInfoBean);
			PageJumps.PageJumps(context, AddTaskActivity.class, mBundle);
		}else{
			LogUtil.i("跳转的类型showModleType："+showModleType);
			mBundle.putInt(SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE,showModleType);
			mBundle.putSerializable(SzTaskOrEvaluateDetailActivity.ACTIVITY_EXTAR_DATA,
					dailyPerformanceInfoBean);
			PageJumps.PageJumps(context,SzTaskOrEvaluateDetailActivity.class,mBundle);
		}


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
		currentDate=date.toString();
		try {
			//先看本地是否存在
			DailyperformanceinfoResultBeanList loacalBeanList=
					(DailyperformanceinfoResultBeanList) CacheServerResponse.readObject(
							context,"DailyperformanceinfoResultBeanList");
			boolean isExist=false;
			List<DailyPerformanceInfoBean> loacalList=null;
			if (loacalBeanList!=null){
				loacalList=loacalBeanList.getDailyPerformanceInfoBeanList();
				if (loacalBeanList!=null && loacalList!=null){
					for (DailyPerformanceInfoBean bean:loacalList) {
						LogUtil.d("先查看本地是否存在："+bean.toString());
						if (DateUtil.tranDateToTime(bean.getWork_date(),"yyyy-MM-dd")
								==DateUtil.tranDateToTime(currentDate,"yyyy-MM-dd")){
		//					本地有，用本地的
							isExist=true;
							LogUtil.d("先查看本地是否存在isExist："+isExist);
						}
					}
				}
			}

			if (isExist==false){
				getData(String.valueOf(
						DateUtil.tranDateToTime(currentDate,"yyyy-MM-dd")));
				LogUtil.i(currentDate+"/"+String.valueOf(
						DateUtil.tranDateToTime(currentDate,"yyyy-MM-dd")));
			}else{//本地存在，直接刷新
				if (!dailyPerformanceInfoBeanArrayList.isEmpty()){
					dailyPerformanceInfoBeanArrayList.clear();
					taskDateStatusAdapter.notifyDataSetChanged();
				}
				loacalList=loacalBeanList.getDailyPerformanceInfoBeanList();
				if (loacalBeanList!=null && loacalList!=null){
					for (DailyPerformanceInfoBean bean:loacalList) {
						if (DateUtil.tranDateToTime(bean.getWork_date(),"yyyy-MM-dd")
								==DateUtil.tranDateToTime(currentDate,"yyyy-MM-dd")){

							dailyPerformanceInfoBeanArrayList.add(bean);
							taskDateStatusAdapter.notifyDataSetChanged();
							dailyInfoBean=bean;//全局-本地
							showAddTv(dailyInfoBean);
						}
					}
				}
			}
		}catch (Exception e1){
			e1.printStackTrace();
		}
	}

	private void getData(String date){
		if (TextUtils.isEmpty(date)) {
			PublicUtil.showToast("日期不可为空！");
			return;
		}
		showWaitDialog();
		SzApi.getWorkDetailsApi(date,0,getNewHandler(0, ResultToken.class));
	}




	@Override
	public void onSuccessException(int requestCode, Throwable t) {
		LogUtil.e("onSuccessException requestCode:"+requestCode);
		if (!dailyPerformanceInfoBeanArrayList.isEmpty()){
			dailyPerformanceInfoBeanArrayList.clear();
		}
		taskDateStatusAdapter.notifyDataSetChanged();
		PublicUtil.showToast("无任务数据！");
		showAddTv(null);

	}

	@Override
	public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
		LogUtil.showMultiLog("requestCode:---->"+requestCode+"/jsonString:"+jsonString);
		DailyperformanceinfoResultBean resultBean=
				JSON.parseObject(jsonString,DailyperformanceinfoResultBean.class);
		String monthlyPer=resultBean.getMonthlyPer();

		if (!TextUtils.isEmpty(monthlyPer)){
		tv_task_point.setText(
				String.format(getString(R.string.sz_task_monthlyper)+"%s",monthlyPer));
		}

		dailyInfoBean=resultBean.getJobsdata();

		//统一时间戳转成日期
		dailyInfoBean.setWork_date(DateUtil.tranTimeToDate(dailyInfoBean.getWork_date(),"yyyy-MM-dd"));
		dailyInfoBean.setUpdatetime(DateUtil.tranTimeToDate(dailyInfoBean.getUpdatetime(),"yyyy-MM-dd"));
		dailyInfoBean.setCreatetime(DateUtil.tranTimeToDate(dailyInfoBean.getCreatetime(),"yyyy-MM-dd"));

		if (!dailyPerformanceInfoBeanArrayList.isEmpty()){
				dailyPerformanceInfoBeanArrayList.clear();
			}
			LogUtil.i("DailyPerformanceInfoBean"+dailyInfoBean.getAsignJobs());
			dailyPerformanceInfoBeanArrayList.add(dailyInfoBean);


	//		任务内容
			asignJobs=dailyInfoBean.getAsignJobs();
		//任务内容转成日期格式
		for (AsignJobBean asignJobBean:asignJobs) {
			asignJobBean.setBegin_time(DateUtil.tranTimeToDate(asignJobBean.getBegin_time(),"HH:mm"));
			asignJobBean.setEnd_time(DateUtil.tranTimeToDate(asignJobBean.getEnd_time(),"HH:mm"));
			asignJobBean.setCreatetime(DateUtil.tranTimeToDate(asignJobBean.getCreatetime(),"yyyy-MM-dd"));
			LogUtil.e("时间转换后：----------》"+asignJobBean.toString());
		}
		taskDateStatusAdapter.notifyDataSetChanged();
		/**本地缓存*/
		DailyperformanceinfoResultBeanList dailyperBeanList=new DailyperformanceinfoResultBeanList();
		dailyperBeanList.setDailyPerformanceInfoBeanList(dailyPerformanceInfoBeanArrayList);
		saveDailyperLocalBeanList(dailyperBeanList);

		LogUtil.e("dailyInfoBean 是否为空："+dailyInfoBean);

		showAddTv(dailyInfoBean);
	}


	/**本地缓存*/
	public void saveDailyperLocalBeanList(DailyperformanceinfoResultBeanList dailyperBeanList){
		try {

			//先读取本地是否存在，覆盖然后重新录入
			DailyperformanceinfoResultBeanList loacalBeanList=
					(DailyperformanceinfoResultBeanList) CacheServerResponse.readObject(
							context,"DailyperformanceinfoResultBeanList");

			if (loacalBeanList!=null){
				//本地实体
				List<DailyPerformanceInfoBean> dailyPerLocalBeanList=loacalBeanList.getDailyPerformanceInfoBeanList();
				//本地实体输出
				for (DailyPerformanceInfoBean bean:dailyPerLocalBeanList) {
					LogUtil.e("本地实体输出====>"+bean.toString());
				}

				//当前实体 只有一个
				List<DailyPerformanceInfoBean> dailyPerCurrBeanList=dailyperBeanList.getDailyPerformanceInfoBeanList();
				DailyPerformanceInfoBean dailyPerformanceInfoBean=dailyPerCurrBeanList.get(0);

				List<DailyPerformanceInfoBean> dailyPerDeleteTempBeanList=new ArrayList<>();

				int size=dailyPerLocalBeanList.size();
				for (int i=0;i<size;i++){
					DailyPerformanceInfoBean dailyLocalBean=dailyPerLocalBeanList.get(i);
					if (dailyLocalBean.getWork_date().equals(dailyPerformanceInfoBean.getWork_date())){
						dailyPerDeleteTempBeanList.add(dailyLocalBean);
					}

				}
				//删除本地有的
				dailyPerLocalBeanList.removeAll(dailyPerDeleteTempBeanList);
				//添加当前最新的
				dailyPerLocalBeanList.addAll(dailyPerCurrBeanList);
				loacalBeanList.setDailyPerformanceInfoBeanList(dailyPerLocalBeanList);

				CacheServerResponse.saveObject(context,
						"DailyperformanceinfoResultBeanList", loacalBeanList);
			}else{

				CacheServerResponse.saveObject(context,
						"DailyperformanceinfoResultBeanList", dailyperBeanList);

			}


		}catch (Exception e){
			e.printStackTrace();
		}
	}


		@Override
	public void onFailureResponse(int requestCode, Throwable t) {
//			super.onFailureResponse(requestCode, t);
			PublicUtil.showToastServerOvertime();
		LogUtil.e("onFailureResponse requestCode:"+requestCode);
		showAddTv(null);

	}

	@Override
	public void onErrorResponse(int requestCode, ResultBase result) {
		super.onErrorResponse(requestCode, result);
		showAddTv(null);
	}

	/**showModleType 1:添加，0：修改，2：查看*/
	public void showAddTv(DailyPerformanceInfoBean infoBean){
		if (infoBean==null){
			if (!dailyPerformanceInfoBeanArrayList.isEmpty()){
				dailyPerformanceInfoBeanArrayList.clear();
			}
			taskDateStatusAdapter.notifyDataSetChanged();
//			设置添加的内容
			tv_addTask.setText(getString(R.string.sz_task_add_task));
			tv_addTask.setCompoundDrawables(
					getDrawable(R.mipmap.icon_sz_task_add),null,null,null);
			showModleType=1;
		}else{
			LogUtil.e("infoBean  isupdate："+infoBean.isupdate);

			if (infoBean.isupdate==true){//可修改
				showModleType=0;
				tv_addTask.setText(getString(R.string.sz_task_modify_task));
				tv_addTask.setCompoundDrawables(
						getDrawable(R.mipmap.icon_sz_task_modify),null,null,null);
			}else if (infoBean.isupdate==false){//只可查看
				tv_addTask.setText(getString(R.string.sz_task_look_task));
				tv_addTask.setCompoundDrawables(
						getDrawable(R.mipmap.icon_sz_task_look),null,null,null);
				showModleType=2;
			}
		}
	}

	private Drawable getDrawable(int id) {
		Drawable drawable=null;
			drawable=getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		if (drawable!=null){
			return drawable;
		}
		return null;
	}


	@Override
	public void changeDate(CustomDate date) {
		dateTv.setText(date.getYear()+"年"+date.getMonth()+"月");
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_addTask:
			case R.id.ll_add_taskView:

				if (showModleType==1){
					Bundle mBundle=new Bundle();
					mBundle.putString(AddTaskActivity.CURRENTDATA,currentDate);
					mBundle.putInt(AddTaskActivity.TASKOPERATETYPE,
							AddTaskActivity.TASKOPERATETYPE_ADD);
					PageJumps.PageJumps(context,AddTaskActivity.class,mBundle);

				}else if(showModleType==0){//修改
					if (dailyInfoBean!=null){
						intentDetailActivity(dailyInfoBean);
					}

				}else if(showModleType==2){//查看
					if (dailyInfoBean!=null){
						intentDetailActivity(dailyInfoBean);
					}

				}

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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
}
