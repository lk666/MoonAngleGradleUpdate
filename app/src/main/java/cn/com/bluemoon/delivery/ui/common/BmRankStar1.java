package cn.com.bluemoon.delivery.ui.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * 可操作
 * Created by tangqiwei on 2017/5/31.
 */

public class BmRankStar1 extends LinearLayout {

    private TextView titleView;
    private RatingBar ratingBar;

    private int titleSize;//字体大小
    private int titleColor;//字体颜色
    private int progressDrawable;//图片列表
    private int backgroundDrawable;//图片列表
    private float rating;//初始值
    private String titleText;//标题


    public BmRankStar1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getInitData(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BmRankStar1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getInitData(attrs);
        init();
    }

    public BmRankStar1(Context context, AttributeSet attrs) {
        super(context, attrs);
        getInitData(attrs);
        init();
    }

    public BmRankStar1(Context context) {
        super(context);
        getInitData();
        init();
    }

    private void getInitData() {
        getInitData(null);
    }

    private void getInitData(AttributeSet attrs) {
        titleSize=sp2px(11);
        titleColor= Color.parseColor("#666666");
        rating=0;
//        progressDrawable=R.drawable.star_1_progress;
//        backgroundDrawable=R.drawable.star_1_background;
        if(attrs!=null){
            TypedArray typedArray=getContext().obtainStyledAttributes(attrs,R.styleable.BmRankStar1);
            int n = typedArray.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.BmRankStar1_rank_star1_titleSize:
                        titleSize=typedArray.getDimensionPixelSize(attr,sp2px(11));
                        break;
                    case R.styleable.BmRankStar1_rank_star1_titleColor:
                        titleColor=typedArray.getColor(attr,Color.parseColor("#666"));
                        break;
                    case R.styleable.BmRankStar1_rank_star1_progressDrawable:
                        progressDrawable=typedArray.getResourceId(attr,R.drawable.star_1_progress);
                        break;
                    case R.styleable.BmRankStar1_rank_star1_backgroundDrawable:
                        backgroundDrawable=typedArray.getResourceId(attr,R.drawable.star_1_background);
                        break;
                    case R.styleable.BmRankStar1_rank_star1_rating:
                        rating=typedArray.getFloat(attr,0);
                        break;
                    case R.styleable.BmRankStar1_rank_star1_titleText:
                        titleText=typedArray.getText(attr).toString();
                        break;
                }
            }
            typedArray.recycle();
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_rank_star1,this,true);
        ratingBar= (RatingBar) findViewById(R.id.rb);
        titleView= (TextView) findViewById(R.id.title);
        initView();
    }

    private LayerDrawable getLayerDrawable(int progressId,int backgroundId){
        Drawable progress=getResources().getDrawable(progressId);
        Drawable background=getResources().getDrawable(backgroundId);
        Drawable[] layers={background,background,progress};
        LayerDrawable layerDrawable=new LayerDrawable(layers);
        layerDrawable.setId(0,android.R.id.background);
        layerDrawable.setId(1,android.R.id.secondaryProgress);
        layerDrawable.setId(2,android.R.id.progress);
        return layerDrawable;
    }

    public void initView(){
        titleView.setTextColor(titleColor);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
        titleView.setText(titleText);
        if(progressDrawable*backgroundDrawable!=0){
            ratingBar.setProgressDrawable(getLayerDrawable(progressDrawable,backgroundDrawable));
        }
        ratingBar.setRating(rating);
    }


    public BmRankStar1 setTitleSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public BmRankStar1 setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public BmRankStar1 setProgressBackgroundDrawable(int progressDrawable,int backgroundDrawable) {
        this.progressDrawable = progressDrawable;
        this.backgroundDrawable=backgroundDrawable;
        return this;
    }

    public float getRating() {
        return ratingBar.getRating();
    }

    public BmRankStar1 setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public String getTitleText() {
        return titleView.getText().toString();
    }

    public BmRankStar1 setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
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
