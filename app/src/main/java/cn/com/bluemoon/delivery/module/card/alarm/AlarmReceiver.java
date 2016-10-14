package cn.com.bluemoon.delivery.module.card.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;

/**
 * Created by allenli on 2016/8/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Remind remind = null;

        if (Reminds.ALARM_ALERT_ACTION.equals(intent.getAction())) {


            try {
                //  remind = (Remind) intent.getSerializableExtra(Reminds.ALARM_INTENT_EXTRA);
                long remindId = intent.getLongExtra(Reminds.ALARM_INTENT_EXTRA, 0);
                if (remindId != 0) {
                    remind = Reminds.getRemind(context.getContentResolver(), remindId);
                }
            } catch (Exception ex) {

            }

            if (remind == null) {
                Reminds.setNextAlert(context);
                return;
            }
            List<Remind> list = new ArrayList<>();
            Cursor mCursor = Reminds.getAlarmsCursor(context.getContentResolver());

            if (mCursor != null) {
                if (mCursor.moveToFirst()) {
                    do {
                        Remind a = new Remind(mCursor);
                        list.add(a);
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
            }
            if (null != list && list.size() > 0) {
                for (Remind remindTemp : list
                        ) {
                    if (remindTemp.getRemindId() == remind.getRemindId()) {
                        RemindControl(context, remind);
                    } else if (remindTemp.getHour() == remind.getHour() && remindTemp.getMinute() == remind.getMinute()
                            ) {
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;
                        DaysOfWeek daysOfWeek = new DaysOfWeek(remindTemp.getRemindWeek());

                        if (daysOfWeek.isSet(today)) {
                            RemindControl(context, remindTemp);
                        }
                    }
                }

            } else {
                RemindControl(context, remind);

            }
        }
    }

    private void RemindControl(Context context, Remind remind) {
        // Disable this alarm if it does not repeat.
        if (!new DaysOfWeek(remind.getRemindWeek()).isRepeatSet()) {
            Reminds.enableAlarm(context, remind.getRemindId(), false);
        } else {
            // Enable the next alert if there is one. The above call to
            // enableAlarm will call setNextAlert so avoid calling it twice.
            Reminds.setNextAlert(context);
        }


        //创建一个启动其他Activity的Intent
        Intent intent2 = new Intent(context
                , MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pi = PendingIntent.getActivity(context
                , 0, intent2, 0);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.mipmap.icon, remind.getRemindTitle()
                , System.currentTimeMillis());
        notification.setLatestEventInfo(context, remind.getRemindTitle(),
                remind.getRemindContent(), pi);
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify((int) remind.getRemindId(), notification);
    }
}
