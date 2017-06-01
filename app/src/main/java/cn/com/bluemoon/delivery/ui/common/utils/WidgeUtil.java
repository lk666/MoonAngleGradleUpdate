package cn.com.bluemoon.delivery.ui.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

/**
 * Created by bm on 2017/5/31.
 */

public class WidgeUtil {

    /**
     * dp转px
     *
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    //获取颜色选择器
    public static ColorStateList createPressedColorList(int normal, int pressed, int disable) {
        int[] colors = new int[]{normal, pressed, disable};
        int[][] states = new int[3][];
        states[0] = new int[]{-android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[2] = new int[]{-android.R.attr.state_enabled};
        return new ColorStateList(states, colors);
    }


    //获取颜色选择器
    public static ColorStateList createSelectColorList(int normal, int select, int disable) {
        int[] colors = new int[]{normal, select, disable};
        int[][] states = new int[3][];
        states[0] = new int[]{-android.R.attr.state_selected, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_selected, android.R.attr.state_enabled};
        states[2] = new int[]{-android.R.attr.state_enabled};
        return new ColorStateList(states, colors);
    }

    //获取图片资源选择器
    public static StateListDrawable createSelectListDrawable(Drawable dNormal, Drawable dSelect,
                                                             Drawable dDisable) {
        StateListDrawable stateList = new StateListDrawable();
        stateList.addState(new int[]{-android.R.attr.state_selected, android.R.attr
                .state_enabled}, dNormal);
        stateList.addState(new int[]{android.R.attr.state_selected, android.R.attr
                .state_enabled}, dSelect);
        stateList.addState(new int[]{-android.R.attr.state_enabled}, dDisable);
        return stateList;
    }

    //获取图片资源选择器
    public static StateListDrawable createSelectListDrawable(Context context, int idNormal, int
            idSelect, int idDisable) {
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable select = idSelect == -1 ? null : context.getResources().getDrawable(idSelect);
        Drawable disable = idDisable == -1 ? null : context.getResources().getDrawable
                (idDisable);
        return createSelectListDrawable(normal, select, disable);
    }

    //获取图片资源选择器
    public static StateListDrawable createPressedListDrawable(Drawable dNormal, Drawable dSelect,
                                                              Drawable dDisable) {
        StateListDrawable stateList = new StateListDrawable();
        stateList.addState(new int[]{-android.R.attr.state_pressed, android.R.attr
                .state_enabled}, dNormal);
        stateList.addState(new int[]{android.R.attr.state_pressed, android.R.attr
                .state_enabled}, dSelect);
        stateList.addState(new int[]{-android.R.attr.state_enabled}, dDisable);
        return stateList;
    }

    //获取图片资源选择器
    public static StateListDrawable createPressedListDrawable(Context context, int idNormal, int
            idPressed, int idDisable) {
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable disable = idDisable == -1 ? null : context.getResources().getDrawable
                (idDisable);
        return createPressedListDrawable(normal, pressed, disable);
    }

    //获取图片资源选择器
    public static StateListDrawable createPressedListDrawable(int colorNormal, int colorPressed, int
            colorDisable) {
        ColorDrawable normal = colorNormal == 0 ? null : new ColorDrawable(colorNormal);
        ColorDrawable pressed = colorPressed == 0 ? null : new ColorDrawable(colorPressed);
        ColorDrawable disable = colorDisable == 0 ? null : new ColorDrawable(colorDisable);
        return createPressedListDrawable(normal, pressed, disable);
    }

    /*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static GradientDrawable createShapeStateDrawable(int colorNormal, int colorPressed, int
            colorDisable, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(createPressedColorList(colorNormal,colorPressed,colorDisable));
        return drawable;
    }*/

    public static StateListDrawable createShapeStateDrawable(int colorNormal, int colorPressed, int
            colorDisable, int radius) {
        GradientDrawable normal = new GradientDrawable();
        normal.setShape(GradientDrawable.RECTANGLE);
        normal.setCornerRadius(radius);
        normal.setColor(colorNormal);
        GradientDrawable pressed = new GradientDrawable();
        pressed.setShape(GradientDrawable.RECTANGLE);
        pressed.setCornerRadius(radius);
        pressed.setColor(colorPressed);
        GradientDrawable disable = new GradientDrawable();
        disable.setShape(GradientDrawable.RECTANGLE);
        disable.setCornerRadius(radius);
        disable.setColor(colorDisable);
        return createPressedListDrawable(normal,pressed,disable);
    }
}
