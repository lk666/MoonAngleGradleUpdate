package cn.com.bluemoon.lib.badger;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.List;

import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;

/**
 * Created by bm on 2016/10/21.
 */
public class BadgeUtil {

    private final static String TAG = "badger";
    private static int TYPE_NORMAL = 10000;

    /**
     * 更新所有应用的角标
     *
     * @param context    Caller context
     * @param badgeCount Desired badge count
     * @return true in case of success, false otherwise
     */
    private static boolean applyAllCount(Context context, int badgeCount) {
        List<ResolveInfo> list = getAllResolveInfo(context);
        boolean success = false;
        for (int i = 0; i < list.size(); i++) {
            success = applyCountByPackage(context, badgeCount, list.get(i).activityInfo
                    .packageName, null);
            if (!success) {
                break;
            }
        }
        return success;
    }

    /**
     * 更新指定包名角标
     *
     * @param context
     * @param badgeCount
     * @param packageName
     * @param notification
     * @return
     */
    public static boolean applyCountByPackage(Context context, int badgeCount, String
            packageName, Notification notification) {
        try {
            BadgerManager.applyCountOrThrow(context, badgeCount, packageName, notification);
            return true;
        } catch (ShortcutBadgeException e) {
            showNotification(context, notification);
            Log.e(TAG, "Unable to execute badge", e);
            return false;
        }
    }

    /**
     * 更新当前应用的角标
     *
     * @param context    Caller context
     * @param badgeCount Desired badge count
     * @return true in case of success, false otherwise
     */
    public static boolean applyCount(Context context, int badgeCount, Notification notification) {
        try {
            BadgerManager.applyCountOrThrow(context, badgeCount, notification);
            return true;
        } catch (ShortcutBadgeException e) {
            showNotification(context, notification);
            Log.e(TAG, "Unable to execute badge", e);
            return false;
        }
    }

    public static boolean applyCount(Context context, int badgeCount) {
        return applyCount(context, badgeCount, null);
    }

    /**
     * 清除所有应用的角标
     *
     * @param context
     * @return
     */
    private static boolean removeAllCount(Context context) {
        return applyAllCount(context, 0);
    }

    /**
     * 清除指定包名应用的角标
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean removeCountByPackage(Context context, String packageName) {
        return applyCountByPackage(context, 0, packageName, null);
    }

    /**
     * 清除当前应用的角标
     *
     * @param context
     * @return
     */
    public static boolean removeCount(Context context) {
        return applyCount(context, 0, null);
    }

    /**
     * 展示通知
     *
     * @param context
     * @param notification
     * @param notificationId
     */
    public static void showNotification(Context context, Notification notification, int
            notificationId) {
        if (notification != null) {
            NotificationManager manger = (NotificationManager) context.getSystemService(Context
                    .NOTIFICATION_SERVICE);
            manger.notify(notificationId, notification);
        }
    }

    public static void showNotification(Context context, Notification notification) {
        showNotification(context, notification, TYPE_NORMAL++);
    }

    /**
     * 获取所有应用的配置
     *
     * @param context
     * @return
     */
    public static List<ResolveInfo> getAllResolveInfo(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return context.getPackageManager().queryIntentActivities(
                intent, PackageManager.GET_ACTIVITIES);
    }

}
