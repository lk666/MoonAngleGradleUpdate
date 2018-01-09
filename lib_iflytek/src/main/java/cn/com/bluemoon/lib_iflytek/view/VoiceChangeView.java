package cn.com.bluemoon.lib_iflytek.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.com.bluemoon.lib_iflytek.utils.SpeechUtil;

/**
 * Created by bm on 2017/8/31.
 */

public class VoiceChangeView extends View {

    private Paint paint;
    private int voiceLevel = 1;
    private static final int maxLevel = 8;

    private int rectRadius = 1;
    private int rectHeight = 10;
    private int rectWidth = 4;
    private int rectSpace = 6;

    public VoiceChangeView(Context context) {
        super(context);
        init();
    }

    public VoiceChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setVoiceLevel(int level) {
        if (level < 1) {
            level = 1;
        } else if (level > maxLevel) {
            level = maxLevel;
        }
        if (voiceLevel != level) {
            this.voiceLevel = level;
            invalidate();
        }
    }

    public void setValue(int rectHeight, int rectWidth, int rectRadius, int rectSpace) {
        this.rectSpace = rectSpace;
        this.rectHeight = rectHeight;
        this.rectWidth = rectWidth;
        this.rectRadius = rectRadius;
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xffd8d8d8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < voiceLevel; i++) {
            int left = dp2px(i * (rectWidth + rectSpace));
            int top = 0;
            int right = dp2px((i + 1) * rectWidth + i * rectSpace);
            int bottom = dp2px(rectHeight);
            int radius = dp2px(rectRadius);
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, radius, radius, paint);
        }
    }

    private int dp2px(int dp) {
        return SpeechUtil.dip2px(getContext(), dp);
    }

    public int getVoiceLevel() {
        return voiceLevel;
    }

}
