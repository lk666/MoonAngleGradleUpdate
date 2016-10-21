package cn.com.bluemoon.delivery.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import cn.com.bluemoon.delivery.R;

/**
 * Created by ljl on 2016/9/21.
 */
public class UpDownTextView extends LinearLayout {
    private boolean isUp;
    private String upText;
    private String downText;
    private TextView textView;
    private ImageView imageView;

    private boolean defaultStatus;
    private ClickListener listener;
    private int index;

    public UpDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UpDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setListener(ClickListener l, int index) {
        this.listener = l;
        this.index = index;
    }

    public boolean isUp() {
        return isUp;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init(final Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.UpDownTextView);
        isUp = typedArray.getBoolean(R.styleable.UpDownTextView_is_up, true);
        defaultStatus = isUp;
        boolean hasText = typedArray.getBoolean(R.styleable.UpDownTextView_has_text, true);
        this.setGravity(Gravity.CENTER_VERTICAL);
        if (hasText) {
            String text = typedArray.getString(R.styleable.UpDownTextView_tip_text);
            if (!StringUtils.isNotBlank(text)) {
                text = context.getString(R.string.open_close);
            }
            String[] ss = text.split("\\,");
            if (ss.length == 2){
                upText = ss[0];
                downText = ss[1];
                textView = new TextView(context);
                textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                float textSize = typedArray.getDimension(R.styleable.UpDownTextView_utext_size, 0);
                if (textSize == 0) {
                    textSize = context.getResources().getDimension(R.dimen.text_size_15);
                }
                int textColor  = typedArray.getColor(R.styleable.UpDownTextView_utext_color, getContext().getResources().getColor(R.color.text_grep));
                textView.setText(upText);
               if (isUp) {
                    if (StringUtils.isNotBlank(downText))
                        textView.setText(downText);
                } else {
                    if (StringUtils.isNotBlank(upText))
                        textView.setText(upText);
                }

                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                textView.setTextColor(textColor);
                textView.setPadding(0,0,10,0);
                this.addView(textView);
            }
        }

        imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (isUp) {
            imageView.setImageResource(R.mipmap.ic_up);
        } else {
            imageView.setImageResource(R.mipmap.ic_down);
        }
        this.addView(imageView);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void changeStatus () {
        isUp = !isUp;
        if (isUp) {
            if (StringUtils.isNotBlank(downText))
                textView.setText(downText);
        } else {
            if (StringUtils.isNotBlank(upText))
                textView.setText(upText);
        }
        float pivotX = imageView.getWidth() / 2f;
        float pivotY = imageView.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (defaultStatus) {
            if (isUp) {
                fromDegrees = 180f;
                toDegrees = 360f;
            } else {
                fromDegrees = 0f;
                toDegrees = 180f;
            }
        } else {
            if (!isUp) {
                fromDegrees = 180f;
                toDegrees = 360f;
            } else {
                fromDegrees = 0f;
                toDegrees = 180f;
            }
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        imageView.startAnimation(animation);
        if (listener != null) {
            listener.onClick(isUp, index);
        }
    }

    public interface ClickListener {
        void onClick(boolean clicked, int index);
    }

}
