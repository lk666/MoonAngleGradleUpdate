package cn.com.bluemoon.delivery.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.bluemoon.lib.utils.LibDateUtil;

@SuppressLint("SimpleDateFormat")
public class DateUtil extends LibDateUtil {

    public static Date strToDate(String format, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(System.currentTimeMillis());
        }
    }

    public static String dateToStr(String format, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String getCurDate() {
        return getTime(System.currentTimeMillis(), "yyyy-MM-dd");
    }

    public static String getCurDateAndWeek() {
        return getTime(System.currentTimeMillis(), "yyyy-MM-dd  EE");
    }

    public static long getTimeOffsetMonth() {
        return getTimeOffsetMonth(-1);
    }

    public static String getTime(long t) {
        return getTime(t, "yyyy-MM-dd");
    }
    public static String getDotTime(long t) {
        return getTime(t, "yyyy.MM.dd");
    }

    public static String getTimeToHours(long t) {
        return getTime(t, "HH:mm");
    }

    public static String getTimeToYMDHM(long t) {
        return getTime(t, "yyyy-MM-dd HH:mm");
    }

    /**
     * @param t 精确到毫秒
     */
    public static String getTimeToYMDHMS(long t) {
        return getTimeMill(t, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * @param t 精确到毫秒
     */
    public static String getTimeMill(long t, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date curDate = new Date(t);
        String time = formatter.format(curDate);
        return time;
    }


    public static String getTimeToYM(long t) {
        return getTime(t, "yyyy-MM");
    }

    /**
     * 将当前时间戳转化为字符串
     *
     * @return
     */
    public static String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getTimes(long start, long end) {
        return getTime(start) + " " + getTimeToHours(start) + "-" + getTimeToHours(end);
    }

}
