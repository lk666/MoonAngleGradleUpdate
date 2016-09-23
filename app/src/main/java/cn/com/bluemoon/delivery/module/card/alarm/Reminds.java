package cn.com.bluemoon.delivery.module.card.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import java.util.Calendar;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.lib.utils.LibPublicUtil;

/**
 * Created by allenli on 2016/9/18.
 */
public class Reminds {

    public static final String ALARM_ALERT_ACTION = "cn.com.bluemoon.delivery.ALARM_ALERT";

    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";


    public static long addAlarm(Context context, Remind alarm) {

        long timeInMillis = calculateAlarm(alarm);
        ContentValues values = createContentValues(alarm);
        Uri uri = context.getContentResolver().insert(
                Constants.ALARM_CONTENT_URI, values);

        if (!alarm.isClose) {
            popAlarmSetToast(context, timeInMillis);
        }
        setNextAlert(context);
        return timeInMillis;
    }


    public static void deleteAlarm(Context context, long alarmId) {
        if (alarmId == -1) return;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(Constants.ALARM_CONTENT_URI, alarmId);
        contentResolver.delete(uri, "", null);

        setNextAlert(context);
    }


    /**
     * Queries all alarms
     *
     * @return cursor over all alarms
     */
    public static Cursor getAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(
                Constants.ALARM_CONTENT_URI, Constants.ALARM_QUERY_COLUMNS,
                null, null, Constants.DEFAULT_SORT_ORDER);
    }

    // Private method to get a more limited set of alarms from the database.
    private static Cursor getFilteredAlarmsCursor(
            ContentResolver contentResolver) {
        return contentResolver.query(Constants.ALARM_CONTENT_URI, Constants.ALARM_QUERY_COLUMNS, Constants.WHERE_ENABLE,
                null, null);
    }


    private static ContentValues createContentValues(Remind alarm) {
        ContentValues values = new ContentValues(8);
        values.put("remindId", alarm.getRemindId());
        values.put("isClose", alarm.isClose ? 1 : 0);
        values.put("hour", alarm.getHour());
        values.put("minute", alarm.getMinute());
        values.put("remindTime", alarm.getRemindTime());
        values.put("remindWeek", alarm.getRemindWeek());
        values.put("remindTitle", alarm.getRemindTitle());
        values.put("remindContent", alarm.getRemindContent());
        return values;
    }

    /**
     * A convenience method to set an alarm in the Alarms
     * content provider.
     *
     * @return Time when the alarm will fire.
     */
    public static long setAlarm(Context context, Remind alarm) {
        long timeInMillis = calculateAlarm(alarm);
        ContentValues values = createContentValues(alarm);
        ContentResolver resolver = context.getContentResolver();
        resolver.update(
                ContentUris.withAppendedId(Constants.ALARM_CONTENT_URI, alarm.getRemindId()),
                values, null, null);

        if (!alarm.isClose) {
            popAlarmSetToast(context, alarm.getHour(), alarm.getMinute(), alarm.getRemindWeek());
        }
        setNextAlert(context);
        return timeInMillis;
    }

     static void popAlarmSetToast(Context context, int hour, int minute,
                                        int days) {
        popAlarmSetToast(context,
                Reminds.calculateAlarm(hour, minute, new DaysOfWeek(days))
                        .getTimeInMillis());
    }

     static void popAlarmSetToast(Context context, long timeInMillis) {
        LibPublicUtil.showToast(context, formatToast(context, timeInMillis));
    }


    private static String formatToast(Context context, long timeInMillis) {
        long delta = timeInMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String formats = context.getResources().getString(R.string.alarm_toast);
        return String.format(formats, days, hours, minutes);
    }

    public static void enableAlarm(
            final Context context, final long id, boolean enabled) {
        enableAlarmInternal(context, id, enabled);
        setNextAlert(context);
        try{
            DeliveryApi.turnRemindOnOrOff(ClientStateManager.getLoginToken(),id,enabled,null);
        }catch (Exception ex){

        }
    }

    private static void enableAlarmInternal(final Context context,
                                            final long id, boolean enabled) {
        enableAlarmInternal(context, getRemind(context.getContentResolver(), id),
                enabled);
    }

    private static void enableAlarmInternal(final Context context,
                                            final Remind alarm, boolean enabled) {
        if (alarm == null) {
            return;
        }
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues(2);
        values.put("isClose", enabled ? 0 : 1);

        // If we are enabling the alarm, calculate alarm time since the time
        // value in Alarm may be old.
        if (enabled) {
            long time = 0;
            if (!new DaysOfWeek(alarm.getRemindWeek()).isRepeatSet()) {
                time = calculateAlarm(alarm);
            }
            values.put("remindTime", time);
        }

        resolver.update(ContentUris.withAppendedId(
                Constants.ALARM_CONTENT_URI, alarm.getRemindId()), values, null, null);
    }



    private static Remind calculateNextAlert(Context context) {
        Remind alarm = null;
        long minTime = Long.MAX_VALUE;
        long now = System.currentTimeMillis();
        Cursor cursor = getFilteredAlarmsCursor(context.getContentResolver());
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Remind a = new Remind(cursor);
                    // A time of 0 indicates this is a repeating alarm, so
                    // calculate the time to get the next alert.
                    if (a.getRemindTime() == 0) {
                        a.setRemindTime(calculateAlarm(a));
                    } else if (a.getRemindTime() < now) {
                        LogUtils.v("calculateNextAlert", "Disabling expired alarm set for ");
                        // Expired alarm, disable it and move along.
                        enableAlarmInternal(context, a, false);
                        continue;
                    }
                    if (a.getRemindTime() < minTime) {
                        minTime = a.getRemindTime();
                        alarm = a;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return alarm;
    }

    //    /**
//     * Called at system startup, on time/timezone change, and whenever
//     * the user changes alarm settings.  Activates snooze if set,
//     * otherwise loads all alarms, activates next alert.
//     */
    public static void setNextAlert(Context context) {

        Remind alarm = calculateNextAlert(context);
        if (alarm != null) {
            enableAlert(context, alarm, alarm.getRemindTime());
        } else {
            disableAlert(context);
        }
    }

    private static void enableAlert(Context context, final Remind remind,
                                    final long atTimeInMillis) {
        AlarmManager am = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(ALARM_ALERT_ACTION);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ALARM_INTENT_EXTRA, remind);
        intent.putExtras(bundle);

        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);
    }

   private static void disableAlert(Context context) {
        AlarmManager am = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, new Intent(ALARM_ALERT_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }

    //获取本次响铃时间
    public static long calculateAlarm(Remind alarm) {
        return calculateAlarm(alarm.getHour(), alarm.getMinute(), new DaysOfWeek(alarm.getRemindWeek()))
                .getTimeInMillis();
    }

    static Calendar calculateAlarm(int hour, int minute,
                                   DaysOfWeek daysOfWeek) {

        // start with now
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);

        // if alarm is behind current time, advance one day
        if (hour < nowHour ||
                hour == nowHour && minute <= nowMinute) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int addDays = daysOfWeek.getNextAlarm(c);
        if (addDays > 0) c.add(Calendar.DAY_OF_WEEK, addDays);
        return c;
    }

    public static Remind getRemind(ContentResolver contentResolver, long alarmId) {
        Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(Constants.ALARM_CONTENT_URI, alarmId),
                Constants.ALARM_QUERY_COLUMNS,
                null, null, null);
        Remind alarm = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                alarm = new Remind(cursor);
            }
            cursor.close();
        }
        return alarm;
    }

}
