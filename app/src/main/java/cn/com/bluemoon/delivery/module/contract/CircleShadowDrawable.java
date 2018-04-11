package cn.com.bluemoon.delivery.module.contract;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.ViewUtil;

public class CircleShadowDrawable extends Drawable {
    private Paint paint;
    private int w;
    private int padding;

    public CircleShadowDrawable() {
        paint = new Paint();
        paint.setColor(AppContext.getInstance().getResources().getColor(R.color.orange_ff6c47));
        paint.setAntiAlias(true);
        padding = ViewUtil.dp2px(2);
        paint.setShadowLayer(ViewUtil.dp2px(2), 0, padding, AppContext.getInstance().getResources()
                .getColor(R.color.transparent_40));
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        // todo没考虑宽度不够2*padding的情况
        w = right - left;
        int h = bottom - top;
        int detal = (w - h) / 2;
        if (w > h) {
            left += detal;
            right -= detal;
            w = h;
        } else if (w < h) {
            top -= detal;
            bottom += detal;
        }
        w = w - padding - padding;
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float r = w / 2;
        canvas.drawCircle(r + padding, r, r, paint);
    }

    // getIntrinsicWidth、getIntrinsicHeight主要是为了在View使用wrap_content的时候，提供一下尺寸，默认为-1可不是我们希望的
    @Override
    public int getIntrinsicHeight() {
        return w;
    }

    @Override
    public int getIntrinsicWidth() {
        return w;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
