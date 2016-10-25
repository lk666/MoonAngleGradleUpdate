package cn.com.bluemoon.lib.badger;

import android.content.Context;
import android.util.Log;

import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;

/**
 * Created by bm on 2016/10/21.
 */
public class BadgeUtil {

    private final static String TAG = "badger";

    /**
     * 更新当前应用的角标
     *
     * @param context    Caller context
     * @param badgeCount Desired badge count
     * @return true in case of success, false otherwise
     */
    public static boolean applyCount(Context context, int badgeCount) {
        try {
            ShortcutBadger.applyCountOrThrow(context, badgeCount);
            return true;
        } catch (ShortcutBadgeException e) {
            Log.e(TAG, "Unable to execute badge", e);
            return false;
        }
    }

    /**
     * 清除当前应用的角标
     *
     * @param context Caller context
     * @return true in case of success, false otherwise
     */
    public static boolean removeCount(Context context) {
        return applyCount(context, 0);
    }
}
