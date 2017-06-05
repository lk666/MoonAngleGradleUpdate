package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.common.interf.BMEditTextListener;
import cn.com.bluemoon.delivery.ui.common.interf.BMFieldListener;

/**
 * Created by bm on 2017/5/25.
 */

public class BMFieldText1View extends RelativeLayout {

    private TextView txtTitle;
    private BMEditText etContent;
    private View lineBottom;

    private BMFieldListener listener;

    public BMFieldText1View(Context context) {
        super(context);
        init();
    }

    public BMFieldText1View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_field_text1, this, true);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        etContent = (BMEditText) findViewById(R.id.et_content);
        lineBottom = findViewById(R.id.lin_bottom);
        etContent.setCleanable(true);
        etContent.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        etContent.setListener(new BMEditTextListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.afterTextChanged(BMFieldText1View.this, s.toString());
                }
            }
        });
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMFieldText1View);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            switch (attr) {
                case R.styleable.BMFieldText1View_field_text1_title:
                    setTitle(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldText1View_field_text1_title_color:
                    setTitleColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldText1View_field_text1_title_size:
                    setTitleSize(attribute.getDimensionPixelSize(attr, -1));
                    break;
                case R.styleable.BMFieldText1View_field_text1_hint:
                    setHint(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldText1View_field_text1_hint_color:
                    setHintColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldText1View_field_text1_content:
                    setContent(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldText1View_field_text1_content_color:
                    setContentColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldText1View_field_text1_content_size:
                    setContentSize(attribute.getDimensionPixelSize(attr, -1));
                    break;
                case R.styleable.BMFieldText1View_field_text1_line_color:
                    setLineColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldText1View_field_text1_line_padding_left:
                    setLineMarginLeft(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMFieldText1View_field_text1_line_padding_right:
                    setLineMarginRight(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMFieldParagraphView_field_content_maxLength:
                    setMaxLength(attribute.getInt(attr, Integer.MAX_VALUE));
                    break;
            }
        }
        attribute.recycle();
    }

    public void setListener(BMFieldListener listener) {
        this.listener = listener;
    }


    //公共方法

    //设置标题文字
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
        etContent.setHint(hint);
    }

    //设置提示语颜色，默认#999
    public void setHintColor(int color) {
        etContent.setHintTextColor(color);
    }

    //设置正文内容
    public void setContent(CharSequence content) {
        etContent.setText(content);
    }

    //设置正文颜色，默认#666
    public void setContentColor(int color) {
        etContent.setTextColor(color);
    }

    //设置正文大小，默认14
    public void setContentSize(float size) {
        etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
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

    //设置最大字数
    public void setMaxLength(int maxLength) {
        etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength + 1)});
    }

    //获取编辑框内容
    public String getContent() {
        return etContent.getText().toString();
    }

    //设置inputType
    public void setInputType(int inputType) {
        etContent.setInputType(inputType);
    }

}
