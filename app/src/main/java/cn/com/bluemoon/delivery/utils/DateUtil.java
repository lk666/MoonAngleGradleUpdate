package cn.com.bluemoon.delivery.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import cn.com.bluemoon.lib.utils.LibDateUtil;

@SuppressLint("SimpleDateFormat")
public class DateUtil extends LibDateUtil{

	public static Date strToDate(String format, String date) {  
	    SimpleDateFormat formatter = new SimpleDateFormat(format);  
	    try {  
	        return formatter.parse(date);  
	    } catch (ParseException e) {  
	        e.printStackTrace();  
	        return new Date();  
	    }  
	}  
	  
	public static String dateToStr(String format, Date date) {  
	    SimpleDateFormat formatter = new SimpleDateFormat(format);  
	    return formatter.format(date);  
	}  
	
	public static String getCurDate(){
		return getTime(System.currentTimeMillis(), "yyyy-MM-dd");
	}
	public static String getCurDateAndWeek(){
		return getTime(System.currentTimeMillis(), "yyyy-MM-dd  EE");
	}
	public static long getTimeOffsetMonth(){
		return getTimeOffsetMonth(-1);
	}
}
