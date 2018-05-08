package cn.com.bluemoon.delivery.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
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

    /**
     * 获得状态栏的高度?
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object)
                    .toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static void initTop(Activity aty) {
        initTop(aty,null,false);
    }

    /**
     * 设置沉浸式
     *
     * @param aty
     * @param topHead
     * @param isFixHeight tophead是否固定高度（重要）
     */
    public static void initTop(Activity aty, View topHead, boolean isFixHeight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = aty.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(layoutParams);
            setTopHead(topHead, isFixHeight);
        }
    }

    /**
     * 留出状态栏高度
     *
     * @param topHead
     * @param isFixHeight tophead是否固定高度（重要）
     */
    public static void setTopHead(View topHead, boolean isFixHeight) {
        if (topHead != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = 0;
            int resourceId = topHead.getContext().getResources().getIdentifier("status_bar_height" +
                    "", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = topHead.getContext().getResources().getDimensionPixelSize
                        (resourceId);
            }
            if (isFixHeight) {
                ViewGroup.LayoutParams lp = topHead.getLayoutParams();
                lp.height = lp.height + statusBarHeight;
                topHead.setLayoutParams(lp);
            }
            topHead.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                AppContext.getInstance().getResources().getDisplayMetrics());
    }

}
