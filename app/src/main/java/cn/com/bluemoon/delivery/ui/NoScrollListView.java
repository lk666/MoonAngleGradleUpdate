package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决ListView在SrollView里面显示不全: </br>
 * 解决嵌套在listview里，ListView还有滑动的效果，去掉滑动效果
 */
public class NoScrollListView extends ListView {
    private boolean mIsScrollable = false;

    public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollListView(Context context) {
        super(context);
    }

    public void setScrollaable(boolean isScrollable) {
        this.mIsScrollable = isScrollable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsScrollable) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

    }
}
