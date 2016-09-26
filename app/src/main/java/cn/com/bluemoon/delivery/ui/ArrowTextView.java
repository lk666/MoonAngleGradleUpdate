package cn.com.bluemoon.delivery.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;


/**
 * Created by Administrator on 2016/9/14.
 */
public class ArrowTextView extends TextView {

    public ArrowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArrowTextView);
        radius = typedArray.getDimension(R.styleable.ArrowTextView_arrowRadius, 0);
        arrowWidth = typedArray.getDimension(R.styleable.ArrowTextView_arrowWidth, 0);
        arrowInWidth = typedArray.getDimension(R.styleable.ArrowTextView_arrowInWidth, 0);
        color = typedArray.getColor(R.styleable.ArrowTextView_arrowBg, Color.WHITE);
    }


    public ArrowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    public ArrowTextView(Context context) {
        super(context);
    }

    private float radius;
    private float arrowWidth;
    /**
     * 三角形箭头在此宽度居中......
     */
    private float arrowInWidth;
    private int color;

    /**
     * @param arrowWidth 三角形箭头的宽度.......
     */
    public void setArrowWidth(float arrowWidth) {
        this.arrowWidth = arrowWidth;
        invalidate();

    }

    /**
     * @param arrowInWidth 三角形箭头在此高度居中......
     */
    public void setArrowInWidth(float arrowInWidth) {
        this.arrowInWidth = arrowInWidth;
        invalidate();
    }

    /**
     * @param radius 矩形四角圆角的半径..........
     */
    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();

    }

    /**
     * @param color 箭头矩形的背景色.........
     */
    public void setBgColor(int color) {
        this.color = color;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color == 0 ? Color.WHITE : color);
        paint.setAntiAlias(true);
        if (radius == 0) {
            radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
        }
        if (arrowWidth == 0) {
            arrowWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        }
        //带圆角的矩形(左边减去三角形的宽度...........)
        int top = (int) (getPaddingTop() - arrowWidth);
        int height = getHeight();
        int width = getWidth();
        canvas.drawRoundRect(new RectF(0, top, getWidth(), height), radius, radius, paint);
        if (arrowInWidth == 0) {
            arrowInWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        }
        width = (int) (width > arrowInWidth ? arrowInWidth : width);
        //画三角形
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        float yMiddle = height/2;
        float xMiddle = width/2;
        float xLeft = xMiddle-(arrowWidth/2);
        float xRight = xMiddle+(arrowWidth/2);
        float yTop=yMiddle-(arrowWidth/2);
        float yBottom=yMiddle+(arrowWidth/2);
        path.moveTo(xMiddle, 0);
        path.lineTo(xRight, top);
        path.lineTo(xLeft, top);
        path.lineTo(xMiddle, 0);
        path.close();
        canvas.drawPath(path, paint);
        // canvas.restore();
        // canvas.translate(left, 0);
        super.onDraw(canvas);

    }
}

