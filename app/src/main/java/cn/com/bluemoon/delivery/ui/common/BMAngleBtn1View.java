package cn.com.bluemoon.delivery.ui.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.common.utils.WidgeUtil;

/**
 * Created by bm on 2017/5/31.
 * <p>
 * 背景色，字体颜色，字体大小，高度必须在自定义属性中配置
 */

public class BMAngleBtn1View extends FrameLayout implements View.OnTouchListener {

    private Button btn;

    protected int colorNormal = 0;
    protected int colorPressed = 0;
    protected int colorDisable = 0;

    protected int paddingLeft;
    protected int paddingRight;
    protected int paddingTop;
    protected int paddingBottom;

    protected int marginLeft;
    protected int marginRight;
    protected int marginTop;
    protected int marginBottom;

    protected int textColor = 0;
    protected int textSize;

    protected int radius;

    protected float translationZ = -1;

    protected float translationZDown = -1;

    protected int height = -1;

    protected int type = -1;


    public BMAngleBtn1View(Context context) {
        super(context);
        init(null);
    }

    public BMAngleBtn1View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setClickable(true);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_angle_btn, this, true);
        btn = (Button) findViewById(R.id.btn_todo);
        btn.setClickable(false);
        setOnTouchListener(this);

        //初始化默认值
        initColor();

        //接收自定义参数
        initAttrs(attrs);

        setValue();

    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        initColor();
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMAngleBtn1View);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            switch (attr) {
                case R.styleable.BMAngleBtn1View_btn_color_normal:
                    int colorNormal = attribute.getColor(attr, 0);
                    if (colorNormal != 0) {
                        this.colorNormal = colorNormal;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_color_pressed:
                    int colorPressed = attribute.getColor(attr, 0);
                    if (colorPressed != 0) {
                        this.colorPressed = colorPressed;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_color_disable:
                    int colorDisable = attribute.getColor(attr, 0);
                    if (colorDisable != 0) {
                        this.colorDisable = colorDisable;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_radius:
                    int radius = attribute.getDimensionPixelSize(attr, -1);
                    if (radius != -1) {
                        this.radius = radius;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_translationZ:
                    int shadowZ = attribute.getDimensionPixelSize(attr, -1);
                    if (shadowZ != -1) {
                        this.translationZ = shadowZ;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_height:
                    int height = attribute.getDimensionPixelSize(attr, -1);
                    if (height != -1) {
                        this.height = height;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_text:
                    setText(attribute.getText(attr));
                    break;
                case R.styleable.BMAngleBtn1View_btn_text_color:
                    int textColor = attribute.getColor(attr, 0);
                    if (textColor != 0) {
                        this.textColor = textColor;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_text_size:
                    int textSize = attribute.getDimensionPixelSize(attr, -1);
                    if (textSize != -1) {
                        this.textSize = textSize;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_marginBottom:
                    int marginBottom = attribute.getDimensionPixelSize(attr, -1);
                    if (marginBottom != -1) {
                        this.marginBottom = marginBottom;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_marginTop:
                    int marginTop = attribute.getDimensionPixelSize(attr, -1);
                    if (marginTop != -1) {
                        this.marginTop = marginTop;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_marginLeft:
                    int marginLeft = attribute.getDimensionPixelSize(attr, -1);
                    if (marginLeft != -1) {
                        this.marginLeft = marginLeft;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_marginRight:
                    int marginRight = attribute.getDimensionPixelSize(attr, -1);
                    if (marginRight != -1) {
                        this.marginRight = marginRight;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_paddingBottom:
                    int paddingBottom = attribute.getDimensionPixelSize(attr, -1);
                    if (paddingBottom != -1) {
                        this.paddingBottom = paddingBottom;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_paddingTop:
                    int paddingTop = attribute.getDimensionPixelSize(attr, -1);
                    if (paddingTop != -1) {
                        this.paddingTop = paddingTop;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_paddingLeft:
                    int paddingLeft = attribute.getDimensionPixelSize(attr, -1);
                    if (paddingLeft != -1) {
                        this.paddingLeft = paddingLeft;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_paddingRight:
                    int paddingRight = attribute.getDimensionPixelSize(attr, -1);
                    if (paddingRight != -1) {
                        this.paddingRight = paddingRight;
                    }
                    break;
                case R.styleable.BMAngleBtn1View_btn_type:
                    type = attribute.getInt(attr, -1);
                    break;
                case R.styleable.BMAngleBtn1View_btn_enable:
                    setEnabled(attribute.getBoolean(attr, false));
                    break;
            }
        }
        attribute.recycle();
    }

    //默认类型是红色
    private void setType(int type) {
        if (type == 0) {
            colorNormal = 0xFFFF6C47;
            colorPressed = 0xFFE56140;
            colorDisable = 0xFFD7D7D7;
        } else if (type == 1) {
            colorNormal = 0xFF1FB8FF;
            colorPressed = 0xFF1CA6E5;
            colorDisable = 0xFFD7D7D7;
        } else {
            colorNormal = colorNormal == 0 ? 0xFFFF6C47 : colorNormal;
            colorPressed = colorPressed == 0 ? 0xFFE56140 : colorPressed;
            colorDisable = colorDisable == 0 ? 0xFFD7D7D7 : colorDisable;
        }
    }

    //初始化按钮颜色值（可重写）
    protected void initColor() {

        textColor = 0xFFFFFFFF;
        textSize = sp2px(17);

        paddingLeft = dip2px(0);
        paddingRight = dip2px(0);
        paddingTop = dip2px(0);
        paddingBottom = dip2px(0);

        marginLeft = dip2px(10);
        marginRight = dip2px(10);
        marginTop = dip2px(12);
        marginBottom = dip2px(10);

        translationZ = dip2px(3);

        translationZDown = dip2px(2);

        radius = dip2px(4);

        height = dip2px(48);

    }

    //设置配置参数
    private void setValue() {
        setType(type);
        setTextColor(textColor);
        setTextSize(textSize);
        setHeight(height);
        setBackground(colorNormal, colorPressed, colorDisable, radius);
        setMargins(marginLeft, marginTop, marginRight, marginBottom);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
//        setTranslationZ(translationZ);
        setElevation(translationZ);
    }

    protected int sp2px(int sp) {
        return WidgeUtil.sp2px(getContext(), sp);
    }

    protected int dip2px(int dp) {
        return WidgeUtil.dip2px(getContext(), dp);
    }

    public void setBackground(int colorNormal, int colorPressed, int colorDisable, int radius) {
        StateListDrawable drawable = WidgeUtil.createShapeStateDrawable(colorNormal,
                colorPressed, colorDisable, radius);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btn.setBackground(drawable);
        } else {
            btn.setBackgroundDrawable(drawable);
        }
    }

    public void setText(CharSequence text) {
        btn.setText(text);
    }

    public void setTextColor(int color) {
        if (color == 0) return;
        btn.setTextColor(color);
    }

    public void setTextSize(int size) {
        if (size == -1) return;
        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setMargins(int left, int top, int right, int bottom) {
        if (left == -1 || top == -1 || right == -1 || bottom == -1) return;
        MarginLayoutParams params = (MarginLayoutParams) btn.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        btn.setLayoutParams(params);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (left == -1 || top == -1 || right == -1 || bottom == -1) return;
        btn.setPadding(left, top, right, bottom);
    }

    public void setHeight(int height) {
        if (height == -1) return;
        ViewGroup.LayoutParams params = btn.getLayoutParams();
        params.height = height;
        btn.setLayoutParams(params);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTranslationZ(float translationZ) {
        if (translationZ == -1 || !btn.isEnabled()) return;
        btn.setTranslationZ(translationZ);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setElevation(float elevation) {
        if (elevation == -1 || !btn.isEnabled()) return;
        btn.setElevation(elevation);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (btn.isEnabled() == enabled) return;
        btn.setEnabled(enabled);
        if (!enabled) {
            setElevation(0);
        } else {
            setElevation(translationZ);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setElevation(translationZDown);
                break;
            case MotionEvent.ACTION_UP:
                setElevation(translationZ);
                break;
        }
        return false;
    }
}
