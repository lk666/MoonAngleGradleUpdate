package cn.com.bluemoon.delivery.ui.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by tangqiwei on 2017/5/25.
 */

public class BmMarkView extends FrameLayout {
    /**
     * 宽度变宽的倍数
     */
    private final static double  MULTIPLE_ONE_POINT_FIVE = 1.5, MULTIPLE_TWO_POINT_ZERO = 2.0;

    private TextView markView;

    private int number;

    private int color;

    private int defaultHeight;

    private String txtNumber;

    private final static String APOSTROPHE="...";

    private final static String PLUS_SIGN="99+";


    private final static int  MODE_SHOW_APOSTROPHE=0;//超过99用"..."
    private final static int  MODE_SHOW_PLUS_SIGN=1;//超过99用"99+"
    private int showMode;

    public BmMarkView(Context context) {
        super(context);
        number = 0;
        color = -1;
        defaultHeight = dip2px(12);
        showMode=MODE_SHOW_PLUS_SIGN;
        init();
    }

    public BmMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getInitData(attrs);
        init();
    }

    public BmMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getInitData(attrs);
        init();
    }


    private void getInitData(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BmMarkView);
            number = typedArray.getInteger(R.styleable.BmMarkView_txt_number, 0);
            color = typedArray.getColor(R.styleable.BmMarkView_color_bg, -1);
            defaultHeight = typedArray.getDimensionPixelSize(R.styleable.BmMarkView_mark_height, dip2px(12));
            showMode=typedArray.getInt(R.styleable.BmMarkView_mark_show_mode,MODE_SHOW_PLUS_SIGN);
            typedArray.recycle();
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_mark, this, true);
        markView = (TextView) findViewById(R.id.txt_mark);
        setGradientDrawable(markView,defaultHeight,color);
        setMarkViewWidthAndText(number);
    }

    /**
     * @param number
     */
    public int setMarkViewWidthAndText(int number) {
        ViewGroup.LayoutParams params = markView.getLayoutParams();
        params.height = defaultHeight;
        int height = params.height;
        if (number <= 0) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
            if (number < 10) {
                params.width = height;
                txtNumber=String.valueOf(number);
            } else if (number < 100) {
                params.width = (int) (height * MULTIPLE_ONE_POINT_FIVE);
                txtNumber=String.valueOf(number);
            } else {
                switch (showMode) {
                    case MODE_SHOW_APOSTROPHE:
                        params.width = (int) (height * MULTIPLE_ONE_POINT_FIVE);
                        txtNumber=APOSTROPHE;
                        break;
                    case MODE_SHOW_PLUS_SIGN:
                        params.width = (int) (height * MULTIPLE_TWO_POINT_ZERO);
                        txtNumber=PLUS_SIGN;
                        break;
                }
            }
        }
        markView.setText(txtNumber);
        markView.setLayoutParams(params);
        return params.width;
    }

    /**
     * 高度改变时调用
     * @param markView
     * @param height
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setGradientDrawable(TextView markView,int height,int colorBg) {
        GradientDrawable grad = null;
        try {
            grad = (GradientDrawable) markView.getBackground();
        } catch (Exception e) {
            try {
                ColorDrawable color = (ColorDrawable) markView.getBackground();
                grad = new GradientDrawable();
                grad.setColor(color.getColor());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (grad == null) {
            grad = new GradientDrawable();
            grad.setColor(0xfffc5500);
        }
        grad.setShape(GradientDrawable.RECTANGLE);
        grad.setCornerRadius((float) height / (float) 2);
        if(colorBg!=-1)
            grad.setColor(colorBg);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            markView.setBackgroundDrawable(grad);
        } else {
            markView.setBackground(grad);
        }
    }

    /**
     * dp  转 px
     *
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
