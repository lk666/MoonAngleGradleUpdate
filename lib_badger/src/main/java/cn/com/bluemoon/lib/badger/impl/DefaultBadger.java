package cn.com.bluemoon.lib.badger.impl;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.lib.badger.BadgeUtil;
import cn.com.bluemoon.lib.badger.interf.Badger;
import cn.com.bluemoon.lib.badger.util.BroadcastHelper;
import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;

/**
 * @author leolin
 */
public class DefaultBadger implements Badger {
    private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount,Notification notification) throws ShortcutBadgeException {


        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, componentName.getPackageName());
        intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, componentName.getClassName());
        if (BroadcastHelper.canResolveBroadcast(context, intent)) {
            context.sendBroadcast(intent);
        } else {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent.toString());
        }

        BadgeUtil.showNotification(context, notification);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return new ArrayList<String>(0);
    }
}
