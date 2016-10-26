package cn.com.bluemoon.lib.badger.impl;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import cn.com.bluemoon.lib.badger.BadgeUtil;
import cn.com.bluemoon.lib.badger.interf.Badger;
import cn.com.bluemoon.lib.badger.util.BroadcastHelper;
import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;

/**
 * @author Gernot Pansy
 */
public class ApexHomeBadger implements Badger {

    private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
    private static final String PACKAGENAME = "package";
    private static final String COUNT = "count";
    private static final String CLASS = "class";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount,Notification notification) throws ShortcutBadgeException {
        BadgeUtil.showNotification(context, notification);

        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, componentName.getPackageName());
        intent.putExtra(COUNT, badgeCount);
        intent.putExtra(CLASS, componentName.getClassName());
        if (BroadcastHelper.canResolveBroadcast(context, intent)) {
            context.sendBroadcast(intent);
        } else {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent.toString());
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.anddoes.launcher");
    }
}
