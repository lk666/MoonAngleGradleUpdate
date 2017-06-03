package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by tangqiwei on 2017/5/25.
 */

public class BmCellTextView extends FrameLayout {

    private View lineView;
    private TextView titleView;
    private TextView contentView;
    private LinearLayout layouotParent;

    private CharSequence title;
    private CharSequence content;

    private int titleColor;
    private int contentColor;
    private int lineBg;

    private int titleSize;
    private int contentSize;

    private int minHeight;
    private int lineHeight;
    private int linePaddingLeft;
    private int linePaddingRight;
    private int paddingLeft;
    private int paddingRight;


    public BmCellTextView(Context context) {
        super(context);
        getInitData();
        init();
    }

    public BmCellTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getInitData(attrs);
        init();
    }

    public BmCellTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getInitData(attrs);
        init();
    }

    private void getInitData(){
        getInitData(null);
    }
    private void getInitData(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                    .BmCellTextView);
            int n = attribute.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = attribute.getIndex(i);
                switch (attr) {
                    case R.styleable.BmCellTextView_cell_text_txt_title:
                        title = attribute.getText(attr);
                        break;
                    case R.styleable.BmCellTextView_cell_text_title_color:
                        titleColor = attribute.getColor(attr,Color.parseColor("#666666"));
                        break;
                    case R.styleable.BmCellTextView_cell_text_title_size:
                        titleSize = attribute.getDimensionPixelSize(attr,0);
                        break;
                    case R.styleable.BmCellTextView_cell_text_txt_content:
                        content = attribute.getText(attr);
                        break;
                    case R.styleable.BmCellTextView_cell_text_content_color:
                        contentColor = attribute.getColor(attr,Color.parseColor("#333333"));
                        break;
                    case R.styleable.BmCellTextView_cell_text_content_size:
                        contentSize = attribute.getDimensionPixelSize(attr,0);
                        break;
                    case R.styleable.BmCellTextView_cell_text_line_color:
                        lineBg = attribute.getColor(attr,Color.parseColor("#E5E5E5"));
                        break;
                    case R.styleable.BmCellTextView_cell_text_line_height:
                        lineHeight = attribute.getDimensionPixelSize(attr,-1);
                        break;
                    case R.styleable.BmCellTextView_cell_text_line_paddingleft:
                        linePaddingLeft = attribute.getDimensionPixelSize(attr,0);
                        break;
                    case R.styleable.BmCellTextView_cell_text_line_paddingright:
                        linePaddingRight= attribute.getDimensionPixelSize(attr,0);
                        break;
                    case R.styleable.BmCellTextView_cell_text_paddingleft:
                        paddingLeft = attribute.getDimensionPixelSize(attr,0);
                        break;
                    case R.styleable.BmCellTextView_cell_text_paddingright:
                        paddingRight= attribute.getDimensionPixelSize(attr,0);
                        break;
                    case R.styleable.BmCellTextView_cell_text_min_height:
                        minHeight= attribute.getDimensionPixelSize(attr,0);
                        break;
                }
            }
            attribute.recycle();
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_cell_text, this, true);
        lineView = findViewById(R.id.line_bottom);
        titleView = (TextView) findViewById(R.id.txt_title);
        contentView = (TextView) findViewById(R.id.txt_content);
        layouotParent= (LinearLayout) findViewById(R.id.llayout_parent);
        initView();
    }

    public void initView(){
        titleView.setText(title);
        contentView.setText(content);
        if(titleSize>0){
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
        }
        if(contentSize>0){
            contentView.setTextSize(TypedValue.COMPLEX_UNIT_PX,contentSize);
        }
        if(titleColor>0){
            titleView.setTextColor(titleColor);
        }
        if(contentColor>0){
            contentView.setTextColor(contentColor);
        }
        if(lineBg>0){
            lineView.setBackgroundColor(lineBg);
        }
        if(minHeight>0){
            layouotParent.setMinimumHeight(minHeight);
        }
        if (lineHeight>0) {
            ViewGroup.LayoutParams params=lineView.getLayoutParams();
            params.height=lineHeight;
            lineView.setLayoutParams(params);
        }
        if(linePaddingLeft>0){
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) lineView.getLayoutParams();
            params.leftMargin=linePaddingLeft;
            lineView.setLayoutParams(params);
        }
        if (linePaddingRight>0) {
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) lineView.getLayoutParams();
            params.rightMargin=linePaddingRight;
            lineView.setLayoutParams(params);
        }
        if(paddingLeft>0){
            layouotParent.setPadding(paddingLeft,layouotParent.getPaddingTop(),layouotParent.getPaddingRight(),layouotParent.getPaddingBottom());
        }
        if(paddingRight>0){
            layouotParent.setPadding(layouotParent.getPaddingLeft(),layouotParent.getPaddingTop(),paddingRight,layouotParent.getPaddingBottom());
        }
    }

    public String getContentText() {
        return contentView.getText().toString();
    }

    public void setContentText(String content) {
        contentView.setText(content);
    }

    public String getTitleText() {
        return titleView.getText().toString();
    }

    public void setTitleText(String title) {
        titleView.setText(title);
    }
}
