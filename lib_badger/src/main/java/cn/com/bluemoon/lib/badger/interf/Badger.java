package cn.com.bluemoon.lib.badger.interf;

import android.content.ComponentName;
import android.content.Context;

import java.util.List;

import cn.com.bluemoon.lib.badger.util.ShortcutBadgeException;
import cn.com.bluemoon.lib.badger.ShortcutBadger;

public interface Badger {

    /**
     * Called when user attempts to update notification count
     * @param context Caller context
     * @param componentName Component containing package and class name of calling application's
     *                      launcher activity
     * @param badgeCount Desired notification count
     * @throws ShortcutBadgeException
     */
    void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException;

    /**
     * Called to let {@link ShortcutBadger} knows which launchers are supported by this badger. It should return a
     * @return List containing supported launchers package names
     */
    List<String> getSupportLaunchers();
}
