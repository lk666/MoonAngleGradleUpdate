package cn.com.bluemoon.delivery.module.base;

import android.content.Context;

import com.youth.banner.loader.ImageLoaderInterface;

import cn.com.bluemoon.delivery.ui.ReplaceImageView;

/**
 * Created by bm on 2017/10/10.
 */

public abstract class ReplaceImageLoader implements ImageLoaderInterface<ReplaceImageView> {
    @Override
    public ReplaceImageView createImageView(Context context) {
        return new ReplaceImageView(context, true);
    }
}
