package cn.com.bluemoon.delivery.sz.view.calendar;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import cn.com.bluemoon.delivery.sz.util.DateUtil;

public class CustomDate implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	public int year;
	public int month;
	public int day;
	public int week;
	
	public CustomDate(int year,int month,int day){
		if(month > 12){
			month = 1;
			year++;
		}else if(month <1){
			month = 12;
			year--;
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public CustomDate(){
		this.year = DateUtil.getYear();
		this.month = DateUtil.getMonth();
		this.day = DateUtil.getCurrentMonthDay();
	}

	public boolean isEqual(CustomDate date){
		if(this.year == date.getYear() && this.month == date.getMonth() && this.day == date.getDay()){
			return true;
		}
		return false;
	}
	
	public static CustomDate modifiDayForObject(CustomDate date,int day){
		CustomDate modifiDate = new CustomDate(date.year,date.month,day);
		return modifiDate;
	}
	@Override
	public String toString() {
		String currDate=year+"-"+month+"-"+day;
		SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			currDate=sd1.format(sd1.parse(currDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return currDate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

}
