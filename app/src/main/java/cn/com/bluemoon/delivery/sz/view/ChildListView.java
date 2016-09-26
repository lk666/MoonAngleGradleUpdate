package cn.com.bluemoon.delivery.sz.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Wan.N
 * Date       2016/9/7
 * Desc       ${TODO}
 */
public class ChildListView extends ListView {
    ///**是否可滑动，默认不可滑动*/
    boolean isCanScroll = false;

    public ChildListView(Context context) {
        super(context);
        updateViewState();
    }

    public ChildListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateViewState();
    }

    private void updateViewState() {
        setClickable(isCanScroll);
        setPressed(isCanScroll);
        setFocusable(isCanScroll);
        setEnabled(isCanScroll);
    }

    public void setScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
        updateViewState();
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isCanScroll) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isCanScroll) {
            return false;
        }
        return super.onTouchEvent(ev);
    }
}
