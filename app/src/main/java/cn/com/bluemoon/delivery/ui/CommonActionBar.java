package cn.com.bluemoon.delivery.ui;

import android.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;

public class CommonActionBar implements OnClickListener {

    private ActionBar actionBar;
    private TextView tvTitle;
    private ImageView imgLeft;
    private TextView tvLeft;
    private ImageView imgRight;
    private TextView tvRight;
    private IActionBarListener listener;

    public CommonActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
        init();
    }

    public CommonActionBar(ActionBar actionBar, IActionBarListener listener) {
        this.actionBar = actionBar;
        this.listener = listener;
        init();
    }

    public void init() {
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.top_back_center_bar);

        tvTitle = (TextView) actionBar.getCustomView().findViewById(
                R.id.tv_tbb_title);
        listener.setTitle(tvTitle);
        imgLeft = (ImageView) actionBar.getCustomView().findViewById(R.id.img_back);
        tvLeft = (TextView) actionBar.getCustomView().findViewById(R.id.tv_back);
        imgRight = (ImageView) actionBar.getCustomView().findViewById(R.id.img_right);
        tvRight = (TextView) actionBar.getCustomView().findViewById(R.id.tv_right);
        imgLeft.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        tvRight.setOnClickListener(this);
    }

    public TextView getTitleView() {
        return tvTitle;
    }

    public ImageView getImgLeftView() {
        return imgLeft;
    }

    public TextView getTvLeftView() {
        return tvLeft;
    }

    public ImageView getImgRightView() {
        return imgRight;
    }

    public TextView getTvRightView() {
        return tvRight;
    }


    @Override
    public void onClick(View v) {
        if (listener != null) {
            if (v == imgLeft || v == tvLeft) {
                listener.onBtnLeft(v);
            } else if (v == imgRight || v == tvRight) {
                listener.onBtnRight(v);
            }
        }

    }
}
