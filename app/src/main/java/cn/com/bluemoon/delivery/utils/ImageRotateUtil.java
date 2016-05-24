package cn.com.bluemoon.delivery.utils;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by liangjiangli on 2016/4/1.
 */
public class ImageRotateUtil {

    public static void rotateArrow(boolean tag, ImageView imgDown) {
        float pivotX = imgDown.getWidth() / 2f;
        float pivotY = imgDown.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (tag) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        imgDown.startAnimation(animation);
    }
}
