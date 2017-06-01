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

public class BmCellTextView extends FrameLayout {

    private View lineView;
    private TextView titleView;
    private TextView contentView;

    private int title=-1;
    private int content=-1;

    public BmCellTextView(Context context) {
        super(context);
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

    private void getInitData(AttributeSet attrs){
        if(attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BmCellTextView);
            title=  typedArray.getResourceId(R.styleable.BmCellTextView_cell_text_txt_title,-1);
            content=  typedArray.getResourceId(R.styleable.BmCellTextView_cell_text_txt_content,-1);
            typedArray.recycle();
        }
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_cell_text, this, true);
        lineView=findViewById(R.id.line_bottom);
        titleView= (TextView) findViewById(R.id.txt_title);
        contentView= (TextView) findViewById(R.id.txt_content);

        if(title!=-1){
            titleView.setText(title);
        }
        if(content!=-1){
            contentView.setText(content);
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
