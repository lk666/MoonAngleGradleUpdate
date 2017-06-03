package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by bm on 2017/6/1.
 */

public class BMAngleBtn2View extends BMAngleBtn1View {
    public BMAngleBtn2View(Context context) {
        super(context);
    }

    public BMAngleBtn2View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initColor() {

        textColor = 0xFFFFFFFF;
        textSize = sp2px(14);

        paddingLeft = dip2px(8);
        paddingRight = dip2px(8);
        paddingTop = dip2px(0);
        paddingBottom = dip2px(0);

        marginLeft = dip2px(10);
        marginRight = dip2px(10);
        marginTop = dip2px(10);
        marginBottom = dip2px(10);

        translationZ = dip2px(3);

        radius = dip2px(4);

        height = dip2px(48);
    }
}
