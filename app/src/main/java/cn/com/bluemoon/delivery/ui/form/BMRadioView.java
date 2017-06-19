package cn.com.bluemoon.delivery.ui.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.lib_widget.base.BaseBMView;
import cn.com.bluemoon.lib_widget.module.form.BMRadioItem2View;
import cn.com.bluemoon.lib_widget.module.form.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.form.entity.RadioItem;


/**
 * Created by liangjiangli on 2017/6/15.
 */

public class BMRadioView extends BaseBMView {

    private TextView txtTitle;
    private View lineBottom;
    private LinearLayout layoutRight;

    public BMRadioView(Context context) {
        super(context);
    }

    public BMRadioView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        lineBottom = findViewById(R.id.line_bottom);
        layoutRight = (LinearLayout)findViewById(R.id.layout_right);
        initAttrs(attrs);
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMRadioView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            if (attr == R.styleable.BMRadioView_radio_title) {
                setTitle(attribute.getText(attr));
            } else if (attr == R.styleable.BMRadioView_radio_title_color) {
                setTitleColor(attribute.getColor(attr, 0));
            } else if (attr == R.styleable.BMRadioView_radio_title_size) {
                setTitleSize(attribute.getDimensionPixelSize(attr, -1));
            } else if (attr == R.styleable.BMRadioView_radio_line_color) {
                setLineColor(attribute.getColor(attr, 0));
            } else if (attr == R.styleable.BMRadioView_radio_line_padding_left) {
                setLineMarginLeft(attribute.getDimensionPixelOffset(attr, -1));
            } else if (attr == R.styleable.BMRadioView_radio_line_padding_right) {
                setLineMarginRight(attribute.getDimensionPixelOffset(attr, -1));
            } else if(attr == R.styleable.BMRadioView_radio_line_visible) {
                setLineVisible(attribute.getInt(attr, -1));
            }else if(attr == R.styleable.BMRadioView_radio_item_layout) {
                setLayoutId(attribute.getInt(attr, -1));
            }
        }
        attribute.recycle();
    }

    private int layoutId = -1;
    int i;
    public void setData(List<RadioItem> list) {
        i = 0;
        layoutRight.removeAllViews();
        if (list != null && list.size() > 0) {
            for (final RadioItem item : list) {
                BMRadioItem2View itemView;
                if (layoutId == -1) {
                    itemView = new BMRadioItem2View(getContext());
                } else {
                    itemView = (BMRadioItem2View)LayoutInflater.from(getContext()).inflate(layoutId, null);
                }
                itemView.setId(i);
                itemView.setType(item.type);
                itemView.setContent(item.text);
                if (listener != null && item.type != BMRadioItemView.TYPE_DISABLE) {
                    itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BMRadioItem2View itemView = (BMRadioItem2View)view;
                            int count = layoutRight.getChildCount();
                            for (int j = 0; j < count; j++) {
                                BMRadioItem2View radioView = (BMRadioItem2View)layoutRight.getChildAt(j);
                                radioView.setType(radioView.getId() == itemView.getId() ? BMRadioItemView.TYPE_SELECT : BMRadioItemView.TYPE_NORMAL);
                            }
                            listener.onSelected(i, item);
                        }
                    });
                }
                layoutRight.addView(itemView);
                i++;
            }

        }
    }

    private ClickListener listener;
    public void setListener(ClickListener listener) {
        this.listener = listener;
    }


    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
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

    public void setLineVisible(int visible) {
        if(visible != -1) {
            this.lineBottom.setVisibility(visible);
        }
    }


    //设置布局
    protected int getLayoutId(){
        return R.layout.layout_bm_radio;
    }

    public interface ClickListener {
        void onSelected(int position, Object value);
    }


}
