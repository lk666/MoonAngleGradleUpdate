package cn.com.bluemoon.delivery.utils;

import android.animation.ObjectAnimator;
import android.view.View;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by bm on 2016/8/4.
 */
public class ViewUtil extends LibViewUtil {

    public static void longToast(String msg) {
        longToast(AppContext.getInstance(),msg);
    }

    public static void toast(String msg) {
        toast(AppContext.getInstance(), msg);
    }

    public static void longToast(int resId) {
        longToast(AppContext.getInstance(), resId);
    }

    public static void toast(int resId) {
        toast(AppContext.getInstance(), resId);
    }

    /**
     * 网络异常提示
     */
    public static void toastNoInternet() {
        toast(R.string.request_no_internet);
    }

    /**
     * 服务器繁忙提示
     */
    public static void toastBusy() {
        toast(R.string.request_server_busy);
    }

    /**
     * 服务器超时提示
     *
     */
    public static void toastOvertime() {
        toast(R.string.request_server_overtime);
    }

    /**
     * 获取数据错误提示
     */
    public static void toastErrorData() {
        toast(R.string.get_data_busy);
    }

    public static void showTextAmin(View view){
        ViewUtil.setViewVisibility(view, View.VISIBLE);
        ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f).setDuration(3000).start();
    }

    public static void showBtnAmin(View view){
        float translationX = view.getResources().getDimensionPixelOffset(R.dimen.translation_x);
        ObjectAnimator.ofFloat(view, "translationX", 0.0f, -translationX, translationX, 0.0f).setDuration(400).start();
    }

    /**
     * 提交按钮动画方法
     * @param btnView 提交按钮
     * @param txtView 错误提示控件
     */
    public static void showSubmitAmin(View btnView, View txtView){
        showBtnAmin(btnView);
        showTextAmin(txtView);
    }

}
