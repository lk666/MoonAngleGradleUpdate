package cn.com.bluemoon.delivery.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.com.bluemoon.delivery.R;


/**
 * TODO   select order deliver or receive time
 *
 * @author wangshanhai
 * @version 1.1.0
 * @time 2016-4-10 上午11:43:51
 */

public class DateTimePickDialogUtil implements OnDateChangedListener,
        OnTimeChangedListener {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;//2016-1-1 10:10
    private long initTime; //1433232323232
    private String initDaTeTime2;//yyyyMMddHHmmss(20160421231011)

    private Activity activity;
    private long time;
    OnDetailClickLister onDetailClickLister;


    public interface OnDetailClickLister {
        void btnClickLister(long time, String dateTime);
    }

    /**
     * @param activity
     * @param initDateTime        2016-1-1 10:00
     * @param onDetailClickLister
     */
    public DateTimePickDialogUtil(Activity activity, String initDateTime, OnDetailClickLister
            onDetailClickLister) {
        this.activity = activity;
        this.initDateTime = initDateTime;
        this.onDetailClickLister = onDetailClickLister;
    }

    /**
     * @param activity
     * @param initTime            long型时间戳  142121212121
     * @param onDetailClickLister
     */
    public DateTimePickDialogUtil(Activity activity, long initTime, OnDetailClickLister
            onDetailClickLister) {
        this.activity = activity;
        this.initTime = initTime;
        this.onDetailClickLister = onDetailClickLister;
    }

    /**
     * @param activity
     * @param initDaTeTime2       yyyyMMddHHmmss
     * @param type
     * @param onDetailClickLister
     */
    public DateTimePickDialogUtil(Activity activity, String initDaTeTime2, boolean type,
                                  OnDetailClickLister onDetailClickLister) {
        this.activity = activity;
        this.initDaTeTime2 = initDaTeTime2;
        this.onDetailClickLister = onDetailClickLister;
    }


    @SuppressLint("SimpleDateFormat")
    public void init(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else if (!(null == initDaTeTime2 || "".equals(initDaTeTime2))) {
            try {
                calendar.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(initDaTeTime2));
            } catch (Exception ex) {
                initDateTime = calendar.get(Calendar.YEAR) + "-"
                        + calendar.get(Calendar.MONTH) + "-"
                        + calendar.get(Calendar.DAY_OF_MONTH) + "- "
                        + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                        + calendar.get(Calendar.MINUTE);
            }
        } else {
            if (initTime > 0) {
                calendar.setTimeInMillis(initTime * 1000);
            }
            initDateTime = calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "- "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);

        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(0);
        //timePicker.setCurrentMinute(Integer.getInteger("0"));
    }


    public AlertDialog dateTimePicKDialog() {

        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        init(datePicker, timePicker);
        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setView(dateTimeLayout)
                .setPositiveButton(activity.getResources().getString(R.string.btn_ok), new
                        DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onDetailClickLister.btnClickLister(time, "time");
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.btn_cancel), new
                        DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onDetailClickLister.btnClickLister(time, "dismiss");
                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return ad;
    }


    private boolean mIgnoreEvent = false;
    private static final int TIME_PICKER_INTERVAL = 60;


    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
        if (mIgnoreEvent)
            return;
        if (minute % TIME_PICKER_INTERVAL != 0) {
            int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
            if (minute == 60)
                minute = 0;
            mIgnoreEvent = true;
            timePicker.setCurrentMinute(minute);
            mIgnoreEvent = false;
        }
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(), 0);
/*        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                calendar.get(Calendar.MINUTE));// minute 也返回去*/
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd" +
                " HH:mm");
        dateTime = sdf.format(calendar.getTime());
        time = calendar.getTimeInMillis() / 1000;
        //ad.setTitle(dateTime);
    }


    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        // 将初始日期时间2012-07-02 16:45 拆分成年 月 日 时 分 秒
        String date = spliteString(initDateTime, " ", "index", "front"); // 日期
        String time = spliteString(initDateTime, " ", "index", "back"); // 时间

        String yearStr = spliteString(date, "-", "index", "front"); // 年份
        String monthAndDay = spliteString(date, "-", "index", "back"); // 月日

        String monthStr = spliteString(monthAndDay, "-", "index", "front"); // 月
        String dayStr = spliteString(monthAndDay, "-", "index", "back"); // 日

        String hourStr = spliteString(time, ":", "index", "front"); // 时
        String minuteStr = spliteString(time, ":", "index", "back"); // 分

        int currentYear = Integer.valueOf(yearStr.trim());
        int currentMonth = Integer.valueOf(monthStr.trim()) - 1;
        int currentDay = Integer.valueOf(dayStr.trim());
        int currentHour = Integer.valueOf(hourStr.trim());
        int currentMinute = Integer.valueOf(minuteStr.trim());

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

    /**
     * 截取子串
     *
     * @param srcStr      源串
     * @param pattern     匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }

}
