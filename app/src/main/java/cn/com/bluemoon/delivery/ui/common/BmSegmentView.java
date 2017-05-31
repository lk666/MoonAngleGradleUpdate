package cn.com.bluemoon.delivery.ui.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;

import static cn.com.bluemoon.delivery.R.id.text;
import static cn.com.bluemoon.delivery.R.id.textView;

/**
 * Created by tangqiwei on 2017/5/26.
 */

public class BmSegmentView extends HorizontalScrollView implements View.OnClickListener {
    /**
     * MODE_FIXED_WIDTH 固定宽度	120
     * MODE_DIVIDE      等分屏幕	"4（含4）个以下等分 超出项，宽度=25%屏幕"
     * MODE_ADAPTIVE    自适应宽度（格子宽度=paddingLeft+选项名称宽度+paddingRight）	padding=10
     */
    private final static int MODE_FIXED_WIDTH = 0, MODE_DIVIDE = 1, MODE_ADAPTIVE = 2;

    private LinearLayout parentGroup;//主要的布局容器

    private int height;//高度
    private int width;//宽度
    private int colorBg;//底色 默认颜色#1fb8ff
    private float translationZ;//z轴高度 默认为2
    private int showMode;//模式
    private int lineWidth;//底部线的宽度 默认70
    private int lineHeight;//底部线的高度 默认2
    private float textSizeCheck;//选中的字体大小 默认15
    private float textSizeUnCheck;//没选中的字体大小 默认14
    private int textColorCheck;//选中的颜色 默认#fff
    private int textColorUnCheck;//没选中的字体颜色 默认#a3e2ff
    private int lineBottomMargin;//线距离底部多远 默认2
    private int markTopMargin;//红点距离顶部多远 默认4
    private int paddingLeft;//自适应模式-MODE_ADAPTIVE，左边内间距 默认10
    private int paddingRight;//自适应模式-MODE_ADAPTIVE，右边内间距 默认10

    private CheckCallBack checkCallBack;

    private List<String> textList;

    private List<Integer> numberList;

    private List<ViewCollective> viewCollectiveList;

    private class ViewCollective {

        public ViewCollective(RelativeLayout layout, TextView textView, View lineView, BmMarkView markView) {
            this.layout = layout;
            this.textView = textView;
            this.lineView = lineView;
            this.markView = markView;
        }

        public RelativeLayout layout;
        public TextView textView;
        public View lineView;
        public BmMarkView markView;
    }

    public BmSegmentView(Context context) {
        super(context);
        init();
    }

    public BmSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getInitData(attrs);
        init();
    }

    public BmSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getInitData(attrs);
        init();
    }

    private void getInitData(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typeArray = getContext().obtainStyledAttributes(attrs, R.styleable.BmSegmentView);
            height = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_height, dip2px(50));
            width = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_width, dip2px(120));
            colorBg = typeArray.getColor(R.styleable.BmSegmentView_segmentview_colorBg, Color.parseColor("#1fb8ff"));
            translationZ = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_translationZ, dip2px(2));
            showMode = typeArray.getInt(R.styleable.BmSegmentView_segmentview_showMode, MODE_DIVIDE);
            lineWidth = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_lineWidth, dip2px(70));
            lineHeight = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_lineHeight, dip2px(2));
            textSizeCheck = typeArray.getDimension(R.styleable.BmSegmentView_segmentview_textSizeCheck, sp2px(15));
            textSizeUnCheck = typeArray.getDimension(R.styleable.BmSegmentView_segmentview_textSizeUnCheck, sp2px(14));
            textColorCheck = typeArray.getColor(R.styleable.BmSegmentView_segmentview_textColorCheck, Color.parseColor("#ffffff"));
            textColorUnCheck = typeArray.getColor(R.styleable.BmSegmentView_segmentview_textColorUnCheck, Color.parseColor("#a3e2ff"));
            lineBottomMargin = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_lineBottomMargin, dip2px(2));
            markTopMargin = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_markTopMargin, dip2px(4));
            paddingLeft = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_paddingLeft, dip2px(10));
            paddingRight = typeArray.getDimensionPixelSize(R.styleable.BmSegmentView_segmentview_paddingRight, dip2px(10));
            typeArray.recycle();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        parentGroup = new LinearLayout(getContext());
        parentGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        parentGroup.setOrientation(LinearLayout.HORIZONTAL);
        parentGroup.setBackgroundColor(colorBg);
        parentGroup.setTranslationZ(translationZ);
        addView(parentGroup);
        viewCollectiveList = new ArrayList<>();
    }

    public void setTextList(List<String> textList) {
        if (textList == null) {
            this.textList = new ArrayList<>();
        } else {
            this.textList = textList;
        }
        addTabLayout();
    }

    public void setNumberList(List<Integer> numberList) {
        this.numberList = new ArrayList<>();
        if (numberList != null) {
            this.numberList.addAll(numberList);
        }
        int gap=textList.size()-this.numberList.size();
        if(gap>0){
            for (int i = 0; i < gap; i++) {
                this.numberList.add(0);
            }
        }
        adjustBmMarkViewLocation();

    }

    public void setCheckCallBack(CheckCallBack checkCallBack) {
        this.checkCallBack = checkCallBack;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        checkUIChange(position);
        if (checkCallBack != null) {
            checkCallBack.checkListener(position);
        }
    }

    /**
     * 选中时UI的变化
     *
     * @param position
     */
    private void checkUIChange(int position) {
        for (int i = 0; i < viewCollectiveList.size(); i++) {
            ViewCollective viewCollective = viewCollectiveList.get(i);
            if (position == i) {
                viewCollective.textView.setTextColor(textColorCheck);
                viewCollective.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeCheck);
                viewCollective.lineView.setVisibility(View.VISIBLE);
            } else {
                viewCollective.textView.setTextColor(textColorUnCheck);
                viewCollective.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeUnCheck);
                viewCollective.lineView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 计算tab宽度
     *
     * @param size
     * @param showMode
     */
    private int calculativeWidth(int size, int showMode) {
        switch (showMode) {
            case MODE_FIXED_WIDTH:
                return width;
            case MODE_DIVIDE:
                if (size < 5) {
                    return getDisplayWidth() / size;
                } else {
                    return getDisplayWidth() / 4;
                }
            default:
                return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private void addTabLayout() {
        int width = calculativeWidth(textList.size(), showMode);
        for (int i = 0; i < textList.size(); i++) {
            parentGroup.addView(getTabLayout(width, i));
        }
        if (textList.size() > 0)
            checkUIChange(0);
    }

    private RelativeLayout getTabLayout(int width, int position) {
        RelativeLayout layoutGroup = new RelativeLayout(getContext());
        layoutGroup.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutGroup.setPadding(paddingLeft, 0, paddingRight, 0);
        }
        View lineView = getLineView();
        TextView textView = getTextView(position);
        BmMarkView bmMarkView = getBmMarkView(textView);
        layoutGroup.addView(lineView);
        layoutGroup.addView(textView);
        layoutGroup.addView(bmMarkView);
        layoutGroup.setTag(position);
        layoutGroup.setOnClickListener(this);
        viewCollectiveList.add(new ViewCollective(layoutGroup, textView, lineView, bmMarkView));
        return layoutGroup;
    }

    /**
     * 生产底部的线
     *
     * @return
     */
    private View getLineView() {
        View line = new View(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lineWidth, lineHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = lineBottomMargin;
        line.setLayoutParams(params);
        line.setBackgroundColor(textColorCheck);
        line.setVisibility(View.INVISIBLE);
        return line;
    }

    /**
     * 生产TextVuew
     *
     * @return
     */
    private TextView getTextView(int position) {
        TextView textView = new TextView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setTextColor(textColorUnCheck);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeUnCheck);
        textView.setLayoutParams(params);
        textView.setId(position+1);
        textView.setText(textList.get(position));
        return textView;
    }

    /**
     * 生产红点
     *
     * @return
     */
    private BmMarkView getBmMarkView(TextView textView) {
        BmMarkView markView = new BmMarkView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_RIGHT, textView.getId());
        params.topMargin = markTopMargin;
        markView.setLayoutParams(params);
        return markView;
    }

    /**
     * 调整红点位置
     * 设置值
     * @return
     */
    private void adjustBmMarkViewLocation() {
        for (int i = 0; i < viewCollectiveList.size(); i++) {
            BmMarkView markView = viewCollectiveList.get(i).markView;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) markView.getLayoutParams();
            params.rightMargin = -markView.setMarkViewWidthAndText(numberList.get(i)) / 2;
            markView.setLayoutParams(params);
        }
    }


    public interface CheckCallBack {
        void checkListener(int position);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public int getDisplayWidth() {
        return ((WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

    /**
     * dp  转 px
     *
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp  转 px
     *
     * @param spValue
     * @return
     */
    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
