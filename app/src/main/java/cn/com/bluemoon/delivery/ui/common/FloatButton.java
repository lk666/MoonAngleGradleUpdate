package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.common.utils.WidgeUtil;

/**
 * Created by bm on 2017/6/6.
 */

public class FloatButton extends Button implements View.OnTouchListener {

    private final static float z = 3.0f;
    private final static float zDown = 2.0f;

    public FloatButton(Context context) {
        super(context);
        init();
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.btn_oval_red);
        setGravity(Gravity.CENTER);
        setTextSize(12);
        setTextColor(0xffffffff);
        setElevation(dip2px(z));
        setOnTouchListener(this);
    }

    private int dip2px(float dp) {
        return WidgeUtil.dip2px(getContext(), dp);
    }

    @Override
    public void setElevation(float elevation) {
        if (!isEnabled()) return;
        super.setElevation(elevation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setElevation(WidgeUtil.dip2px(getContext(), zDown));
                break;
            case MotionEvent.ACTION_UP:
                setElevation(WidgeUtil.dip2px(getContext(), z));
                break;
        }
        return false;
    }
}
