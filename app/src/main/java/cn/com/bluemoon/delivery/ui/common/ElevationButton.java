package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by bm on 2017/6/8.
 */

public class ElevationButton extends Button {
    public ElevationButton(Context context) {
        super(context);
    }

    public ElevationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(elevation);
        }
    }

    @Override
    public void setTranslationZ(float translationZ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setTranslationZ(translationZ);
        }
    }

    @Override
    public float getElevation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getElevation();
        }
        return 0;
    }

    @Override
    public float getTranslationZ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getTranslationZ();
        }
        return 0;
    }
}
