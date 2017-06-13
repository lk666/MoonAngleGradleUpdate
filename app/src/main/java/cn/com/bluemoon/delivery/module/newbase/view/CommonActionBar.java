package cn.com.bluemoon.delivery.module.newbase.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

// TODO: lk 2016/12/12 抽象出titlebar，代码添加进activity中，而不是xml中，方便更改

/**
 * 最常用的标题栏，包括一个左按钮（文本或图片）、居中文本和一个右按钮（文本或图片），默认为左边返回按钮
 *
 * @author LK
 */
public class CommonActionBar extends BaseTitleBar implements View.OnClickListener {

    private TextView tvTitle;
    private ImageView imgLeft;
    private TextView tvLeft;
    private ImageView imgRight;
    private TextView tvRight;
    private IActionBarListener listener;

    public CommonActionBar(Context context) {
        super(context);
        init();
    }

    public CommonActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setListener(IActionBarListener listener) {
        this.listener = listener;
    }

    /**
     * 双击标题栏事件
     */
    public interface OnTitleBarClickLister {
        void onDoubleClick();
    }

    private OnTitleBarClickLister onTitleBarClickLister;

    public void setDoubleClickListener(OnTitleBarClickLister listener) {
        this.onTitleBarClickLister = listener;
    }

    GestureDetector gestureDetector;

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.title_bar, this, true);

        tvTitle = (TextView) findViewById(R.id.tv_tbb_title);
        imgLeft = (ImageView) findViewById(R.id.img_back);
        imgRight = (ImageView) findViewById(R.id.img_right);
        tvRight = (TextView) findViewById(R.id.tv_right);
        tvLeft = (TextView) findViewById(R.id.tv_back);

        imgLeft.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        tvLeft.setVisibility(GONE);
        tvRight.setVisibility(GONE);
        imgRight.setVisibility(GONE);

        gestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (onTitleBarClickLister != null) {
                            onTitleBarClickLister.onDoubleClick();
                            return true;
                        } else {
                            return super.onSingleTapConfirmed(e);
                        }
                    }
                });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector != null) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
                return false;
            }
        });
    }


    public TextView getTitleView() {
        return tvTitle;
    }

    public ImageView getImgLeftView() {
        return imgLeft;
    }

    public ImageView getImgRightView() {
        return imgRight;
    }

    public TextView getTvRightView() {
        return tvRight;
    }

    public TextView getTvLeftView() {
        return tvLeft;
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

    /**
     * 设置为沉浸式
     */
    public void setImmerse() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);

            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height += statusBarHeight;
            setLayoutParams(lp);
        }
    }
}
