package cn.com.bluemoon.delivery.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

/**
 * Created by LIANGJIANGLI on 2016/6/25.
 */
public class AnimationUtils {

    private static int durationMillis = 500;

    public static int getTargetHeight(View v) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class,
                    int.class);
            m.setAccessible(true);
            m.invoke(v, View.MeasureSpec.makeMeasureSpec(
                    ((View) v.getParent()).getMeasuredWidth(),
                    View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
        } catch (Exception e) {

        }
        return v.getMeasuredHeight();
    }

    public static void applyTransformation(final View v, final int height, final boolean isCheck) {
        Animation animation = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                if (isCheck) {
                    lp.height = (int) ( height * interpolatedTime);
                } else {
                    lp.height = (int) (height * (1-interpolatedTime));
                }
                v.setLayoutParams(lp);
            }
        };
        animation.setDuration(durationMillis);
        v.startAnimation(animation);
    }
}
