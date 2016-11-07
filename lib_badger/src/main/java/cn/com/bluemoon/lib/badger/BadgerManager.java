package cn.com.bluemoon.lib.badger;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import cn.com.bluemoon.lib.badger.impl.AdwHomeBadger;
import cn.com.bluemoon.lib.badger.impl.ApexHomeBadger;
import cn.com.bluemoon.lib.badger.impl.AsusHomeLauncher;
import cn.com.bluemoon.lib.badger.impl.DefaultBadger;
import cn.com.bluemoon.lib.badger.impl.HuaweiHomeBadger;
import cn.com.bluemoon.lib.badger.impl.LGHomeBadger;
import cn.com.bluemoon.lib.badger.impl.NewHtcHomeBadger;
import cn.com.bluemoon.lib.badger.impl.NovaHomeBadger;
import cn.com.bluemoon.lib.badger.impl.OPPOHomeBader;
import cn.com.bluemoon.lib.badger.impl.SamsungHomeBadger;
import cn.com.bluemoon.lib.badger.impl.SonyHomeBadger;
import cn.com.bluemoon.lib.badger.impl.XiaomiHomeBadger;
import cn.com.bluemoon.lib.badger.impl.ZukHomeBadger;
import cn.com.bluemoon.lib.badger.interf.Badger;
import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;


/**
 * @author bm
 */
public class BadgerManager {

    private static Badger curBadger;
    private static ComponentName componentName;

    private static final List<Class<? extends Badger>> BADGERS = new LinkedList<>();

    //添加类型
    static {
        BADGERS.add(AdwHomeBadger.class);
        BADGERS.add(ApexHomeBadger.class);
        BADGERS.add(NewHtcHomeBadger.class);
        BADGERS.add(NovaHomeBadger.class);
        BADGERS.add(SonyHomeBadger.class);
        BADGERS.add(XiaomiHomeBadger.class);
        BADGERS.add(AsusHomeLauncher.class);
        BADGERS.add(HuaweiHomeBadger.class);
//        BADGERS.add(LGHomeBadger.class);
        BADGERS.add(OPPOHomeBader.class);
//        BADGERS.add(SamsungHomeBadger.class);
        BADGERS.add(ZukHomeBadger.class);
    }

    /**
     * Tries to update the notification count, throw a {@link ShortcutBadgeException} if it fails
     *
     * @param context    Caller context
     * @param badgeCount Desired badge count
     */
    public static void applyCountOrThrow(Context context, int badgeCount, String packageName,
                                         Notification notification) throws ShortcutBadgeException {
        if (TextUtils.isEmpty(packageName)) {
            throw new ShortcutBadgeException("packageName can not empty");
        }
        ComponentName name = context.getPackageManager().getLaunchIntentForPackage(packageName)
                .getComponent();
        if (curBadger == null) {
            boolean launcherReady = initBadger(context);
            if (!launcherReady)
                throw new ShortcutBadgeException("No default launcher available");
        }
        try {
            curBadger.executeBadge(context, name, badgeCount, notification);
        } catch (Exception e) {
            throw new ShortcutBadgeException("Unable to execute badge", e);
        }
    }

    /**
     * Tries to update the notification count, throw a {@link ShortcutBadgeException} if it fails
     *
     * @param context    Caller context
     * @param badgeCount Desired badge count
     */
    public static void applyCountOrThrow(Context context, int badgeCount, Notification
            notification) throws ShortcutBadgeException {
        if (curBadger == null) {
            boolean launcherReady = initBadger(context);
            if (!launcherReady)
                throw new ShortcutBadgeException("No default launcher available");
        }
        if (componentName == null) {
            componentName = context.getPackageManager().getLaunchIntentForPackage(context
                    .getPackageName()).getComponent();
        }
        try {
            curBadger.executeBadge(context, componentName, badgeCount, notification);
        } catch (Exception e) {
            throw new ShortcutBadgeException("Unable to execute badge", e);
        }
    }

    // Initialize Badger if a launcher is availalble (eg. set as default on the device)
    // Returns true if a launcher is available, in this case, the Badger will be set and
    // curBadger will be non null.
    private static boolean initBadger(Context context) {

        componentName = context.getPackageManager().getLaunchIntentForPackage(context
                .getPackageName()).getComponent();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo == null || resolveInfo.activityInfo.name.toLowerCase().contains("resolver"))
            return false;

        String currentHomePackage = resolveInfo.activityInfo.packageName;
        for (Class<? extends Badger> badger : BADGERS) {
            Badger shortcutBadger = null;
            try {
                shortcutBadger = badger.newInstance();
            } catch (Exception ignored) {
            }
            if (shortcutBadger != null && shortcutBadger.getSupportLaunchers().contains
                    (currentHomePackage)) {
                curBadger = shortcutBadger;
                break;
            }
        }
        if (curBadger == null) {
            String manufacturer = Build.MANUFACTURER.toLowerCase();
            if (manufacturer.equals("xiaomi")) {
                curBadger = new XiaomiHomeBadger();
            } else if (manufacturer.equals("zuk")) {
                curBadger = new ZukHomeBadger();
            } else if (manufacturer.equals("oppo")) {
                curBadger = new OPPOHomeBader();
            } else {
                curBadger = new DefaultBadger();
            }
        }
        return true;
    }

    // Avoid anybody to instantiate this class
    private BadgerManager() {

    }
}
