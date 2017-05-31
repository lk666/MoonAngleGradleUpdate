package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by bm on 2017/5/25.
 */

public class BMListPaginationView extends RelativeLayout {

    private TextView txtContent;
    private View lineLeft;
    private View lineRight;

    public BMListPaginationView(Context context) {
        super(context);
        init();
    }

    public BMListPaginationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_listpagination, this, true);
        txtContent = (TextView) findViewById(R.id.txt_content);
        lineLeft = findViewById(R.id.view_line_left);
        lineRight = findViewById(R.id.view_line_right);
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs){
        if(attrs==null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs,R.styleable.BMListPaginationView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            switch (attr) {
                case R.styleable.BMListPaginationView_pag_text:
                    setText(attribute.getText(attr));
                    break;
                case R.styleable.BMListPaginationView_pag_text_color:
                    setTextColor(attribute.getColor(attr,0));
                    break;
                case R.styleable.BMListPaginationView_pag_line_color:
                    setLineColor(attribute.getColor(attr,0));
                    break;
                case R.styleable.BMListPaginationView_pag_line_size:
                    setLineSize(attribute.getDimensionPixelSize(attr,-1));
                    break;

            }
        }
        attribute.recycle();
    }

    //设置文字，默认“已经到底了”
    public void setText(CharSequence title){
        txtContent.setText(title);
    }

    //设置文字颜色，默认#999
    public void setTextColor(int color){
        txtContent.setTextColor(color);
    }

    //设置分割线颜色，默认#e5e5e5
    public void setLineColor(int color){
        lineLeft.setBackgroundColor(color);
        lineRight.setBackgroundColor(color);
    }

    //设置分割线大小，默认0.5dp
    public void setLineSize(int size){
        if(size<0) return;
        ViewGroup.LayoutParams params = lineLeft.getLayoutParams();
        params.height = size;
        lineLeft.setLayoutParams(params);
        lineRight.setLayoutParams(params);
    }
}
