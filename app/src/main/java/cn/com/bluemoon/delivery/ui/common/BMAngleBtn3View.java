package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by bm on 2017/6/1.
 */

public class BMAngleBtn3View extends BMAngleBtn1View {
    public BMAngleBtn3View(Context context) {
        super(context);
    }

    public BMAngleBtn3View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initColor() {

        textColor = 0xFFFFFFFF;
        textSize = sp2px(17);

        height = dip2px(48);
    }
}
