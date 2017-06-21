package cn.com.bluemoon.delivery.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by bm on 2016/8/4.
 */
public class ViewUtil extends LibViewUtil {

    public static void longToast(String msg) {
        longToast(AppContext.getInstance(), msg);
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

    public static void showTextAmin(View view) {
        ViewUtil.setViewVisibility(view, View.VISIBLE);
        ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f).setDuration(3000).start();
    }

    public static void showBtnAmin(View view) {
        float translationX = view.getResources().getDimensionPixelOffset(R.dimen.translation_x);
        ObjectAnimator.ofFloat(view, "translationX", 0.0f, -translationX, translationX, 0.0f)
                .setDuration(400).start();
    }

    /**
     * 提交按钮动画方法
     *
     * @param btnView 提交按钮
     * @param txtView 错误提示控件
     */
    public static void showSubmitAmin(View btnView, View txtView) {
        showBtnAmin(btnView);
        showTextAmin(txtView);
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 跳转页面
     *
     * @param aty
     * @param cls
     */
    public static void showActivity(Context aty, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param aty
     * @param cls
     */
    public static void showActivityForResult(Activity aty, Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(aty, cls);
        aty.startActivityForResult(intent, requestCode);
    }

    public static void showErrorMsg(ResultBase resultBase) {
        String msg = Constants.ERROR_MAP.get(resultBase.getResponseCode());
        if (TextUtils.isEmpty(msg)) {
            ViewUtil.toast(resultBase.getResponseMsg());
        } else {
            ViewUtil.toast(msg);
        }
    }


}
