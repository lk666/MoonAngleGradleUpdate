package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by LIANGJIANGLI on 2016/6/28.
 */
public class GridViewInScrollview extends GridView{
    public GridViewInScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(false);
    }

    public GridViewInScrollview(Context context) {
        super(context);
        this.setFocusable(false);
    }

    public GridViewInScrollview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setFocusable(false);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
