package cn.com.bluemoon.delivery.utils;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by bm on 2016/8/4.
 */
public class ViewUtil extends LibViewUtil {

    public static void longToast(String msg) {
        longToast(AppContext.getInstance(),msg);
    }

    public static void toast(String msg) {
        toast(AppContext.getInstance(),msg);
    }

    public static void longToast(int resId) {
        longToast(AppContext.getInstance(), resId);
    }

    public static void toast(int resId) {
        toast(AppContext.getInstance(), resId);
    }

}
