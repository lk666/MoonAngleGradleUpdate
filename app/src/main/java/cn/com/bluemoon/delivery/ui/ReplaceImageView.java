package cn.com.bluemoon.delivery.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.com.bluemoon.delivery.R;

/**
 * 自定义图片
 * Created by bm on 2017/9/15.
 */

public class ReplaceImageView extends ImageView {

    private int placeholderId;
    private boolean isLarge;

    public ReplaceImageView(Context context) {
        super(context);
        init(null);
    }

    public ReplaceImageView(Context context, boolean isLarge) {
        super(context);
        this.isLarge = isLarge;
        init(null);
    }

    public ReplaceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        isLarge(isLarge);
        initAttrs(attrs);
        setScaleType(ScaleType.FIT_XY);
        setImageResource(placeholderId);

    }

    /**
     * 初始化属性值
     */
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .ReplaceImageView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            if (attr == R.styleable.ReplaceImageView_img_isLarge) {
                isLarge(attribute.getBoolean(attr, false));
            } else if (attr == R.styleable.ReplaceImageView_img_placeholder) {
                int resId = attribute.getResourceId(R.styleable
                        .ReplaceImageView_img_placeholder, 0);
                if (resId != 0) {
                    placeholder(resId);
                }
            }
        }
        attribute.recycle();
    }

    /**
     * 设置是否是大的占位图
     */

    public ReplaceImageView isLarge(boolean isLarge) {
        placeholderId = isLarge ? R.drawable.ic_image_loading : R.drawable.ic_image_loading_s;
        return this;
    }

    /**
     * 自定义占位图
     */
    public ReplaceImageView placeholder(int resId) {
        placeholderId = resId;
        return this;
    }

    /**
     * 设置网络图片
     */
    public void setImageUrl(String url) {
        if(getContext() instanceof Activity){
            if(((Activity)getContext()).isFinishing()){
                return;
            }
        }
        Glide.with(getContext())
                .load(url)
                .placeholder(placeholderId)
                .error(placeholderId)
                .crossFade()
                .centerCrop()
                .dontAnimate()
                .into(this);
    }
}
