package cn.com.bluemoon.delivery.sz.meeting;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kymjs.kjframe.KJActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.view.MeetingTimeView.AsyncMeetingView;
import cn.com.bluemoon.delivery.sz.view.datepicker.adapter.ArrayWheelAdapter;
import cn.com.bluemoon.delivery.sz.view.datepicker.adapter.NumericWheelAdapter;
import cn.com.bluemoon.delivery.sz.view.datepicker.widget.WheelView;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

/**
 * 添加会议安排
 * @author jiangyuehua
 * */
public class TimeChooseActivity extends KJActivity {

	private int itemWidth =0;

	private Context context;

	private String defualTimeLenght="30 分";

	private List<String> ls_userNames=null;
	private List<MeetingUserInfo> ls_meetingUserInfos=null;

	public AsyncMeetingView meetingView=null;

	private WheelView year;
	private WheelView month;
	private WheelView day;
	private WheelView hour;
	private WheelView mins;
	private WheelView timeLenght;

	private Calendar c = Calendar.getInstance(Locale.CHINA);

	private String[] minutes=new String[12];//用于装载五分钟自增的分钟数

	String[] timeLenghts=new String[]{"0 分","5 分","10 分","15 分","20 分","25 分","30 分","35 分",
			"40 分","45 分","50 分","55 分","60 分",
			"1.15 小时","1.30 小时","1.45 小时","2.00 小时",
			"2.30 小时","3.00 小时","3.30 小时","4.00 小时",
			"4.30 小时","5.00 小时","5.30 小时","6.00 小时",
			"6.30 小时","7.00 小时","7.30 小时","8.00 小时",
	};

	@Override
	public void setRootView() {
		setFinishOnTouchOutside(false);//外围点击不消失
		setContentView(R.layout.activity_meetng_time_choose);
	}

	@Override
	public void initWidget() {
		super.initWidget();
		context=TimeChooseActivity.this;
		itemWidth = (int) getResources().getDimension(R.dimen.activity_meeting_times_titelWidth);

		initCustomActionBar();
		initLeftData();
		initMeetintUserInfo();

		for (int i=0;i<minutes.length;i++){
			minutes[i]= String.valueOf(i*5)+" 分";
//			LogUtil.i("五分钟制----------》："+minutes[i]);
		}

		meetingView= (AsyncMeetingView) findViewById(R.id.meetingView);
		meetingView.setRightContentAdapter(new RightContentAdapter(context,meetingView,ls_meetingUserInfos));
		meetingView.setLeftNameAdapter(new LeftNameAdapter(context,ls_userNames));

		meetingView.setMeetingViewBarTimeLenght(defualTimeLenght);//默认为30分钟

		String[] timeLenghts=defualTimeLenght.split(" ");

		int timeLenghtUnit= getTranTimeLenghtToWidth(timeLenghts);
		LogUtil.i("时长转换成相应的宽度："+timeLenghtUnit);

		if (timeLenghtUnit!=0){
			meetingView.setTimeBarParams(timeLenghtUnit,itemWidth);
		}


		meetingView.setMeetingViewBarTimeOnListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			String date=meetingView.getMeetingViewBarDate();
			String dateTime=meetingView.getMeetingViewBarTime();
			String timeLenght=meetingView.getMeetingViewBarTimeLenght();

				if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(dateTime)){
					date=date.replace("年","-").replace("月","-").replace("日","");
					LogUtil.i("获取的时间："+date);

					int curYear = Integer.parseInt(date.split("-")[0]);
					int curMonth = Integer.parseInt(date.split("-")[1]);
					int curDay = Integer.parseInt(date.split("-")[2]);

					int curHour = Integer.parseInt(dateTime.split(":")[0]);
					int curMin = Integer.parseInt(dateTime.split(":")[1]);

					LogUtil.i("获取到的时间长度单位"+timeLenght);

					showDateAndTime(curYear,curMonth,curDay,curHour,curMin,timeLenght);

				}

			}
		});

	}

	@Override
	public void widgetClick(View v) {
		EventMessageBean messageBean=new EventMessageBean();
		super.widgetClick(v);
		switch (v.getId()){
			case R.id.btn_inputCommit:
				break;
			case R.id.btn_inputCancel:
//				imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
				finish();
				break;

			default:

				break;
		}

	}


	private void initLeftData() {
		ls_userNames=new ArrayList<String>();
		for (int i=0;i<24;i++)
			ls_userNames.add("张三 "+i);
	}

	private  void initMeetintUserInfo(){
		ls_meetingUserInfos=new ArrayList<MeetingUserInfo>();
		for (int i=0;i<24;i++)
			ls_meetingUserInfos.add(new MeetingUserInfo("11","12","13","14","15","16","17","18","19","20",
					"21","22","23","24","25","26","27","28","29","30","31","32","33","34"));
	}


	private void initCustomActionBar() {
		CommonActionBar commonActionBar=new CommonActionBar(getActionBar(),new IActionBarListener() {
			@Override
			public void onBtnRight(View v) {
				// TODO Auto-generated method stu
			}
			@Override
			public void onBtnLeft(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				v.setText(R.string.sz_meeting_time_setting);
			}
		});
		TextView tv_right=commonActionBar.getTvRightView();
		tv_right.setVisibility(View.VISIBLE);
		tv_right.setText("确定");
	}

	/**
	 * 显示全部日期
	 */
	private void showDateAndTime(int curYear, int curMonth, int curDay,
								 final int curHour, final int curMin, String timeLenghtUnit){

		LogUtil.v("当前年月日"+curYear+"/"+curMonth+"/"+curDay+"/"+curHour+"/"+curMin);

		final AlertDialog dialog = new AlertDialog.Builder(TimeChooseActivity.this)
				.create();
		dialog.show();
		Window window = dialog.getWindow();
		// 设置布局
		window.setContentView(R.layout.activity_meeting_date_time_datapick);
		// 设置宽高
		window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		// 设置弹出的动画效果
		window.setWindowAnimations(R.style.AnimBottom);

		year = (WheelView) window.findViewById(R.id.wv_date_time_year);
		initYear(curYear);
		month = (WheelView) window.findViewById(R.id.wv_date_time_month);
		initMonth();
		day = (WheelView) window.findViewById(R.id.wv_date_time_day);
		initDay(curYear,curMonth);
		hour = (WheelView) window.findViewById(R.id.wv_date_time_hour);
		initHour();
		mins = (WheelView) window.findViewById(R.id.wv_date_time_min);
		initMins();

		timeLenght = (WheelView) window.findViewById(R.id.wv_date_time_lenght);
		initTimeLenght();

		// 设置当前时间
		year.setCurrentItem(curYear-1990);//当前年月日2016/8/16
		month.setCurrentItem(curMonth-1);
		day.setCurrentItem(curDay-1);//自有规则
		hour.setCurrentItem(curHour);

		/**得到相应的分钟角标*/
		int curMinIndex=0;
		for (int i=0;i<minutes.length;i++){
			if (minutes[i].equals(curMin+" 分")){
				curMinIndex=i;
			}
		}
		mins.setCurrentItem(curMinIndex);

		/**得到相应的角标*/
		int lenghtIndex=0;
		for (int i=0;i<timeLenghts.length;i++){
			if (timeLenghts[i].equals(timeLenghtUnit)){
				lenghtIndex=i;
			}
		}
		timeLenght.setCurrentItem(lenghtIndex);//为指定角标

		year.setVisibleItems(6);
		month.setVisibleItems(6);
		day.setVisibleItems(6);
		hour.setVisibleItems(6);
		mins.setVisibleItems(6);

		// 设置监听
		TextView ok = (TextView) window.findViewById(R.id.set);
		TextView cancel = (TextView) window.findViewById(R.id.cancel);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String date = (1990+year.getCurrentItem())+ "年"+ (month.getCurrentItem()+1)+"月"+(day.getCurrentItem()+1+"日");
				String curTime = (hour.getCurrentItem())+ ":"+ (minutes[mins.getCurrentItem()]);//分钟得到的是数组的角标
				curTime=curTime.replace("分","").trim();//去掉分钟及空格

				String timeLenghtValue=timeLenghts[timeLenght.getCurrentItem()];
				LogUtil.d("选取确定的时间date："+date);
				LogUtil.d("选取确定的时间time："+curTime);
				LogUtil.d("选取确定的时间timeLenghtValue："+timeLenghtValue);


				meetingView.setMeetingViewBarDate(date);//日期
				meetingView.setMeetingViewBarTime(curHour+":"+curMin,curTime,true);//起始时间 及返回结果值移动
				meetingView.setMeetingViewBarTimeLenght(timeLenghtValue);//时长

//				时长转换成相应的宽度
				String[] timeLenghts=timeLenghtValue.split(" ");

				int timeLenghtUnit=getTranTimeLenghtToWidth(timeLenghts);
				LogUtil.i("时长转换成相应的宽度："+timeLenghtUnit);

//				调整TimeBar宽度
				if (timeLenghtUnit!=0){
					meetingView.setTimeBarParams(timeLenghtUnit,itemWidth);
				}

				dialog.cancel();
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		LinearLayout cancelLayout = (LinearLayout) window.findViewById(R.id.view_none);
		cancelLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				dialog.cancel();
				return false;
			}
		});
	}


	public int getTranTimeLenghtToWidth(String[] timeLenghts){
		int timeLenghtUnit=0;
		if ("分".equals(timeLenghts[1])){
			timeLenghtUnit=(int)(Float.parseFloat(timeLenghts[0])/(float) 60*itemWidth);

		}else if ("小时".equals(timeLenghts[1])){//1.30
			timeLenghtUnit=(int)(Float.parseFloat(timeLenghts[0])*itemWidth);
		}

		return timeLenghtUnit;
	}



	/**
	 * 初始化年  年份不同 另起adapter
	 */
	private void initYear(int curYear) {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,1990, curYear+10);
		numericWheelAdapter.setLabel(" 年");
		//		numericWheelAdapter.setTextSize(15);  设置字体大小
		year.setViewAdapter(numericWheelAdapter);
		year.setCyclic(true);
	}

	/**
	 * 初始化月
	 */
	private void initMonth() {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,1, 12, "%02d");
		numericWheelAdapter.setLabel(" 月");
		//		numericWheelAdapter.setTextSize(15);  设置字体大小
		month.setViewAdapter(numericWheelAdapter);
		month.setCyclic(true);
	}

	/**
	 * 初始化天
	 */
	private void initDay(int arg1, int arg2) {
		NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(this,1, getDay(arg1, arg2), "%02d");
		numericWheelAdapter.setLabel(" 日");
		//		numericWheelAdapter.setTextSize(15);  设置字体大小
		day.setViewAdapter(numericWheelAdapter);
		day.setCyclic(true);
	}

	/**
	 * 初始化时
	 */
	private void initHour() {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,0, 23, "%02d");
		numericWheelAdapter.setLabel(" 时");
		//		numericWheelAdapter.setTextSize(15);  设置字体大小
		hour.setViewAdapter(numericWheelAdapter);
		hour.setCyclic(true);
	}

	/**
	 * 初始化分
	 */
	private void initMins() {
		ArrayWheelAdapter arrayWheelAdapter=new ArrayWheelAdapter(this,minutes);
		mins.setViewAdapter(arrayWheelAdapter);
		mins.setCyclic(true);
	}
	/**
	 * 初始化时长
	 */
	private void initTimeLenght() {
		ArrayWheelAdapter arrayWheelAdapter=new ArrayWheelAdapter(this,timeLenghts);
		timeLenght.setViewAdapter(arrayWheelAdapter);
		timeLenght.setCyclic(true);
	}


	/**
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
			case 0:
				flag = true;
				break;
			default:
				flag = false;
				break;
		}
		switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = 31;
				break;
			case 2:
				day = flag ? 29 : 28;
				break;
			default:
				day = 30;
				break;
		}
		return day;
	}

}







/**左侧名称*/
class LeftNameAdapter extends BaseAdapter {

	private List<String> listNames = null;
	private Context context;

	public LeftNameAdapter(Context context, List<String> listNames) {
		this.listNames = listNames;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listNames.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Viewholder viewholder=null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.meeting_layout_item_name,null);

			viewholder=new Viewholder();
			viewholder.tv_meetingName= (TextView) convertView.findViewById(R.id.tv_meetingName);
			convertView.setTag(viewholder);
		}else {
			viewholder= (Viewholder) convertView.getTag();
		}
		viewholder.tv_meetingName.setText(listNames.get(position).toString());

		return convertView;
	}

	class Viewholder{
		TextView tv_meetingName;
	}



}



class RightContentAdapter extends BaseAdapter {
	private List<MeetingUserInfo> listUserInfos = null;
	private Context context;
	private int totalWidth=0;
	private AsyncMeetingView meetingView;

	public RightContentAdapter(Context context, AsyncMeetingView meetingView,List<MeetingUserInfo> listUserInfos) {
		this.listUserInfos = listUserInfos;
		this.context = context;
		this.meetingView=meetingView;
	}

	@Override
	public int getCount() {
		return listUserInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewholdTexter viewholder=null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.meeting_layout_item_userinfo,null);
			viewholder=new ViewholdTexter();
			viewholder.view= (TextView) convertView.findViewById(R.id.tv_01);

			totalWidth=meetingView.getTotalWidth();

			viewholder.view.setWidth(totalWidth);

			Log.d("ListView 宽度",totalWidth+"");

			convertView.setTag(viewholder);

		}else {
			viewholder= (ViewholdTexter) convertView.getTag();
		}

//		MeetingUserInfo meetingUserInfo=listUserInfos.get(position);

//			viewholder.view.setText(meetingUserInfo.getTxt1());
//			传递时间段到内部 进行时间块的填充

		return convertView;
	}

	class ViewholdTexter{

		TextView view;

	}

}

	class MeetingUserInfo{
		private String txt1;
		private String txt2;
		private String txt3;
		private String txt4;
		private String txt5;
		private String txt6;
		private String txt7;
		private String txt8;
		private String txt9;
		private String txt10;
		private String txt11;
		private String txt12;
		private String txt13;
		private String txt14;
		private String txt15;
		private String txt16;
		private String txt17;
		private String txt18;
		private String txt19;
		private String txt20;
		private String txt21;
		private String txt22;
		private String txt23;
		private String txt24;

		public MeetingUserInfo(String txt1, String txt2, String txt3, String txt4, String txt5, String txt6
				, String txt7, String txt8, String txt9, String txt10, String txt11, String txt12, String txt13
				, String txt14, String txt15, String txt16, String txt17, String txt18, String txt19, String txt20
				, String txt21, String txt22, String txt23, String txt24){
			this.txt1=txt1;
			this.txt2=txt2;
			this.txt3=txt3;
			this.txt4=txt4;
			this.txt5=txt5;
			this.txt6=txt6;
			this.txt7=txt7;
			this.txt8=txt8;
			this.txt9=txt9;
			this.txt10=txt10;
			this.txt11=txt11;
			this.txt12=txt12;
			this.txt13=txt13;
			this.txt14=txt14;
			this.txt15=txt15;
			this.txt16=txt16;
			this.txt17=txt17;
			this.txt18=txt18;
			this.txt19=txt19;
			this.txt20=txt20;
			this.txt21=txt21;
			this.txt22=txt22;
			this.txt23=txt23;
			this.txt24=txt24;
		}


	}











