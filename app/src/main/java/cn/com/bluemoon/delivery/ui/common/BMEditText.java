package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.common.interf.BMEditTextListener;

public class BMEditText extends EditText implements TextWatcher, View.OnFocusChangeListener {

    private BMEditTextListener listener;

    private Drawable drawable;

    private boolean isCleanable = true;

    public BMEditText(Context context) {
        super(context);
        init();
    }

    public BMEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BMEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        setGravity(Gravity.CENTER_VERTICAL);
        drawable = getResources().getDrawable(R.mipmap.ic_clear);
        addTextChangedListener(this);
        setOnFocusChangeListener(this);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if(length()>0){
            setSelection(length());
        }
    }

    public void setListener(BMEditTextListener listener) {
        this.listener = listener;
    }

    //更新状态
    public void updateCleanable(boolean hasFocus) {
        if (isCleanable) {
            if (length() > 0 && hasFocus) {
                setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0], null,
                        drawable, null);
                setSelection(length());
            } else {
                setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0], null, null,
                        null);
            }
        }
    }

    //设置右边图标
    public void setRightDrawable(int rightId) {
        drawable = getContext().getResources().getDrawable(rightId);
        if (isCleanable) {
            updateCleanable(isFocused());
        } else {
            setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0], null, drawable,
                    null);
        }
    }

    //设置是否可点击
    public void setCleanable(boolean isCleanable) {
        this.isCleanable = isCleanable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        Drawable rightIcon = getCompoundDrawables()[DRAWABLE_RIGHT];
        if (rightIcon != null && event.getAction() == MotionEvent.ACTION_UP) {

            int leftEdgeOfRightDrawable = getRight() - getPaddingRight()
                    - rightIcon.getBounds().width();
            if (event.getRawX() >= leftEdgeOfRightDrawable) {

                if (listener != null) {
                    listener.onRightClick(this, isCleanable);
                } else {
                    if (isCleanable) setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        drawable = null;
        super.finalize();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateCleanable(true);
        if (listener != null) {
            listener.afterTextChanged(s);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        updateCleanable(hasFocus);
    }

}

