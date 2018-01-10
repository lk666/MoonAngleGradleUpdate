package cn.com.bluemoon.lib_iflytek.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.com.bluemoon.lib_iflytek.R;
import cn.com.bluemoon.lib_iflytek.utils.SpeechUtil;

/**
 * 声音解析动画
 * Created by bm on 2017/8/31.
 */

public class VoiceDecodeView extends View {

    //每次变化的距离
    private int diff;
    //每个多长时间变化一次
    private int delay;
    private Paint paint;
    //长方形的条数
    private int voiceSize;

    private int minHeight;
    private int maxHeight;
    //圆角
    private int rectRadius;
    //宽度
    private int rectWidth;
    //间距
    private int rectSpace;
    //颜色
    private int rectColor;
    //每个长方形的高度
    private int[] heights;
    //每个长方形的变化值
    private int[] diffs;
    //是否开启动画
    private boolean isStart;

    public VoiceDecodeView(Context context) {
        super(context);
        init(null);
    }

    public VoiceDecodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置控件宽高
        int width = rectWidth * voiceSize + rectSpace * (voiceSize - 1);
        setMeasuredDimension(width,maxHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < voiceSize; i++) {
            int left = i * (rectWidth + rectSpace);
            int top = (maxHeight - heights[i]) / 2;
            int right = (i + 1) * rectWidth + i * rectSpace;
            int bottom = maxHeight - top;
            int radius = rectRadius;
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, radius, radius, paint);
        }
        if (isStart) {
            reDrawHeight();
            postInvalidateDelayed(delay);
        }

    }

    private void init(AttributeSet attrs) {
        initValue();
        initAttrs(attrs);
        heights = new int[voiceSize];
        diffs = new int[voiceSize];
        initHeight();
        paint = new Paint();
        paint.setColor(rectColor);
    }

    private void initValue(){
        diff = dp2px(5);
        delay = 100;
        voiceSize = 5;
        minHeight = dp2px(20);
        maxHeight = dp2px(40);
        rectRadius = dp2px(3);
        rectWidth = dp2px(6);
        rectSpace = dp2px(10);
        rectColor = 0xffd8d8d8;
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .VoiceDecodeView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            if (attr == R.styleable.VoiceDecodeView_vd_width) {
                rectWidth = attribute.getDimensionPixelSize(attr, rectWidth);
            }else if (attr == R.styleable.VoiceDecodeView_vd_min_height) {
                minHeight = attribute.getDimensionPixelSize(attr, minHeight);
            }else if (attr == R.styleable.VoiceDecodeView_vd_max_height) {
                maxHeight = attribute.getDimensionPixelSize(attr, maxHeight);
            }else if (attr == R.styleable.VoiceDecodeView_vd_radius) {
                rectRadius = attribute.getDimensionPixelSize(attr, rectRadius);
            }else if (attr == R.styleable.VoiceDecodeView_vd_color) {
                rectColor = attribute.getColor(attr, rectColor);
            }else if (attr == R.styleable.VoiceDecodeView_vd_space) {
                rectSpace = attribute.getDimensionPixelSize(attr, rectSpace);
            }else if (attr == R.styleable.VoiceDecodeView_vd_size) {
                voiceSize = attribute.getInt(attr, voiceSize);
            }else if (attr == R.styleable.VoiceDecodeView_vd_diff) {
                diff = attribute.getDimensionPixelSize(attr, diff);
            }else if (attr == R.styleable.VoiceDecodeView_vd_delay) {
                delay = attribute.getInt(attr, delay);
            }
        }
        attribute.recycle();
    }

    private void initHeight() {
        //初始化各长方形的高度和变化值
        int dif = (maxHeight - minHeight) / (voiceSize / 2);
        for (int i = 0; i < voiceSize; i++) {
            int height = maxHeight - Math.abs(i - voiceSize / 2) * dif;
            setHeight(i, height);
            diffs[i] = diff;
        }
    }

    /**
     * 重绘高度，动画
     */
    private void reDrawHeight() {
        for (int i = 0; i < voiceSize; i++) {
            setHeight(i, heights[i] + diffs[i]);
        }
    }

    /**
     * 设置某个长方形高度
     */
    private void setHeight(int i, int value) {
        if (value <= minHeight) {
            value = minHeight;
            diffs[i] = diff;
        }
        if (value >= maxHeight) {
            value = maxHeight;
            diffs[i] = -diff;
        }
        heights[i] = value;
    }

    private int dp2px(int dp) {
        return SpeechUtil.dip2px(getContext(), dp);
    }

    public int getVoiceSize() {
        return voiceSize;
    }

    public void setAnim(boolean isStart) {
        this.isStart = isStart;
        if (isStart) {
            invalidate();
        }
    }

}
