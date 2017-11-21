package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.com.bluemoon.lib_widget.utils.WidgeUtil;

/**
 * 带阴影分割线
 * Created by bm on 2017/9/28.
 */

public class ShadowLineView extends View {

    Paint paint;
    protected int pColor = 0xffffffff;
    protected int bColor = 0x33000000;
    protected int blur = dp2px(6);
    protected int dx = 0;
    protected int dy = dp2px(-2);
    protected int height = dp2px(10);

    public ShadowLineView(Context context) {
        super(context);
        init();
    }

    public ShadowLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(pColor);
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        paint.setShadowLayer(blur,dx,dy,bColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,height,getWidth(),getHeight(),paint);
    }

    private int dp2px(float dp){
        return WidgeUtil.dip2px(getContext(),dp);
    }
}
