package cn.com.bluemoon.delivery.sz.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.bluemoon.delivery.sz.view.calendar.CustomDate;

/**
 * Created by dujiande on 2016/8/9.
 */
public class DateUtil {

	public static String[] weekName = { "周日", "周一", "周二", "周三", "周四", "周五","周六" };

	public static int getMonthDays(int year, int month) {
		if (month > 12) {
			month = 1;
			year += 1;
		} else if (month < 1) {
			month = 12;
			year -= 1;
		}
		int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int days = 0;

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			arr[1] = 29; // 闰年2月29天
		}

		try {
			days = arr[month - 1];
		} catch (Exception e) {
			e.getStackTrace();
		}

		return days;
	}

	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static int getCurrentMonthDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static int getWeekDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}
	public static int getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}
	public static CustomDate getNextSunday() {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7 - getWeekDay()+1);
		CustomDate date = new CustomDate(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
		return date;
	}

	public static int[] getWeekSunday(int year, int month, int day, int pervious) {
		int[] time = new int[3];
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.add(Calendar.DAY_OF_MONTH, pervious);
		time[0] = c.get(Calendar.YEAR);
		time[1] = c.get(Calendar.MONTH )+1;
		time[2] = c.get(Calendar.DAY_OF_MONTH);
		return time;

	}

	/***根据年月算出，当前月的多少号**/
	public static int getWeekDayFromDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFromString(year, month));
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;

		Log.d(Calendar.DAY_OF_WEEK+"week_index==============", "" + week_index);

		if (week_index < 0) {
			week_index = 0;
		}
		return week_index;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(int year, int month) {//默认为当月的一号。
		String dateString = year + "-" + (month > 9 ? month : ("0" + month))
				+ "-01";
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateString);

			//			Log.d(""+"SimpleDateFormat==============", ""+sdf.format(date));
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return date;
	}

	public static boolean isToday(CustomDate date){
		return(date.year == DateUtil.getYear() &&
				date.month == DateUtil.getMonth()
				&& date.day == DateUtil.getCurrentMonthDay());
	}

	public static boolean isCurrentMonth(CustomDate date){
		return(date.year == DateUtil.getYear() &&
				date.month == DateUtil.getMonth());
	}

	public static String showPeriodTime(String bTime,String eTime){
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

		String time1 = formatTime(bTime,sdf1,sdf2);
		String time2 = formatTime(eTime,sdf1,sdf2);

		return time1+" - "+time2;
	}

	public static String formatTime(String bTime,SimpleDateFormat sdf1,SimpleDateFormat sdf2){
		String timeStr = "";
		try {
			Date date = sdf1.parse(bTime);
			timeStr = sdf2.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeStr;
	}



	/**毫秒转日期*/
	public static String tranTimeToDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(Long.valueOf(time)));
	}

	public static String tranTimeToDate(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(time)));
	}

	/**日期转毫秒*/
	public static long tranDateToTime(String date){
		long times=0;
		try {
			DateFormat dm= new SimpleDateFormat("HH:mm");
			times=dm.parse(date.toString()).getTime();
			LogUtil.i("times:"+times);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return times;
	}

	/**日期转毫秒*/
	public static long tranDateToTime(String date,String format){
		long times=0;
		try {
			DateFormat dm= new SimpleDateFormat(format);
			times=dm.parse(date.toString()).getTime();
			LogUtil.i("times:"+times);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return times;
	}



}