package cn.com.bluemoon.delivery.module.offline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import cn.com.bluemoon.delivery.AppContext;

/**
 * 排课详情的包心圆
 * Created by tangqiwei on 2018/8/16.
 */

public class WrapDot extends View {

    private int fillColor = Color.parseColor("#1fb8ff");
    private int borderColor = Color.parseColor("#ffffff");
    private float cx = dp2px(5.5f);
    private float cy = dp2px(5.5f);
    private float fillR = dp2px(3.5f);
    private float borderR = dp2px(5.5f);
    private float strokeWidth = dp2px(2f);
    private Paint paint;


    public WrapDot(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(fillColor);
        canvas.drawCircle(cx, cy, fillR, paint);
        paint.setColor(borderColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(cx, cy, borderR, paint);
    }

    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                AppContext.getInstance().getResources().getDisplayMetrics());
    }
}
