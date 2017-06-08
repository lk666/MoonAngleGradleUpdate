package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.common.interf.BMFieldListener;

/**
 * Created by bm on 2017/5/25.
 */

public class BMFieldParagraphView extends RelativeLayout implements View.OnTouchListener{

    private TextView txtTitle;
    private TextView txtCount;
    private EditText etContent;
    private View lineBottom;

    private int maxCount;

    private BMFieldListener listener;

    public BMFieldParagraphView(Context context) {
        super(context);
        init();
    }

    public BMFieldParagraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_field_paragraph, this, true);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtCount = (TextView) findViewById(R.id.txt_count);
        etContent = (EditText) findViewById(R.id.et_content);
        lineBottom = findViewById(R.id.lin_bottom);
        etContent.addTextChangedListener(textWatcher);
        etContent.setOnTouchListener(this);
        setMaxCount(200);
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMFieldParagraphView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            switch (attr) {
                case R.styleable.BMFieldParagraphView_field_title:
                    setTitle(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldParagraphView_field_title_color:
                    setTitleColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldParagraphView_field_title_size:
                    setTitleSize(attribute.getDimensionPixelSize(attr, -1));
                    break;
                case R.styleable.BMFieldParagraphView_field_count_color:
                    setCountColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldParagraphView_field_count_size:
                    setCountSize(attribute.getDimensionPixelSize(attr, -1));
                    break;
                case R.styleable.BMFieldParagraphView_field_hint:
                    setHint(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldParagraphView_field_hint_color:
                    setHintColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldParagraphView_field_content:
                    setContent(attribute.getText(attr));
                    break;
                case R.styleable.BMFieldParagraphView_field_content_color:
                    setContentColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldParagraphView_field_content_size:
                    setContentSize(attribute.getDimensionPixelSize(attr, -1));
                    break;
                case R.styleable.BMFieldParagraphView_field_content_maxLength:
                    setMaxCount(attribute.getInt(attr, maxCount));
                    break;
                case R.styleable.BMFieldParagraphView_field_line_color:
                    setLineColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMFieldParagraphView_field_line_padding_left:
                    setLineMarginLeft(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMFieldParagraphView_field_line_padding_right:
                    setLineMarginRight(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMFieldParagraphView_field_count_visible:
                    setCountVisible(attribute.getInt(attr, -1));
                    break;
            }
        }
        attribute.recycle();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateCountText();
            if(listener!=null){
                listener.afterTextChanged(BMFieldParagraphView.this,s.toString());
            }
        }
    };

    public void setListener(BMFieldListener listener){
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
        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }

    //设置字数颜色，默认#ff6c47
    public void setCountColor(int color) {
        txtCount.setTextColor(color);
    }

    //设置字数大小，默认11
    public void setCountSize(float size) {
        txtCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }

    //设置字数大小，默认11
    public void setCountVisible(int visible) {
        if (visible == -1) return;
        txtCount.setVisibility(visible);
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
        etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }

    //设置字数限制
    public void setMaxCount(int count) {
        if (count < 0) return;
        maxCount = count;
        InputFilter[] filters = {new InputFilter.LengthFilter(count)};
        etContent.setFilters(filters);
        updateCountText();
    }

    //更新字数统计的文案
    public void updateCountText() {
        txtCount.setText(etContent.getText().toString().length() + "/" + maxCount);
    }

    //设置底边颜色，默认#e5e5e5
    public void setLineColor(int color) {
        lineBottom.setBackgroundColor(color);
    }

    //设置底边左右边距，默认为0
    public void setLineMarginLeft(int left) {
        if (left == -1) return;
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) lineBottom.getLayoutParams();
        params.leftMargin = left;
        lineBottom.setLayoutParams(params);
    }

    //设置底边左右边距，默认为0
    public void setLineMarginRight(int right) {
        if (right == -1) return;
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) lineBottom.getLayoutParams();
        params.rightMargin = right;
        lineBottom.setLayoutParams(params);
    }

    //获取编辑框内容
    public String getContent(){
        return etContent.getText().toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.getParent().requestDisallowInterceptTouchEvent(false);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.getParent().requestDisallowInterceptTouchEvent(false);
        }
        return false;
    }
}
