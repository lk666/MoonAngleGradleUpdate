package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import cn.com.bluemoon.delivery.R;

/**
 * 上半边是圆弧形的内容控件
 */
public class TopArcLayout extends RelativeLayout {

    private Paint borderPaint;
    private Paint fillPaint;

    /**
     * 圆弧形渐变区高度
     */
    private int strokeThiness;
    /**
     * 圆弧形半径
     */
    private int radius;
    /**
     * 圆弧形渐变区顶部颜色，即渐变结束色
     */
    private int endColor;
    /**
     * 圆弧形渐变区底部颜色，即渐变开始色
     */
    private int startColor;

    /**
     * 背景色
     */
    private int bg;

    public TopArcLayout(Context context) {
        super(context);
    }

    public TopArcLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public TopArcLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    /**
     * 初始化，获取设置的属性
     */
    protected void initAttr(Context context, AttributeSet attrs) {
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.TopArcLayout);
        radius = attribute.getDimensionPixelSize(R.styleable.TopArcLayout_radius, 2000);
        strokeThiness = attribute.getDimensionPixelSize(R.styleable
                .TopArcLayout_strokeThiness, 10);

        startColor = attribute.getColor(R.styleable.TopArcLayout_startColor, Color.WHITE);
        endColor = attribute.getColor(R.styleable.TopArcLayout_endColor, Color.TRANSPARENT);
        bg = attribute.getColor(R.styleable.TopArcLayout_viewBg, Color.WHITE);
        attribute.recycle();
        setBackgroundColor(Color.TRANSPARENT);
    }

    float rate;

    /**
     * 初始化画笔
     */
    private void initPaint() {
        if (borderPaint != null) {
            return;
        }

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setStrokeWidth(0);

        float halfWidth = getMeasuredWidth() / 2.0f;
        rate = (float) (radius - strokeThiness) / (float) radius;

        RadialGradient border = new RadialGradient(halfWidth, radius, radius,
                new int[]{startColor, startColor, endColor}, new float[]{0f, rate, 1.0f},
                Shader.TileMode.CLAMP);
        borderPaint.setShader(border);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setStrokeWidth(0);
        fillPaint.setColor(bg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();

        float halfWidth = getMeasuredWidth() / 2.0f;
        canvas.drawCircle(halfWidth, radius, radius, borderPaint);
        canvas.drawCircle(halfWidth, radius, radius * rate, fillPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetPaint();
    }

    private void resetPaint() {
        borderPaint = null;
        fillPaint = null;
    }
}
