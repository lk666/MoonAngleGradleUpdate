package cn.com.bluemoon.lib.badger.impl;

import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Arrays;
import java.util.List;

import cn.com.bluemoon.lib.badger.BadgeUtil;
import cn.com.bluemoon.lib.badger.interf.Badger;
import cn.com.bluemoon.lib.badger.util.CloseHelper;
import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;

/**
 * @author bm
 *         Deprecated, Samesung devices will use DefaultBadger
 */
@Deprecated
public class SamsungHomeBadger implements Badger {
    private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
    private static final String[] CONTENT_PROJECTION = new String[]{"_id", "class"};

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount,
                             Notification notification) throws ShortcutBadgeException {

        Uri mUri = Uri.parse(CONTENT_URI);
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(mUri, CONTENT_PROJECTION, "package=?", new
                    String[]{componentName.getPackageName()}, null);
            if (cursor != null) {
                String entryActivityName = componentName.getClassName();
                boolean entryActivityExist = false;
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    ContentValues contentValues = getContentValues(componentName, badgeCount,
                            false);
                    contentResolver.update(mUri, contentValues, "_id=?", new String[]{String
                            .valueOf(id)});
                    if (entryActivityName.equals(cursor.getString(cursor.getColumnIndex("class"))
                    )) {
                        entryActivityExist = true;
                    }
                }

                if (!entryActivityExist) {
                    ContentValues contentValues = getContentValues(componentName, badgeCount, true);
                    contentResolver.insert(mUri, contentValues);
                }
            }
        } finally {
            CloseHelper.close(cursor);
        }

        BadgeUtil.showNotification(context, notification);
    }

    private ContentValues getContentValues(ComponentName componentName, int badgeCount, boolean
            isInsert) {
        ContentValues contentValues = new ContentValues();
        if (isInsert) {
            contentValues.put("package", componentName.getPackageName());
            contentValues.put("class", componentName.getClassName());
        }

        contentValues.put("badgecount", badgeCount);

        return contentValues;
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.sec.android.app.launcher",
                "com.sec.android.app.twlauncher"
        );
    }
}
