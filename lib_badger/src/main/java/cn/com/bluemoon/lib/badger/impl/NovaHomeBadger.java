package cn.com.bluemoon.lib.badger.impl;

import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.util.Arrays;
import java.util.List;

import cn.com.bluemoon.lib.badger.BadgeUtil;
import cn.com.bluemoon.lib.badger.interf.Badger;
import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;

/**
 * @author bm
 */
public class NovaHomeBadger implements Badger {

    private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
    private static final String COUNT = "count";
    private static final String TAG = "tag";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount,
                             Notification notification) throws ShortcutBadgeException {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG, componentName.getPackageName() + "/" + componentName.getClassName());
        contentValues.put(COUNT, badgeCount);
        context.getContentResolver().insert(Uri.parse(CONTENT_URI), contentValues);

        BadgeUtil.showNotification(context, notification);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.teslacoilsw.launcher");
    }
}
