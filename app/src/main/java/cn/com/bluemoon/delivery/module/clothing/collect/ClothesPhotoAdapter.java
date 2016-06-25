package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;

/**
 * 衣物图片Adapter
 * Created by lk on 2016/6/24.
 */
public class ClothesPhotoAdapter extends BaseListAdapter<ClothingPic> {

    public ClothesPhotoAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_clothes_pic;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        final ClothingPic pic = (ClothingPic) getItem(position);
        if (pic == null) {
            return;
        }

        ImageView ivPic = ViewHolder.get(convertView, R.id.iv_pic);

        ImageLoaderUtil.displayImage(context, pic.getImgPath(), ivPic);

        setClickEvent(isNew, position, ivPic);
    }
}
