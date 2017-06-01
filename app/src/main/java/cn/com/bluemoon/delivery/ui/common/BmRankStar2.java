package cn.com.bluemoon.delivery.ui.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * 只做显示
 * Created by tangqiwei on 2017/5/31.
 */

public class BmRankStar2 extends LinearLayout {

    private RatingBar ratingBar;
    private TextView titleView;
    private LinearLayout layout;
    private int titleSize;//字体大小
    private int titleColor;//字体颜色
    private int progressDrawable;//图片列表
    private int backgroundDrawable;//图片列表
    private int height;//格子高度
    private int rating;//星星个数
    private String titleText;//标题

    public BmRankStar2(Context context) {
        super(context);
        getInitData();
        init();
    }

    public BmRankStar2(Context context, AttributeSet attrs) {
        super(context, attrs);
        getInitData(attrs);
        init();
    }

    public BmRankStar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getInitData(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BmRankStar2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getInitData(attrs);
        init();
    }
    private void getInitData() {
        getInitData(null);
    }
    private void getInitData(AttributeSet attrs) {
        titleSize=sp2px(11);
        titleColor= Color.parseColor("#666666");
        height=dip2px(50);
        rating=5;
        if(attrs!=null){
            TypedArray typedArray=getContext().obtainStyledAttributes(attrs,R.styleable.BmRankStar2);
            int n = typedArray.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.BmRankStar2_rank_star2_titleSize:
                        titleSize=typedArray.getDimensionPixelSize(attr,sp2px(11));
                        break;
                    case R.styleable.BmRankStar2_rank_star2_titleColor:
                        titleColor=typedArray.getColor(attr,Color.parseColor("#666"));
                        break;
                    case R.styleable.BmRankStar2_rank_star2_progressDrawable:
                        progressDrawable=typedArray.getResourceId(attr,R.drawable.star_2);
                        break;
                    case R.styleable.BmRankStar2_rank_star2_backgroundDrawable:
                        backgroundDrawable=typedArray.getResourceId(attr,R.drawable.star_2);
                        break;
                    case R.styleable.BmRankStar2_rank_star2_rating:
                        rating=typedArray.getInteger(attr,0);
                        break;
                    case R.styleable.BmRankStar2_rank_star2_titleText:
                        titleText=typedArray.getText(attr).toString();
                        break;
                    case R.styleable.BmRankStar2_rank_star2_height:
                        height=typedArray.getDimensionPixelSize(attr,dip2px(50));
                        break;
                }
            }
            typedArray.recycle();
        }

    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_rank_star2,this,true);
        ratingBar= (RatingBar) findViewById(R.id.rb);
        titleView= (TextView) findViewById(R.id.title);
        layout= (LinearLayout) findViewById(R.id.llayout);
        initView();
    }
    public void initView(){
        titleView.setTextColor(titleColor);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
        titleView.setText(titleText);
        ViewGroup.LayoutParams laoyutParams=layout.getLayoutParams();
        laoyutParams.height=height;
        layout.setLayoutParams(laoyutParams);
        ratingBar.setNumStars(rating);
        ratingBar.setRating(rating);
    }

    public BmRankStar2 setTitleSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public BmRankStar2 setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public BmRankStar2 setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public BmRankStar2 setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public BmRankStar2 setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public String getTitleText() {
        return titleText;
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
