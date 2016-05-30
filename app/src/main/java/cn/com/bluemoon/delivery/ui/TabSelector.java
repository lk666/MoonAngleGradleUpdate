package cn.com.bluemoon.delivery.ui;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * Created by liangjiangli on 2016/4/19.
 */

public class TabSelector extends LinearLayout {

    private int width;
    private float density;
    private int currentTab = 0;
    private ImageView imgBar;
    private LinearLayout lineLayout;
    private FrameLayout frameLayout;
    private int tagCount;
    private int marginLeft;
    private int marginRight;
    private List<TextView> txtViews = new ArrayList<TextView>();
    private int selectColor;
    private int normalColor;
    private CallBackListener listener;

    public TabSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutInflater.inflate(R.layout.layout_tab_selector2, this);
        screenDefault();

        lineLayout = (LinearLayout) this.findViewById(R.id.line_layout);
        frameLayout  = (FrameLayout)this.findViewById(R.id.frame_layout);
        imgBar = (ImageView) this.findViewById(R.id.img_bar);


        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TabSelector);
        initWidget(typedArray);
    }

    @SuppressLint("ResourceAsColor")
    private void initWidget(TypedArray typedArray) {

        String text = typedArray.getString(R.styleable.TabSelector_text);
        selectColor = typedArray.getColor(R.styleable.TabSelector_color_select_text,
                getContext().getResources().getColor(R.color.text_red));
        normalColor = typedArray.getColor(R.styleable.TabSelector_color_normal_text,
                getContext().getResources().getColor(R.color.text_black_light));
        float textSize = typedArray.getDimension(R.styleable.TabSelector_text_size, 15);
        marginLeft = typedArray.getDimensionPixelOffset(R.styleable.TabSelector_margin_left, getPx(10));
        marginRight = typedArray.getDimensionPixelOffset(R.styleable.TabSelector_margin_right, getPx(10));

        android.widget.LinearLayout.LayoutParams p = new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        p.setMargins(marginLeft, 0, marginRight, 0);
        frameLayout.setLayoutParams(p);


        if (text != null && !"".equals(text.trim())) {
            String[] tag = text.split("，");
            tagCount = tag.length;
            int i = 0;
            for (String s : tag) {
                TextView txtView = new TextView(getContext());
                txtView.setText(s);
                txtView.setTextSize(textSize);
                txtView.setTextColor(normalColor);
                android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;

                final int index = i;
                if (index == 0) {
                    txtView.setTextColor(selectColor);
                }
                txtView.setLayoutParams(params);
                txtView.setGravity(Gravity.CENTER);
                txtView.setLayoutParams(new LinearLayout.LayoutParams((width - marginLeft - marginRight) / tagCount,
                        LayoutParams.MATCH_PARENT));
                lineLayout.addView(txtView, i);
                txtViews.add(txtView);
                txtView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        barChange(index);
                        listener.onClick(currentTab);
                    }
                });

                i++;
            }
        }
        if (tagCount != 0)
        imgBar.setLayoutParams(new LinearLayout.LayoutParams((width - marginLeft - marginRight) / tagCount,
                LayoutParams.MATCH_PARENT));

    }

    public void barChange(int index) {
        if (txtViews.size() > 0) {
            for (int i = 0; i < txtViews.size(); i++) {
                TextView v = txtViews.get(i);
                if (i == index) {
                    v.setTextColor(selectColor);
                } else {
                    v.setTextColor(normalColor);
                }
            }
        }

        int mCurrentLeft = currentTab * (width - marginLeft - marginRight) / tagCount;
        int mToLeft = index * (width - marginLeft - marginRight) / tagCount;

        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation;
        translateAnimation = new TranslateAnimation(mCurrentLeft, mToLeft, 0f, 0f);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillBefore(true);
        animationSet.setFillAfter(true);
        animationSet.setDuration(300);
        imgBar.startAnimation(animationSet);
        currentTab = index;
    }

    private void screenDefault() {
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(dm);

        density = dm.density;
        width = AppContext.getInstance().getDisplayWidth();
    }

    public int getPx(int dip) {
        return (int) (dip * density);
    }

    public void setOnClickListener(final CallBackListener listener) {
        this.listener = listener;
    }

    public interface CallBackListener {

        void onClick(int currentTab);
    }

}