package cn.com.bluemoon.delivery.utils;

import android.animation.TimeInterpolator;

/**
 *
 * 一个开始很慢然后不断加速的插值器。
 * <hr/>
 * An interpolator where the rate of change starts out slowly and
 * and then accelerates.
 *
 */
public class AccelerateInterpolator implements TimeInterpolator {
    private final float mFactor;
    private final double mDoubleFactor;

    public AccelerateInterpolator() {
        mFactor = 1.0f;
        mDoubleFactor = 2.0;
    }


    @Override
    public float getInterpolation(float input) {
        if (mFactor == 1.0f) {
            return input * input;
        } else {
            return (float)Math.pow(input, mDoubleFactor);
        }
    }
}
