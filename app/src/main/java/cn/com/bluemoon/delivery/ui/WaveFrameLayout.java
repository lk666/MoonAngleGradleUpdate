package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

import cn.com.bluemoon.delivery.R;


/**
 * 涟漪效果控件
 * Created by bm on 2017/11/14.
 */

public class WaveFrameLayout extends FrameLayout {
    public WaveFrameLayout(Context context) {
        super(context);
        init(null);
    }

    public WaveFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(attrs==null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.setBackgroundResource(R.drawable.btn_white);
        }else{
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            int[] attribute = new int[]{android.R.attr.selectableItemBackground};
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
            setForeground(typedArray.getDrawable(0));
            typedArray.recycle();
        }
    }

}
