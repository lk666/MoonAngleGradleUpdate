package cn.com.bluemoon.delivery.ui.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.SquareLayout;
import cn.com.bluemoon.lib.tagview.FlowLayout;
import cn.com.bluemoon.lib_widget.base.BaseBMView;
import cn.com.bluemoon.lib_widget.module.form.BMRadioItem2View;
import cn.com.bluemoon.lib_widget.module.form.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.form.entity.RadioItem;


/**
 * Created by liangjiangli on 2017/6/15.
 */

public class BMSelectorView extends BaseBMView {

    private TextView txtTitle;
    private View lineBottom;
    private FlowLayout layoutRoot;

    public BMSelectorView(Context context) {
        super(context);
    }

    public BMSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        lineBottom = findViewById(R.id.line_bottom);
        layoutRoot = (FlowLayout) findViewById(R.id.layout_root);
        initAttrs(attrs);
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMSelectorView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            if (attr == R.styleable.BMSelectorView_selector_title) {
                setTitle(attribute.getText(attr));
            } else if (attr == R.styleable.BMSelectorView_selector_title_color) {
                setTitleColor(attribute.getColor(attr, 0));
            } else if (attr == R.styleable.BMSelectorView_selector_title_size) {
                setTitleSize(attribute.getDimensionPixelSize(attr, -1));
            } else if (attr == R.styleable.BMSelectorView_selector_line_color) {
                setLineColor(attribute.getColor(attr, 0));
            } else if (attr == R.styleable.BMSelectorView_selector_line_padding_left) {
                setLineMarginLeft(attribute.getDimensionPixelOffset(attr, -1));
            } else if (attr == R.styleable.BMSelectorView_selector_line_padding_right) {
                setLineMarginRight(attribute.getDimensionPixelOffset(attr, -1));
            } else if (attr == R.styleable.BMSelectorView_selector_line_visible) {
                setLineVisible(attribute.getInt(attr, -1));
            } else if (attr == R.styleable.BMSelectorView_selector_title_visible) {
                setTitleVisible(attribute.getInt(attr, -1));
            } else if (attr == R.styleable.BMSelectorView_selector_item_layout) {
                setLayoutId(attribute.getInt(attr, -1));
            } else if (attr == R.styleable.BMSelectorView_selector_mode) {
                setMode(attribute.getInt(attr, 0));
            }
        }
        attribute.recycle();
    }

    private int layoutId = -1;
    int i;

    public void setData(final List<RadioItem> list) {
        i = 0;
        layoutRoot.removeAllViews();
        if (list != null && list.size() > 0) {
            for (final RadioItem item : list) {
                if (layoutId == -1) {
                    layoutId = R.layout.item_bm_selector;
                }
                Button itemView = (Button) LayoutInflater.from(getContext()).inflate(layoutId, null);
                itemView.setId(i);
                itemView.setTag(item.type);
                itemView.setText(item.text);
                if (listener != null) {
                    if (item.type != BMRadioItemView.TYPE_DISABLE) {
                        itemView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button itemView = (Button) view;
                                int count = layoutRoot.getChildCount();
                                boolean isSelcted = false;
                                if (isSingle) {
                                    for (int j = 0; j < count; j++) {
                                        Button btn = (Button) layoutRoot.getChildAt(j);
                                        if (btn.getId() == itemView.getId() && itemView.getTag().equals(BMRadioItemView.TYPE_NORMAL)) {
                                            isSelcted = true;
                                        }
                                        btn.setTag(isSelcted ? BMRadioItemView.TYPE_SELECT : BMRadioItemView.TYPE_NORMAL);
                                        btn.setSelected(isSelcted);
                                    }
                                } else {
                                    if (itemView.getTag().equals(BMRadioItemView.TYPE_NORMAL)) {
                                        isSelcted = true;
                                    }
                                    itemView.setTag(isSelcted ? BMRadioItemView.TYPE_SELECT : BMRadioItemView.TYPE_NORMAL);
                                    itemView.setSelected(isSelcted);
                                }
                                List<RadioItem> selectedList = new ArrayList<>();
                                for (int j = 0; j < count; j++) {
                                    Button btn = (Button) layoutRoot.getChildAt(j);
                                    if (btn.getTag().equals(BMRadioItemView.TYPE_SELECT)) {
                                        selectedList.add(list.get(j));
                                    }
                                }
                                listener.onSelected(selectedList);
                            }
                        });
                    } else {
                        itemView.setEnabled(false);
                    }

                }
                itemView.setSelected(item.type == BMRadioItemView.TYPE_SELECT);
                layoutRoot.addView(itemView);
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
        if (visible != -1) {
            this.lineBottom.setVisibility(visible);
        }
    }

    //设置title，默认不显示
    public void setTitleVisible(int visible) {
        if (visible != -1) {
            this.txtTitle.setVisibility(visible);
        }
    }


    //设置单选多选，默认单选
    private static boolean isSingle = true;
    public final static int SINGLE_TYPE = 0;
    public final static int MULTIPLE_TYPE = 1;

    public void setMode(int type) {
        isSingle = (type == SINGLE_TYPE);
    }


    //设置布局
    protected int getLayoutId() {
        return R.layout.layout_bm_selector;
    }

    public interface ClickListener {
        void onSelected(List<RadioItem> list);
    }


}
