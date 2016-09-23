package cn.com.bluemoon.delivery.module.card.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by allenli on 2016/9/22.
 */
public class AlarmInitReceiver extends BroadcastReceiver {

    /**
     * Sets alarm on ACTION_BOOT_COMPLETED.  Resets alarm on
     * TIME_SET, TIMEZONE_CHANGED
     * 接受开机启动完成的广播，
     * 设置闹钟，当时区改变也设置
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Reminds.setNextAlert(context);
    }
}
