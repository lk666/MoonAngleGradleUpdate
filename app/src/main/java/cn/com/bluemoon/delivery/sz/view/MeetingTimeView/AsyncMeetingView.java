package cn.com.bluemoon.delivery.sz.view.MeetingTimeView;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.UtilTools;

/**
 * Created by jiangyuehua on 16/7/25.
 */
public class AsyncMeetingView extends LinearLayout{

	private int timeModel = 24*2;//半小时制
	private String timeStart = "8:30";

	private Handler handler = new Handler();
	/**
	 * item 宽高 最终于工具类转换
	 */
	private int itemWidth = (int) getResources().getDimension(R.dimen.activity_meeting_times_titelWidth);
	private int ItemHeight = (int) getResources().getDimension(R.dimen.activity_meeting_times_titelHeight);

	private SimpleDateFormat formatDate=new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);


	private AsyncHorizontalScrollView shs_titel;
	private AsyncHorizontalScrollView shs_rightcontent;

	private ListView lv_left_name;
	private ListView lv_rightcontent;

	private LinearLayout ll_rightitle;
	private Button btn_meetingType;
	private MoveView moveView_timeBar;

	private BaseAdapter LeftNameAdapter, RightContentAdapter;
	private Context context;

	private OnClickListener onClickListener=null;

	private String[] times;

	private ScrollView innerScrollView_content;

	private LinearLayout ll_meetingViewBar;
	private TextView tv_meetingViewBarDate,tv_meetingViewBarTime,tv_meetingViewBarTimeLenght;
	private String lefttimeStart = "";
	private String leftTranTimeStart = "";

	/**标识手动对话框选取时间时不联动变化*/
	private boolean isChoose=false;

	//timeBar 移动的单位个数
	private double timeBarUnit = 0;
	//timeBar 最终的时间
	private int finalTimeHour = 0;
	private int finalTimeMin = 0;
	private String finalBarTime = "";
	private String strTimeBarUnit;

	private int lvScrollLocationX;

	private boolean isTranDay=false;

	private int tbLocationLeft;



	public AsyncMeetingView(Context context) {
		this(context, null);
	}

	public AsyncMeetingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_meeting_time_view, this);//this
		initView();

		LogUtil.i("相应的60dp单位宽度："+itemWidth);
	}

	private void initView() {
		initTitelTimesItem();

		tv_meetingViewBarDate = (TextView) findViewById(R.id.tv_meetingViewBarDate);
		tv_meetingViewBarDate.setText(formatDate.format(new Date()));
		tv_meetingViewBarTime = (TextView) findViewById(R.id.tv_meetingViewBarTime);
		tv_meetingViewBarTime.setText(timeStart);//初始时间

		ll_meetingViewBar= (LinearLayout) findViewById(R.id.ll_meetingViewBar);


		tv_meetingViewBarTimeLenght = (TextView) findViewById(R.id.tv_meetingViewBarTimeLenght);
		btn_meetingType = (Button) findViewById(R.id.btn_meetingType);

		moveView_timeBar = (MoveView) findViewById(R.id.view_timeBar);

		lv_rightcontent = (ListView) findViewById(R.id.lv_rightcontent);
		lv_rightcontent.setFocusable(false);//解决不置顶的问题 焦点问题
		shs_rightcontent = (AsyncHorizontalScrollView) findViewById(R.id.shs_rightcontent);
		shs_rightcontent.setFocusable(false);//关键关掉的 解决不置顶的问题 焦点问题
//		传递外围控件用于判断是否在屏幕内显示
		moveView_timeBar.setHorizontalScrollView(shs_rightcontent);
//		左侧参会员名称
		lv_left_name = (ListView) findViewById(R.id.lv_left_name);
		lv_left_name.setFocusable(false);
//		头部时间栏
		shs_titel = (AsyncHorizontalScrollView) findViewById(R.id.shs_titel);
		innerScrollView_content = (ScrollView) findViewById(R.id.innerScrollView_content);
//		时间移动条父控件
		moveView_timeBar.setParentScrollView(innerScrollView_content);

		shs_rightcontent.setOnScrollChangedListener(new AsyncHorizontalScrollView.OnScrollChangedListener() {
			@Override
			public void onScrollChangedListener(int l, int t) {
				setScrollChangedListenerX(l);

			}
		});
		moveView_timeBar.setMoveViewScrollListener(new MoveView.MoveViewScrollListener() {
			@Override
			public void moveViewScrollListener(int viewRectRight, int viewRectLeft) {
				setMoveViewTimeBarScrollListener(viewRectRight,viewRectLeft);
			}
		});
	}

	/**2，
	 * 也分为二种情况：
	 * 	一：scrollview 是否有移动距离的判断 有的话
	 * 	左边距离换算成时间加上自身移动换算成的时间
	 * 	二：scrollView有移动，TimeBar二次移动的问题 （这时是因为多加了TimeBar移动的距离）
	 * 	通过
	 * */
	private void setMoveViewTimeBarScrollListener(int viewRectRight, int viewRectLeft) {
		try {
			LogUtil.i("接口传递的TimeBar位置信息：viewRectRight:"+viewRectRight+" viewRectLeft:"+viewRectLeft);
			tbLocationLeft = viewRectLeft;
			timeBarUnit = ((double) (tbLocationLeft - itemWidth)) / (itemWidth*2);//得到多少个时间单位，换算成时间 换成半小时制
			BigDecimal bdTimeBarUnit = new BigDecimal(timeBarUnit).setScale(2, BigDecimal.ROUND_HALF_UP);//四舍五入
			strTimeBarUnit = String.valueOf(bdTimeBarUnit);
			strTimeBarUnit = strTimeBarUnit.replace(".", ":");
			LogUtil.i("接口传递的TimeBar位置信息：strTimeBarUnit:"+strTimeBarUnit);


			Log.w("左侧起始时间：", lefttimeStart + "--" + finalBarTime);
	//					外界对应的时间
			String[] leftTimes = lefttimeStart.split(":");
	//					按滑动距离 换算成时间单位段
			String[] barUnitTimes = strTimeBarUnit.split(":");

			if (!TextUtils.isEmpty(lefttimeStart)) {//内容有移动
				int barTimeHour = Integer.parseInt(barUnitTimes[0]);//小时
				BigDecimal bdtimeMin = new BigDecimal(
						Integer.parseInt(barUnitTimes[1]) * 0.6).setScale(0, BigDecimal.ROUND_UP);
				int barTimeMin = Integer.parseInt(String.valueOf(bdtimeMin));//分钟

				finalTimeHour = Integer.parseInt(leftTimes[0]) + barTimeHour;
				finalTimeMin = Integer.parseInt(leftTimes[1]) + barTimeMin;//自增效果

	//					Log.w("finalTimeMin",finalTimeMin+"");
				LogUtil.i("TimeBar:finalTimeHour==========>"+finalTimeHour);

				finalBarTime=tranTimeString(finalTimeHour,finalTimeMin);

				/**小时24点转换时的判断*/

				LogUtil.i("TimeBar:finalBarTime==========>"+finalBarTime);//TimeBar:finalBarTime==========>24:12

//				if ()


				int currHour= Integer.parseInt(finalBarTime.split(":")[0]);
				int currMin= Integer.parseInt(finalBarTime.split(":")[1]);

				//
				String reutlTime=TranMinuteTime(String.valueOf(currMin),currHour);

				tv_meetingViewBarTime.setText(reutlTime);



			}else{//一开始就移动TimeBar

	//					些时的开始时间为8：30
				String[] startBarTimes = timeStart.split(":");
				int timeHour = Integer.parseInt(startBarTimes[0]) + Integer.parseInt(barUnitTimes[0]);
				/**小数转成分钟  分钟＝小数*100/60   换成 分钟＝小数*100/30 */
				BigDecimal timeMin = new BigDecimal(
						Integer.parseInt(barUnitTimes[1]) * 0.6).setScale(0, BigDecimal.ROUND_UP);//换成半小时制
				int intTimeMin = Integer.parseInt(String.valueOf(timeMin))
						+Integer.parseInt(startBarTimes[1]);

				LogUtil.d(Integer.parseInt(String.valueOf(timeMin))+"/起始时间的变更2："+intTimeMin);

				String currMin = "";//当前分钟
				if (barUnitTimes.length > 1) {
					if (intTimeMin < 60) {
						if (intTimeMin == 0) {
							currMin = "00";
						} else {
							if (intTimeMin < 10) {
								currMin = "0" + intTimeMin;
							} else
								currMin = "" + intTimeMin;
						}
					} else {//>=60  小时添加
						if (intTimeMin == 60) {
							currMin = "00";
						} else {
							int tranMin = intTimeMin - 60;
							if (tranMin < 10) {
								currMin = "0" + tranMin;
							} else {
								currMin = String.valueOf(tranMin);
							}
						}
						timeHour++;
					}

					lefttimeStart = TranMinuteTime(currMin, timeHour);
					LogUtil.d("shs_rightcontent 最终转换成的时间：" + lefttimeStart);

				}

				tv_meetingViewBarTime.setText(lefttimeStart);

			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**ListView内容移动监听*/
	private void setScrollChangedListenerX(int l) {
		try {
			lvScrollLocationX = l;
	//				左边滑动量换算成为多少个间隔单位
			double leftUnit = ((double) lvScrollLocationX) / (itemWidth*2);
			LogUtil.d("ListView内容移动监听 产生的水平移动量"+l+"leftUnit:"+leftUnit);
			BigDecimal bdLeftUnit = new BigDecimal(
					leftUnit).setScale(2, BigDecimal.ROUND_HALF_UP);//四舍五入
			String leftScorllXUnit = String.valueOf(bdLeftUnit);//double
			LogUtil.d("shs_rightcontent左侧滑动转换成的单位："+leftScorllXUnit);//1.36  前为小时 后为分钟

//			当为15.5时，为跨天的时间转换
			//Calendar //去获取转换日期

			Calendar c=Calendar.getInstance(Locale.CHINA);
			int oldYear = c.get(Calendar.YEAR); //获取当前年份
			int oldMonth = c.get(Calendar.MONTH)+1;//获取当前月份
			int oldDays = c.get(Calendar.DATE);//获取当前月份的日期号码

			if (Float.parseFloat(leftScorllXUnit)>=15.46f) {
				isTranDay=true;
				tv_meetingViewBarDate.setText(oldYear + "年" + (oldMonth) + "月" + (oldDays + 1) + "日");
			}else{
				if (isTranDay==true){
					tv_meetingViewBarDate.setText(oldYear + "年" + (oldMonth) + "月" + (oldDays ) + "日");
					isTranDay=false;
				}
			}
			LogUtil.d("去获取转换日期-------------->"+oldYear+"年"+oldMonth+"月"+(oldDays));


			leftScorllXUnit = leftScorllXUnit.replace(".", ":");
			String[] leftUnitime = leftScorllXUnit.split(":");

			LogUtil.d("shs_rightcontent左侧滑动转换成的单位分解："+leftUnitime[0]+":"+leftUnitime[1]);

			/**起始时间*/
			String[] startTimes=timeStart.split(":");
			LogUtil.d("起始时间："+startTimes[0]+"/"+startTimes[1]);

			int timeHour = Integer.parseInt(startTimes[0]) + Integer.parseInt(leftUnitime[0]);

			/**小数转成分钟  分钟＝小数*100/60   换成 分钟＝小数*100/30 */
			BigDecimal timeMin = new BigDecimal(
					Integer.parseInt(leftUnitime[1]) * 0.6)
					.setScale(0, BigDecimal.ROUND_UP);//换成半小时制

			int intTimeMin = Integer.parseInt(String.valueOf(timeMin))
					+Integer.parseInt(startTimes[1]);

			LogUtil.d(Integer.parseInt(String.valueOf(timeMin))+"/起始时间的变更2："+intTimeMin);

			String currMin = "";//当前分钟

				if (leftUnitime.length > 1) {
					if (intTimeMin < 60) {
						if (intTimeMin == 0) {
							currMin = "00";
						} else {
							if (intTimeMin < 10) {
								currMin = "0" + intTimeMin;
							} else
								currMin = "" + intTimeMin;
						}
					} else {//>=60  小时添加
						if (intTimeMin == 60) {
							currMin = "00";
						} else {
							int tranMin = intTimeMin - 60;
							if (tranMin < 10) {
								currMin = "0" + tranMin;
							} else {
								currMin = String.valueOf(tranMin);
							}
						}
						timeHour++;
					}
					lefttimeStart = TranMinuteTime(currMin, timeHour);
					LogUtil.d("shs_rightcontent 最终转换成的时间：" + lefttimeStart);

				}

				/**
				 * 1、两种情况
				 * 	一：当TimeBar没有移动时以起始时间为基准换距离来换算
				 * 	二：当TimeBar有移动时，起启时间+TimeBar移动的距离得到对应的时间
				 * */
			if (TextUtils.isEmpty(strTimeBarUnit)) {
				if (isChoose==false){//手动选择时间时不联动
					tv_meetingViewBarTime.setText(lefttimeStart);
				}else{
					isChoose=false;
				}

			}else if (!TextUtils.isEmpty(strTimeBarUnit)) {//TimeBar有移动

				LogUtil.w("imeBar有移动 ＝＝＝此时的开始左边时间===="+lefttimeStart);
				/*******此时开始时间变为了TimeBar 移动的的距离转化的时间 *****/
				leftTranTimeStart = lefttimeStart;//
	//					换算成时间
				String[] barTimes = strTimeBarUnit.split(":");//如 2.7

				int barTimeHour = Integer.parseInt(barTimes[0]);//小时
				BigDecimal bdtimeMin = new BigDecimal(
						Integer.parseInt(barTimes[1]) * 0.6).setScale(0, BigDecimal.ROUND_UP);
				int barTimeMin = Integer.parseInt(String.valueOf(bdtimeMin));//分钟
				int currMoveBarHour = timeHour + barTimeHour;
				int currMoveBarMin = Integer.parseInt(currMin) + barTimeMin;

				LogUtil.i("currMoveBarHour===变化后的起始时间（小时）===>"+currMoveBarHour+":"+currMoveBarMin);
				LogUtil.i("strTimeBarUnit======>"+strTimeBarUnit+"///"+barTimeMin);

//				int tranDayTime=currMoveBarHour

	//			小时自增的问题
				leftTranTimeStart=tranTimeString(currMoveBarHour,currMoveBarMin);

				/**也要小时判断*/
				LogUtil.i("TimeBar:leftTranTimeStart==========>"+leftTranTimeStart);

				int currBarHour= Integer.parseInt(leftTranTimeStart.split(":")[0]);
				int currBarMin= Integer.parseInt(leftTranTimeStart.split(":")[1]);

				String reutlTime=TranMinuteTime(String.valueOf(currBarMin),currBarHour);


				LogUtil.w("TimeBar有移动时的自增"+currMoveBarHour + ":" + currMoveBarMin);
				LogUtil.w("TimeBar有移动时的自增 结束	lefttimeStart-----"+ lefttimeStart + "/tran:" + leftTranTimeStart);


				LogUtil.d("isChoose-------1------->"+isChoose);
				if (isChoose==false){//手动选择时间时不联动
					tv_meetingViewBarTime.setText(reutlTime);

				}else{
					isChoose=false;
				}
				LogUtil.d("isChoose-------2------->"+isChoose);

			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void setTimeBarParams(int widht,int marginLeft) {
		FrameLayout.LayoutParams flLayoutParams=
				new  FrameLayout.LayoutParams(widht, FrameLayout.LayoutParams.MATCH_PARENT);
		flLayoutParams.setMargins(marginLeft,0,0,0);
		moveView_timeBar.setLayoutParams(flLayoutParams);
	}


	/**转换成分钟字符串*/
	public String tranTimeString(int finalTimeHour,int finalTimeMin){
		LogUtil.i("finalTimeHour:"+finalTimeHour+"finalTimeMin"+finalTimeMin+"isTranDay:"+isTranDay);


		Calendar c=Calendar.getInstance(Locale.CHINA);
		int oldYear = c.get(Calendar.YEAR); //获取当前年份
		int oldMonth = c.get(Calendar.MONTH)+1;//获取当前月份
		int oldDays = c.get(Calendar.DATE);//获取当前月份的日期号码

		// 天数转换 23-60
		if (finalTimeHour>=23 && finalTimeMin>=58){
			if (isTranDay==false){
				tv_meetingViewBarDate.setText(oldYear + "年" + (oldMonth) + "月" + (oldDays + 1) + "日");
				isTranDay=true;
			}
		}

		if(finalTimeHour<=23 && finalTimeMin<58){
			if (isTranDay==true)
				tv_meetingViewBarDate.setText(oldYear + "年" + (oldMonth) + "月" + (oldDays) + "日");
				isTranDay=false;
		}


		String  finalBarTime="";
		if (finalTimeMin < 60) {
			if (finalTimeMin == 0) {
				finalBarTime = finalTimeHour + ":00";
			} else {
				if (finalTimeMin < 10) {
					finalBarTime = finalTimeHour + ":0" + finalTimeMin;
				} else
					finalBarTime = finalTimeHour + ":" + finalTimeMin;
			}
		} else {
			if (finalTimeMin == 60) {
				if (finalTimeHour + 1==24){
					finalBarTime =  "00:00";
				}else{
					finalBarTime = (finalTimeHour + 1) + ":00";
				}
			} else if (finalTimeMin > 60) {
//							分钟自增小时后，自增的个位数处理
				if ((finalTimeMin - 60) < 10) {
					finalBarTime = (finalTimeHour + 1) + ":0" + (finalTimeMin - 60);
				} else {
					finalBarTime = (finalTimeHour + 1) + ":" + (finalTimeMin - 60);
				}
			}
		}
		LogUtil.d("tranTimeString-------24转化："+finalBarTime);
		return finalBarTime;
	}

	/**尾数，0-2分钟归0，3-5分钟为5*/
	public String TranMinuteTime(String currMin,int timeHour){
		String reultTime="";

		/**小时的转换判断*/
		if (timeHour>=24){
			if (timeHour==24){
				timeHour=0;
			}else{
				timeHour=timeHour-24;
			}
		}

		int firstNumH=0,secondNumM=0;
		if (currMin.length()>1){
		 firstNumH= Integer.parseInt(currMin.substring(0,1));
		 secondNumM= Integer.parseInt(currMin.substring(1,currMin.length()));
		}else{//个位数时
			secondNumM= Integer.parseInt(currMin);
		}

		LogUtil.i(currMin+"-->分钟尾数算法："+firstNumH+"/"+secondNumM);

			if (secondNumM>=0 &&secondNumM<=2 ){//0-2
				if (firstNumH>=6){
					timeHour++;
					currMin="00";
				}else{
					currMin=firstNumH+"0";
				}
			}else if(secondNumM>2 &&secondNumM<=7){
				if (firstNumH>=6){
					timeHour++;
					currMin="00";
				}else{
					currMin=firstNumH+"5";
				}
			}else if(secondNumM>7 &&secondNumM<=9){
				/**小时临界点转换*/
				if ((firstNumH+1)>=6){
					timeHour++;
					if (timeHour==24){//
						timeHour=0;
					}
					currMin="00";
				}else{
					currMin=(firstNumH+1)+"0";
				}
			}

		reultTime = timeHour + ":" + currMin;
		LogUtil.i(currMin+"-->分钟尾数算法2："+reultTime);

		return reultTime;
	}

	/**
	 * 实例时间段
	 */
	private void initTitelTimesItem() {
		ll_rightitle = (LinearLayout) findViewById(R.id.ll_rightitle);
		times = transFormTimeHalf(timeModel, timeStart);
		for (int i = 0; i < times.length; i++) {

			AsyncMeetingTextView tv_meetingViewBarTime = new AsyncMeetingTextView(context);
//			TextView tv_meetingViewBarTime = new TextView(context);
			tv_meetingViewBarTime.setLayoutParams(
					new LinearLayout.LayoutParams(itemWidth, LayoutParams.MATCH_PARENT));//布局改变

			tv_meetingViewBarTime.setText(times[i]);
			tv_meetingViewBarTime.setGravity(Gravity.CENTER);
			tv_meetingViewBarTime.setTextSize(14);

			ll_rightitle.addView(tv_meetingViewBarTime);
			Log.v("times====", times[i]);
		}
	}

	/**
	 * 根据列数生成相应的时间数组 以8：30/startPoint 起始点 开头
	 * 半小时制
	 */
	public String[] transFormTimeHalf(int column, String startPoint) {//24,8:30

		int startHour=0,startMinute=0;

		String tranTime="";

		String[] times = new String[column];
		for (int i = 0; i < times.length; i++) {//48
			String[] startPoints=startPoint.split(":");
			if (startPoints.length>1){
				startHour=Integer.parseInt(startPoints[0]);
				startMinute=Integer.parseInt(startPoints[1]);
			}

			if (i==0){
				times[i]=startPoint;
				startPoint=startPoint;
			}else{
				Log.v("时间段分解333：","====>"+startHour+"/"+startMinute);
				if (startHour>0 && startHour<=24){
					if (startMinute==0){
						//					到达24小时时，转换
						if (startHour<24){
							tranTime=startHour+":30";
						}else if (startHour==24 && startHour<=32){
							tranTime="00:30";
						}
					}else if(startMinute==30){
						if (startHour+1==24)
							tranTime="00:00";
						else
							tranTime=startHour+1+":00";
					}
				}else{
					if (startMinute==0){
						tranTime=startHour+":30";
					}else if(startMinute==30){
						tranTime=startHour+1+":00";
					}
				}
				times[i]=tranTime;
				startPoint=tranTime;
			}
		}
		return times;
	}
	/**
	 * 根据列数生成相应的时间数组 以8：30/startPoint 起始点 开头
	 * 一小时制
	 */
	public String[] transFormTime(int column, int startPoint) {//24,7

		String[] times = new String[column];
		for (int i = 0; i < times.length; i++) {
			if (i >= startPoint && i < times.length) {// =7 && <24
				if (i != 24) {
					times[i - startPoint] = i + ":00";
				} else {
					times[i - startPoint] = "00:00";//24:00转成 00：00
				}
			} else if (i >= 0 && i < startPoint) {//
				times[times.length - (startPoint - i)] = i + ":00";//（index 19-24）
			}
		}
		return times;
	}


	public void setLeftNameAdapter(BaseAdapter leftNameAdapter) {
		LeftNameAdapter = leftNameAdapter;
		if (LeftNameAdapter != null)
			lv_left_name.setAdapter(LeftNameAdapter);
		shs_titel.setmView(shs_rightcontent);
		UtilTools.setListViewHeightBasedOnChildren(lv_left_name);
	}


	public void setRightContentAdapter(BaseAdapter rightContentAdapter) {
		RightContentAdapter = rightContentAdapter;
		if (RightContentAdapter != null)
			lv_rightcontent.setAdapter(RightContentAdapter);

		shs_rightcontent.setmView(shs_titel);
		UtilTools.setListViewHeightBasedOnChildren(lv_rightcontent);
	}



	public int getTimeBarLocationX() {
		if (this.moveView_timeBar != null) {
			return (int) this.moveView_timeBar.getTranslationX();
		}
		return 0;
	}



	public int  getTotalWidth(){
		int totalWidth=0;
		totalWidth=times.length*itemWidth;
		return totalWidth;
	}

	public void setMeetingViewBarTimeOnListener(OnClickListener onClickListener){
		this.onClickListener=onClickListener;
		ll_meetingViewBar.setOnClickListener(onClickListener);
	}

//	tv_meetingViewBarDate,tv_meetingViewBarTime,tv_meetingViewBarTimeLenght
	public void setMeetingViewBarDate(String dateContent){
		if (tv_meetingViewBarDate!=null){
			tv_meetingViewBarDate.setText(dateContent);
		}

	}
	public String getMeetingViewBarDate(){
		String dateContent="";
		if (tv_meetingViewBarDate!=null){
			dateContent=tv_meetingViewBarDate.getText().toString();
		}
		return dateContent;
	}

	public void setMeetingViewBarTime(String oldTime, String curTime,boolean isChooses){

		isChoose=isChooses;

		if (tv_meetingViewBarDate!=null){

			leftTranTimeStart=curTime;//
			tv_meetingViewBarTime.setText(curTime);
//			触发TimeBar的移动
			int moveTranslationX=0;

			/**得到间隔的时间单位*/
			String[] oldTimes=oldTime.split(":");
			String[] currTimes=curTime.split(":");

//			相差的小时
			/**当两位数时间，转到凌晨个们数时，添加判断*/
			int oldHour=Integer.parseInt(oldTimes[0]);
			int currHour=Integer.parseInt(currTimes[0]);

			LogUtil.i(oldTime+"---"+curTime+"得到间隔的时间单位--starthour---currHour-1->"+oldHour+"/"+currHour);

			int unitHour=0;
			if (currHour>=oldHour){//此时分两种情况 如回退：0：10-13：00/如跨天回退2：00-23：00
				if((oldHour>=0 && oldHour<8) && (currHour>=8 && currHour<24)){//跨天回退
					unitHour=currHour-(24+oldHour);//负数
					LogUtil.i("小时换算成单位----00--->："+unitHour);
				}else if((oldHour>=8 && oldHour<24) && (currHour>=8 && currHour<24)){
					unitHour=currHour-oldHour;//正数
					LogUtil.i("小时换算成单位----01--->："+unitHour);
				}else// if((oldHour>=0 && oldHour<8) && (currHour>=0 && currHour<8))
				{
					unitHour=currHour-oldHour;//正数
				}

			}else{//如 回退
				//正常回退 10：10-9：00  ----在8-24小时内
//				if (currHour==0){
//					currHour=24;
//				}
				if ((currHour>=8 && currHour<24) && (oldHour>=8 && oldHour<24)){
					unitHour=currHour-oldHour;
					LogUtil.i("小时换算成单位----1--->："+unitHour);
				}else if((currHour>=0 && currHour<=8) && (oldHour>=8 && oldHour<24)){//23:00-2:00
//					还要进行分钟的判断 如 8:30---7:20 ///已到边界的判断 去移动TimeBar
//					if ()

					unitHour=(currHour+24)-oldHour;
					LogUtil.i("小时换算成单位----2--->："+unitHour);
				}else if((currHour>=0 && currHour<=8) && (oldHour>=8 && oldHour<24)){// 跨天 9:20-6:30
					unitHour=24-(oldHour-currHour);//
					LogUtil.i("小时换算成单位----3--->："+unitHour);
				}else if((currHour>=8 && currHour<24) && (oldHour>=0 && oldHour<=8)){//3:55---22:55
					unitHour=(currHour+24)-oldHour;
					LogUtil.i("小时换算成单位----4--->："+unitHour);
				}else if((currHour>=0 && currHour<=8) && (oldHour>=0 && oldHour<=8)){//6:55---3:55
					unitHour=currHour-oldHour;
					LogUtil.i("小时换算成单位----5--->："+unitHour);
				}else {
					unitHour=currHour-oldHour;
					LogUtil.i("小时换算成单位----default--->："+unitHour);

				}
			}
			LogUtil.i("小时换算成单位："+unitHour);

			if (unitHour>=0){
				moveTranslationX=unitHour*itemWidth*2;
			}else{//负数
				moveTranslationX=unitHour*itemWidth*2;
			}
			LogUtil.i("小时转成的距离："+moveTranslationX);

//			相差的分钟转成
			int unitMin=Integer.parseInt(currTimes[1])-Integer.parseInt(oldTimes[1]);
				LogUtil.i("分钟转成的unitMin ："+unitMin);

			if (unitMin>=0){
//				也分为跨天分钟 如2：20-23：30
				if((oldHour>=0 && oldHour<=8) && (currHour>8 && currHour<24)){

					float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
					moveTranslationX+=(int)tranUnitMin;//小数
					LogUtil.i("分钟转成 unitMin>=0 跨天分钟负数距离："+tranUnitMin);

				}else if((oldHour>8 && oldHour<24) && (currHour>8 && currHour<24)){//正常 11：10- 10：30
					/** jiangyuehua 1 到最边时换成移动TimeBar */
					if (unitHour<0){//实为40分钟而不是20分钟
						unitMin=Math.abs(unitHour)*60-unitMin;
						float tranUnitMin=(float) -unitMin/(float)60*itemWidth*2;
						moveTranslationX=(int)tranUnitMin;//此时为分钟数为准
						LogUtil.i(unitMin+"分钟转成 unitMin>=0 8-24  1小时内："+tranUnitMin);
					}else{
						float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
						moveTranslationX+=(int)tranUnitMin;//此时为分钟数为准
						LogUtil.i(unitMin+"分钟转成 unitMin>=0 8-24  2小时内："+tranUnitMin);
					}
				}else if((oldHour>8 && oldHour<24) && (currHour>=0 && currHour<=8)){//跨天 分钟正数 9:20-6:30
					float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
					moveTranslationX+=(int)tranUnitMin;//小数
					LogUtil.i("分钟转成 unitMin>=0 0-8小时内："+tranUnitMin);
				}
//				else if((currHour>8 && currHour<24) && (oldHour>=0 && oldHour<=8)){//3:20---22:50
//					float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
//					moveTranslationX+=(int)tranUnitMin;//小数
//					LogUtil.i("分钟转成 unitMin>=0 3:20---22:50 小时内："+tranUnitMin);
//				}else if((currHour>=0 && currHour<=8) && (oldHour>=0 && oldHour<=8)){//6:30---3:50
//					float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
//					moveTranslationX+=(int)tranUnitMin;//小数
//					LogUtil.i("小时换算成单位----5--->："+unitHour);
//				}
				else{
					float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
					moveTranslationX+=(int)tranUnitMin;//小数
					LogUtil.i("分钟转成 unitMin>=0 defutl 小时内："+tranUnitMin);

				}

				LogUtil.i("分钟转成的距离2："+moveTranslationX);
			}else{//负数

				if((oldHour>8 && oldHour<24) && (currHour>8 && currHour<24)){//正常 11：40- 10：10
					if (unitHour<=0){//实为40分钟而不是20分钟
						unitMin=Math.abs(unitHour)*60-unitMin;
						float tranUnitMin=(float) -unitMin/(float)60*itemWidth*2;
						moveTranslationX=(int)tranUnitMin;//小数
						LogUtil.i("分钟转成 负数>=0 8-24小时内<=0："+tranUnitMin);
					}else{
						float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
						moveTranslationX+=(int)tranUnitMin;//小数
						LogUtil.i("分钟转成 负数>=0 8-24小时内>0："+tranUnitMin);

					}
				}else if((oldHour>8 && oldHour<24) && (currHour>=0 && currHour<=8)){//跨天 分钟负数 9:50-6:20
					float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
					moveTranslationX+=(int)tranUnitMin;//小数
					LogUtil.i("分钟转成 负数>=0 跨天 小时内："+tranUnitMin);
				}
 				else{//3:50---22:20   6:50---3:30 6:55--6:30
					if (unitHour<0){//实为40分钟而不是20分钟
						unitMin=Math.abs(unitHour)*60-unitMin;
						float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
						moveTranslationX=(int)tranUnitMin;//小数
					}else{
						float tranUnitMin=(float) unitMin/(float)60*itemWidth*2;
						moveTranslationX+=(int)tranUnitMin;//小数

					}
					LogUtil.i("分钟转成的距离 负数："+moveTranslationX);

				}

			}
			LogUtil.i(tbLocationLeft+"选取后得到移动的距离1："+moveTranslationX);

			LogUtil.i("选取后得到移动的距离 lvScrollLocationX1："+lvScrollLocationX);

			int finalLocationX=lvScrollLocationX+moveTranslationX;
			LogUtil.i("选取后得到移动的距离 finalLocationX："+finalLocationX);

			setTimeBarTranslationX(finalLocationX);//有误差

		}

	}
	/**获取会议开始时间*/
	public String getMeetingViewBarTime(){
		String timeContent="";
		if (tv_meetingViewBarTime!=null){
			timeContent=tv_meetingViewBarTime.getText().toString();
		}
		return timeContent;
	}
	public void setMeetingViewBarTimeLenght(String timeLenght){
		if (tv_meetingViewBarTimeLenght!=null){
			tv_meetingViewBarTimeLenght.setText(timeLenght);
		}
	}
	/**获取时长*/
	public String getMeetingViewBarTimeLenght(){
		String timeLenght="";
		if (tv_meetingViewBarTimeLenght!=null){
			timeLenght=tv_meetingViewBarTimeLenght.getText().toString();
		}
		return timeLenght;
	}

	/**联动发生变化*/
	public void setTimeBarTranslationX(final int location) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (shs_rightcontent!=null){
					LogUtil.i("移动测试："+location);
//					shs_rightcontent.smoothScrollTo(location, 0);
					shs_rightcontent.scrollTo(location, 0);


				}
			}
		}, 200);
	}



}
