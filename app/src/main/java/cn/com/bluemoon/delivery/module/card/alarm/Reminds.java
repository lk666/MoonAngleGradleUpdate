package cn.com.bluemoon.delivery.module.card.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.Calendar;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.card.ResultRemind;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * Created by allenli on 2016/9/18.
 */
public class Reminds {


    //    // This action triggers the AlarmReceiver as well as the AlarmKlaxon. It
//    // is a public action used in the manifest for receiving Alarm broadcasts
//    // from the alarm manager.
    public static final String ALARM_ALERT_ACTION = "cn.com.bluemoon.delivery.ALARM_ALERT";
    //
//    // This string is used when passing an Alarm object through an intent.
    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";
    //
    private final static String DM12 = "E h:mm aa";
    private final static String DM24 = "E k:mm";
    private final static String M12 = "h:mm aa";
    // Shared with DigitalClock
    final static String M24 = "kk:mm";

//    /**
//     * Removes an existing Alarm.  If this alarm is snoozing, disables
//     * snooze.  Sets next alert.
//     */
//    public static void deleteAlarm(Context context, int alarmId) {
//        if (alarmId == -1) return;
//
//        /* If alarm is snoozing, lose it */
//        disableSnoozeAlert(context, alarmId);
//
//        setNextAlert(context);
//    }
//
//

    /**
     * A convenience method to set an alarm in the Alarms
     * content provider.
     *
     * @return Time when the alarm will fire.
     */
    public static long setAlarm(Context context, Remind alarm) {

        long timeInMillis = calculateAlarm(alarm);
        setNextAlert();

        return timeInMillis;
    }

    //    /**
//     * A convenience method to enable or disable an alarm.
//     *
//     * @param id             corresponds to the _id column
//     * @param enabled        corresponds to the ENABLED column
//     */
//
    public static void enableAlarm(
            final Remind alarm, boolean enabled) {
        setNextAlert();
    }

    public static Remind calculateNextAlert(List<Remind> reminds) {
        Remind alarm = null;
        long minTime = Long.MAX_VALUE;
        long now = System.currentTimeMillis();

        for (Remind remind : reminds
                ) {
            if (remind.getRemindTime() == 0) {
                remind.setRemindTime(calculateAlarm(remind));
            } else if (remind.getRemindTime() < now) {
//                enableAlarmInternal(context, a, false);
                continue;
            }
            if (remind.getRemindTime() < minTime) {
                minTime = remind.getRemindTime();
                alarm = remind;
            }

        }
        return alarm;
    }


   static AsyncHttpResponseHandler remindHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                ResultRemind resultRemind = JSON.parseObject(responseString,ResultRemind.class);
                if(resultRemind.getResponseCode()== Constants.RESPONSE_RESULT_SUCCESS){
                    Remind alarm = calculateNextAlert(resultRemind.getRemindList());
                    if (alarm != null) {
                        enableAlert(AppContext.getInstance(), alarm, alarm.getRemindTime());
                    } else {
                        disableAlert(AppContext.getInstance());
                    }
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            PublicUtil.showToastServerOvertime();
        }

    };


    //
//    /**
//     * Disables non-repeating alarms that have passed.  Called at
//     * boot.
//     */
//    public static void disableExpiredAlarms(final Context context, Remind alarm) {
//
//        enableAlarmInternal(context, alarm, false);
//
//    }

    //
//    /**
//     * Called at system startup, on time/timezone change, and whenever
//     * the user changes alarm settings.  Activates snooze if set,
//     * otherwise loads all alarms, activates next alert.
//     */
    public static void setNextAlert() {
        DeliveryApi.getRemindList(ClientStateManager.getLoginToken(),remindHandler);

    }

    //
//    /**
//     * Sets alert in AlarmManger and StatusBar.  This is what will
//     * actually launch the alert when the alarm triggers.
//     *
//     * @param alarm Alarm.
//     * @param atTimeInMillis milliseconds since epoch
//     */
    private static void enableAlert(Context context, final Remind remind,
                                    final long atTimeInMillis) {
        AlarmManager am = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(ALARM_ALERT_ACTION);

        // XXX: This is a slight hack to avoid an exception in the remote
        // AlarmManagerService process. The AlarmManager adds extra data to
        // this Intent which causes it to inflate. Since the remote process
        // does not know about the Alarm class, it throws a
        // ClassNotFoundException.
        //
        // To avoid this, we marshall the data ourselves and then parcel a plain
        // byte[] array. The AlarmReceiver class knows to build the Alarm
        // object from the byte[] array.

        Bundle bundle = new Bundle();
        bundle.putSerializable(ALARM_INTENT_EXTRA, remind);
        intent.putExtras(bundle);

        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);

//        setStatusBarIcon(context, true);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(atTimeInMillis);
        String timeString = formatDayAndTime(context, c);
        saveNextAlarm(context, timeString);
    }

    //
//    /**
//     * Disables alert in AlarmManger and StatusBar.
//     *
//     * @param id Alarm ID.
//     */
    static void disableAlert(Context context) {
        AlarmManager am = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, new Intent(ALARM_ALERT_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
//        setStatusBarIcon(context, false);
        saveNextAlarm(context, "");
    }

    //
//    static void saveSnoozeAlert(final Context context, final int id,
//                                final long time) {
//        SharedPreferences prefs = context.getSharedPreferences(
//                DeskClockMainActivity.PREFERENCES, 0);
//        if (id == -1) {
//            clearSnoozePreference(context, prefs);
//        } else {
//            SharedPreferences.Editor ed = prefs.edit();
//            ed.putInt(PREF_SNOOZE_ID, id);
//            ed.putLong(PREF_SNOOZE_TIME, time);
//            ed.apply();
//        }
//        // Set the next alert after updating the snooze.
//        setNextAlert(context);
//    }
//
//    /**
//     * Disable the snooze alert if the given id matches the snooze id.
//     */
//    static void disableSnoozeAlert(final Context context, final int id) {
//        SharedPreferences prefs = context.getSharedPreferences(
//                DeskClockMainActivity.PREFERENCES, 0);
//        int snoozeId = prefs.getInt(PREF_SNOOZE_ID, -1);
//        if (snoozeId == -1) {
//            // No snooze set, do nothing.
//            return;
//        } else if (snoozeId == id) {
//            // This is the same id so clear the shared prefs.
//            clearSnoozePreference(context, prefs);
//        }
//    }
//
//    // Helper to remove the snooze preference. Do not use clear because that
//    // will erase the clock preferences. Also clear the snooze notification in
//    // the window shade.
//    private static void clearSnoozePreference(final Context context,
//                                              final SharedPreferences prefs) {
//        final int alarmId = prefs.getInt(PREF_SNOOZE_ID, -1);
//        if (alarmId != -1) {
//            NotificationManager nm = (NotificationManager)
//                    context.getSystemService(Context.NOTIFICATION_SERVICE);
//            nm.cancel(alarmId);
//        }
//    };
//
//    /**
//     * If there is a snooze set, enable it in AlarmManager
//     * @return true if snooze is set
//     */
//    private static boolean enableSnoozeAlert(final Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(
//                DeskClockMainActivity.PREFERENCES, 0);
//
//        int id = prefs.getInt(PREF_SNOOZE_ID, -1);
//        if (id == -1) {
//            return false;
//        }
//        long time = prefs.getLong(PREF_SNOOZE_TIME, -1);
//
//        // Get the alarm from the db.
//        final Alarm alarm = getAlarm(context.getContentResolver(), id);
//        if (alarm == null) {
//            return false;
//        }
//        // The time in the database is either 0 (repeating) or a specific time
//        // for a non-repeating alarm. Update this value so the AlarmReceiver
//        // has the right time to compare.
//        alarm.time = time;
//
//        enableAlert(context, alarm, time);
//        return true;
//    }
//
//    /**
//     * Tells the StatusBar whether the alarm is enabled or disabled
//     */
//    private static void setStatusBarIcon(Context context, boolean enabled) {
//        Intent alarmChanged = new Intent("android.intent.action.ALARM_CHANGED");
//        alarmChanged.putExtra("alarmSet", enabled);
//        context.sendBroadcast(alarmChanged);
//    }

    //
    public static long calculateAlarm(Remind alarm) {
        return calculateAlarm(alarm.getHour(), alarm.getMinute(), new DaysOfWeek(alarm.getRemindWeek()))
                .getTimeInMillis();
    }

    //
//    /**
//     * Given an alarm in hours and minutes, return a time suitable for
//     * setting in AlarmManager.
//     */
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

    //    static String formatTime(final Context context, int hour, int minute,
//                             Alarm.DaysOfWeek daysOfWeek) {
//        Calendar c = calculateAlarm(hour, minute, daysOfWeek);
//        return formatTime(context, c);
//    }
//
//    /* used by AlarmAlert */
    static String formatTime(final Context context, Calendar c) {
        String format = get24HourMode(context) ? M24 : M12;
        return (c == null) ? "" : (String) DateFormat.format(format, c);
    }

    //
//    /**
//     * Shows day and time -- used for lock screen
//     */
    private static String formatDayAndTime(final Context context, Calendar c) {
        String format = get24HourMode(context) ? DM24 : DM12;
        return (c == null) ? "" : (String) DateFormat.format(format, c);
    }

    //
//    /**
//     * Save time of the next alarm, as a formatted string, into the system
//     * settings so those who care can make use of it.
//     */
    static void saveNextAlarm(final Context context, String timeString) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.NEXT_ALARM_FORMATTED,
                timeString);
    }
//

    /**
     * @return true if clock is set to 24-hour mode
     */
    static boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }
}
