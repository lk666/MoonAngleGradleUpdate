package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by LIANGJIANGLI on 2016/6/25.
 */
public class ObservableScrollView extends ScrollView {

    private ScrollListener mListener;

    public static interface ScrollListener {
        public void scrollOritention(int oritention);
    }

    /**
     * ScrollView正在向上滑动
     */
    public static final int SCROLL_UP = 0x01;
    /**
     * ScrollView正在向下滑动
     */
    public static final int SCROLL_DOWN = 0x10;
    /**
     * 最小的滑动距离
     */
    private static final int SCROLLLIMIT = 3;

    private int lastScrollY = 0;

    public ObservableScrollView(Context context) {
        super(context, null);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mListener != null) {
            int scrollY = ObservableScrollView.this.getScrollY();
            mListener.scrollOritention(scrollY - lastScrollY);
            lastScrollY = scrollY;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                postScroll(5);
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 用于用户手指离开ScrollView的时候获取ScrollView滚动的Y距离，然后回调给onScroll方
     */
    private void postScroll(int intr) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int scrollY = ObservableScrollView.this.getScrollY();
                //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
                if (lastScrollY != scrollY) {
                    postScroll(20);
                }
                if (mListener != null) {
                    mListener.scrollOritention(scrollY - lastScrollY);
                    lastScrollY = scrollY;
                }
            }
        }, intr);
    }

   /* @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (oldt > t && oldt - t > SCROLLLIMIT) {
            if (mListener != null) mListener.scrollOritention(SCROLL_DOWN);
        } else if (oldt < t && t - oldt > SCROLLLIMIT) {
            if (mListener != null) mListener.scrollOritention(SCROLL_UP);
        }
    }*/

    public void setScrollListener(ScrollListener l) {
        this.mListener = l;
    }
}
