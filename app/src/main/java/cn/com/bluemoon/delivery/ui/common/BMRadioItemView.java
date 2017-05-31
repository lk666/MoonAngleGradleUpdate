package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.common.photopicker.Image;

/**
 * Created by bm on 2017/5/25.
 */

public class BMRadioItemView extends RelativeLayout {

    public static final int TYPE_DISABLE = -1;
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SELECT = 1;

    private LinearLayout layoutMain;
    private ImageView imgCheck;
    private TextView txtContent;
    private View lineBottom;

    private int type = 0;
    private int colorNormal;
    private int colorSelect;
    private int colorDisable;

    private int drawableNormal;
    private int drawableSelect;
    private int drawableDisable;

    public BMRadioItemView(Context context) {
        super(context);
        init(null);
    }

    public BMRadioItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    //设置默认颜色值,可重写
    protected void initSelector() {
        //文字颜色
        colorNormal = 0xff333333;
        colorSelect = 0xff333333;
        colorDisable = 0xff999999;

        //选择图标
        drawableNormal = R.mipmap.bm_radio_normal;
        drawableSelect = R.mipmap.bm_radio_selected;
        drawableDisable = R.mipmap.bm_radio_disable;

    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.item_bm_radio_list, this, true);
        layoutMain = (LinearLayout) findViewById(R.id.layout_main);
        imgCheck = (ImageView) findViewById(R.id.img_check);
        txtContent = (TextView) findViewById(R.id.txt_content);
        lineBottom = findViewById(R.id.line_bottom);

//        layoutMain.setOnClickListener(this);
        //设置默认值
        initSelector();
        //获取自定义属性
        initAttrs(attrs);

        //设置按钮和文字的样式选择器
        txtContent.setTextColor(createColorStateList(colorNormal, colorSelect, colorDisable));
        imgCheck.setImageDrawable(createStateListDrawable(drawableNormal, drawableSelect,
                drawableDisable));
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMRadioItemView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            switch (attr) {
                case R.styleable.BMRadioItemView_radio_item_drawable_normal:
                    drawableNormal = attribute.getResourceId(attr,-1);
                    break;
                case R.styleable.BMRadioItemView_radio_item_drawable_select:
                    drawableSelect = attribute.getResourceId(attr,-1);
                    break;
                case R.styleable.BMRadioItemView_radio_item_drawable_disable:
                    drawableDisable = attribute.getResourceId(attr,-1);
                    break;
                case R.styleable.BMRadioItemView_radio_item_text:
                    setContent(attribute.getText(attr));
                    break;
                case R.styleable.BMRadioItemView_radio_item_text_color_normal:
                    colorNormal = attribute.getColor(attr,0);
                    break;
                case R.styleable.BMRadioItemView_radio_item_text_color_select:
                    colorSelect = attribute.getColor(attr,0);
                    break;
                case R.styleable.BMRadioItemView_radio_item_text_color_disable:
                    colorDisable = attribute.getColor(attr,0);
                    break;
                case R.styleable.BMRadioItemView_radio_item_text_size:
                    setContentSize(attribute.getDimension(attr, -1));
                    break;
                case R.styleable.BMRadioItemView_radio_item_line_color:
                    setLineColor(attribute.getColor(attr, 0));
                    break;
                case R.styleable.BMRadioItemView_radio_item_line_size:
                    setLineSize(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMRadioItemView_radio_item_line_padding_left:
                    setLineMarginLeft(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMRadioItemView_radio_item_line_padding_right:
                    setLineMarginRight(attribute.getDimensionPixelOffset(attr, -1));
                    break;
                case R.styleable.BMRadioItemView_radio_item_line_visible:
                    setLineVisible(attribute.getInt(attr, -1));
                    break;
            }
        }
        attribute.recycle();
    }

    //设置单选框样式
    public void setType(int type) {
        switch (type) {
            case TYPE_NORMAL:
                this.type = type;
                setSelect(false);
                break;
            case TYPE_SELECT:
                this.type = type;
                setSelect(true);
                break;
            case TYPE_DISABLE:
                this.type = type;
                setDisable(false);
                break;
        }
    }

    //设置是否可选择
    private void setDisable(boolean enable) {
        imgCheck.setEnabled(enable);
        txtContent.setEnabled(enable);
        layoutMain.setEnabled(enable);
    }

    //设置是否选择
    private void setSelect(boolean selected) {
        imgCheck.setSelected(selected);
        txtContent.setSelected(selected);
        setDisable(true);
    }

    //获取颜色选择器
    private ColorStateList createColorStateList(int normal, int select, int disable) {
        int[] colors = new int[]{normal, select, disable};
        int[][] states = new int[3][];
        states[0] = new int[]{-android.R.attr.state_selected, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_selected, android.R.attr.state_enabled};
        states[2] = new int[]{-android.R.attr.state_enabled};
        return new ColorStateList(states, colors);
    }

    //获取图片资源选择器
    private StateListDrawable createStateListDrawable(int idNormal, int idSelect, int idDisable) {
        StateListDrawable stateList = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : getContext().getResources().getDrawable(idNormal);
        Drawable select = idSelect == -1 ? null : getContext().getResources().getDrawable(idSelect);
        Drawable disable = idDisable == -1 ? null : getContext().getResources().getDrawable
                (idDisable);
        stateList.addState(new int[]{-android.R.attr.state_selected, android.R.attr
                .state_enabled}, normal);
        stateList.addState(new int[]{android.R.attr.state_selected, android.R.attr
                .state_enabled}, select);
        stateList.addState(new int[]{-android.R.attr.state_enabled}, disable);
        return stateList;
    }


    //公共方法

    public View getLayoutMain(){
        return layoutMain;
    }

    //设置图片资源
    public void setImgCheck(int resId) {
        if (resId == -1) return;
        imgCheck.setImageResource(resId);
    }

    //设置正文内容
    public void setContent(CharSequence content) {
        txtContent.setText(content);
    }

    //设置正文颜色，默认#333
    public void setContentColor(int color) {
        if (color == -1) return;
        txtContent.setTextColor(color);
    }

    //设置正文大小，默认14
    public void setContentSize(float size) {
        if (size <= 0) return;
        txtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    //设置分割线颜色，默认#e5e5e5
    public void setLineColor(int color) {
        if (color == 0) return;
        lineBottom.setBackgroundColor(color);
    }

    //设置分割线粗细，默认0.5dp
    public void setLineSize(int size) {
        if (size == -1) return;
        ViewGroup.LayoutParams params = lineBottom.getLayoutParams();
        params.height = size;
        lineBottom.setLayoutParams(params);
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

    //设置底边是否隐藏
    public void setLineVisible(int visible) {
        if (visible == -1) return;
        lineBottom.setVisibility(visible);
    }

}
