package cn.com.bluemoon.delivery.ui.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    private int colorBg;//底色
    private float translationZ;//z轴高度
    private int showMode;//z轴高度
    private int lineWidth;//底部线的宽度 默认70
    private int lineHeight;//底部线的高度 默认2
    private int textSizeCheck;//选中的字体大小 默认15
    private int textSizeUnCheck;//没选中的字体大小 默认14
    private int textColorCheck;//选中的颜色 默认#fff
    private int textColorUnCheck;//没选中的字体颜色 默认#a3e2ff
    private int lineBottomMargin;//线距离底部多远 默认2
    private int markTopMargin;//红点距离顶部多远 默认4
    private int paddingLeft;//自适应模式-MODE_ADAPTIVE，左边内间距 默认10
    private int paddingRight;//自适应模式-MODE_ADAPTIVE，右边内间距 默认10

    private List<String> textList;

    private List<ViewCollective> viewCollectiveList;

    private class ViewCollective {

        public ViewCollective(RelativeLayout layout,TextView textView,View lineView,BmMarkView markView){
            this.layout=layout;
            this.textView=textView;
            this.lineView=lineView;
            this.markView=markView;
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

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        parentGroup = new LinearLayout(getContext());
        parentGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        parentGroup.setOrientation(LinearLayout.HORIZONTAL);
        parentGroup.setBackgroundColor(colorBg);
        parentGroup.setTranslationZ(translationZ);
        addView(parentGroup);
        viewCollectiveList=new ArrayList<>();
    }

    public void setTextList(List<String> textList) {
        this.textList = textList;
    }


    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();

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

    private void addTabLayout(int size, int showMode) {
        int width = calculativeWidth(size, showMode);
        for (int i = 0; i < size; i++) {
            parentGroup.addView(getTabLayout(width, i));
        }
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
        viewCollectiveList.add(new ViewCollective(layoutGroup,textView,lineView,bmMarkView));
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
        textView.setTextSize(textSizeUnCheck);
        textView.setLayoutParams(params);
        textView.setId(position);
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
        markView.setTag(textView.getId());
        markView.setLayoutParams(params);
        return markView;
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
}
