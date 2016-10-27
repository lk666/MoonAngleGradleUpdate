package cn.com.bluemoon.lib.badger.impl;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import cn.com.bluemoon.lib.badger.BadgeUtil;
import cn.com.bluemoon.lib.badger.interf.Badger;
import cn.com.bluemoon.lib.badger.util.BroadcastHelper;
import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;

/**
 * @author leolin
 */
public class XiaomiHomeBadger implements Badger {

    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra" +
            ".update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra" +
            ".update_application_message_text";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount,Notification notification) throws

            ShortcutBadgeException {
        try {
            //miui 6及以上版本
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, badgeCount);
        } catch (Exception e) {
            //miui 6之前版本
            Intent localIntent = new Intent(INTENT_ACTION);
            localIntent.putExtra(EXTRA_UPDATE_APP_COMPONENT_NAME, componentName.getPackageName()
                    + "/" + componentName.getClassName());
            localIntent.putExtra(EXTRA_UPDATE_APP_MSG_TEXT, String.valueOf(badgeCount == 0 ? "" :
                    badgeCount));
            if (BroadcastHelper.canResolveBroadcast(context, localIntent)) {
                context.sendBroadcast(localIntent);
            } else {
                throw new ShortcutBadgeException("unable to resolve intent: " + localIntent
                        .toString());
            }
        }

        BadgeUtil.showNotification(context, notification,0);

    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.miui.miuilite",
                "com.miui.home",
                "com.miui.miuihome",
                "com.miui.miuihome2",
                "com.miui.mihome",
                "com.miui.mihome2"
        );
    }
}
