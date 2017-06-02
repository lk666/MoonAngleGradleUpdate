package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by tangqiwei on 2017/5/25.
 */

public class BmCellParagraphView extends FrameLayout {

    private View lineView;
    private TextView titleView;
    private TextView contentView;

    private CharSequence title;
    private CharSequence content;

    public BmCellParagraphView(Context context) {
        super(context);
        init();
    }

    public BmCellParagraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getInitData(attrs);
        init();
    }

    public BmCellParagraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getInitData(attrs);
        init();
    }
    private void getInitData(AttributeSet attrs) {
        if(attrs!=null){
            TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                    .BmCellParagraphView);
            int n = attribute.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = attribute.getIndex(i);
                switch (attr) {
                    case R.styleable.BmCellParagraphView_cell_paragraph_txt_title:
                        title = attribute.getText(attr);
                        break;
                    case R.styleable.BmCellParagraphView_cell_paragraph_txt_content:
                        content = attribute.getText(attr);
                        break;
                }
            }
            attribute.recycle();
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_cell_paragraph,this,true);
        lineView=findViewById(R.id.line_bottom);
        titleView= (TextView) findViewById(R.id.txt_title);
        contentView= (TextView) findViewById(R.id.txt_content);

        titleView.setText(title);
        contentView.setText(content);
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
