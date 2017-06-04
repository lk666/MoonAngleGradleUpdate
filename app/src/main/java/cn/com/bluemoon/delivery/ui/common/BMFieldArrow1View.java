package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by bm on 2017/5/25.
 */

public class BMFieldArrow1View extends RelativeLayout implements View.OnClickListener {

    private TextView txtTitle;
    private TextView txtContent;
    private ImageView imgRight;
    private View lineBottom;
    private View layoutMain;
    private FieldArrowListener listener;

    public BMFieldArrow1View(Context context) {
        super(context);
        init();
    }

    public BMFieldArrow1View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_field_arrow1, this, true);
        layoutMain = findViewById(R.id.layout_main);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtContent = (TextView) findViewById(R.id.txt_content);
        imgRight = (ImageView) findViewById(R.id.img_right);
        lineBottom = findViewById(R.id.lin_bottom);

        layoutMain.setOnClickListener(this);
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMFieldArrow1View);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            switch (attr) {
                case R.styleable.BMFieldArrow1View_field_arrow_title:
                    setTitle(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_title_color:
                    setTitleColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_title_size:
                    setTitleSize(attribute.getDimensionPixelSize(attr, -1));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_hint:
                    setHint(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_hint_color:
                    setHintColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_content:
                    setContent(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_content_color:
                    setContentColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_content_size:
                    setContentSize(attribute.getDimensionPixelSize(attr, -1));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_line_color:
                    setLineColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_line_padding_left:
                    setLineMarginLeft(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_line_padding_right:
                    setLineMarginRight(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_img:
                    setImageRight(attribute.getResourceId(attr, -1));
                    break;
                case R.styleable.BMFieldArrow1View_field_arrow_img_clickable:
                    setRightClickable(attribute.getBoolean(attr,false));
                    break;
            }
        }
        attribute.recycle();
    }

    //公共方法

    public void setListener(FieldArrowListener listener) {
        this.listener = listener;
    }

    //设置标题文字，默认“已经到底了”
    public void setTitle(CharSequence title) {
        txtTitle.setText(title);
    }

    //设置标题颜色，默认#666
    public void setTitleColor(int color) {
        txtTitle.setTextColor(color);
    }

    //设置标题大小，默认11
    public void setTitleSize(float size) {
        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    //设置提示语
    public void setHint(CharSequence hint) {
        txtContent.setHint(hint);
    }

    //设置提示语颜色，默认#999
    public void setHintColor(int color) {
        txtContent.setHintTextColor(color);
    }

    //设置正文内容
    public void setContent(CharSequence content) {
        txtContent.setText(content);
    }

    //设置正文颜色，默认#666
    public void setContentColor(int color) {
        txtContent.setTextColor(color);
    }

    //设置正文大小，默认14
    public void setContentSize(float size) {
        txtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    //设置底边颜色，默认#e5e5e5
    public void setLineColor(int color) {
        lineBottom.setBackgroundColor(color);
    }

    //设置底边左右边距，默认为0
    public void setLineMarginLeft(int left) {
        if (left == -1) return;
        MarginLayoutParams params = (MarginLayoutParams) lineBottom.getLayoutParams();
        params.leftMargin = left;
        lineBottom.setLayoutParams(params);
    }

    //设置底边左右边距，默认为0
    public void setLineMarginRight(int right) {
        if (right == -1) return;
        MarginLayoutParams params = (MarginLayoutParams) lineBottom.getLayoutParams();
        params.rightMargin = right;
        lineBottom.setLayoutParams(params);
    }

    //设置右边图标
    public void setImageRight(int resId) {
        imgRight.setImageResource(resId);
    }

    //获取编辑框内容
    public String getContent() {
        return txtContent.getText().toString();
    }

    //设置右图标点击事件
    public void setRightClickable(boolean clickable) {
        imgRight.setClickable(clickable);
        imgRight.setOnClickListener(clickable ? this : null);
    }

    @Override
    public void onClick(View v) {
        if (v == layoutMain) {
            if (listener != null) {
                listener.onClickLayout();
            }
        } else if (v == imgRight) {
            if (listener != null) {
                listener.onClickRight();
            }
        }
    }

    public interface FieldArrowListener {
        //点击整个控件相应事件
        void onClickLayout();

        //点击有图标相应事件
        void onClickRight();
    }
}
