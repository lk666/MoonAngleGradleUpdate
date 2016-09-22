package cn.com.bluemoon.delivery.ui.selectordialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import cn.com.bluemoon.delivery.R;

/**
 * 选中时显示特定后缀的数字WheelView滚轮
 *
 * @author luokai
 */
public class ExtraTextNumberWheelView extends NumberWheelView {

    /**
     * 选中时显示的特定后缀
     */
    private String extraText;

    public ExtraTextNumberWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ExtraTextNumberWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtraTextNumberWheelView(Context context) {
        super(context);
    }

    /**
     * 初始化，获取设置的属性
     */
    @Override
    protected void initAttr(Context context, AttributeSet attrs) {
        super.initAttr(context, attrs);

        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.SimpleWheelView);
        extraText = attribute.getString(R.styleable.SimpleWheelView_extra);
        attribute.recycle();
    }

    @Override
    protected String getItemText(int index, boolean isSelected) {
        String text = super.getItemText(index, isSelected);
        if (isSelected) {
            text += extraText;
        }
        return text;
    }
}
