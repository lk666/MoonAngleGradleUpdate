package cn.com.bluemoon.delivery.module.clothing.collect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.kymjs.kjframe.utils.SystemTool;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.KJFUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;

/**
 * 收衣明细Adapter
 * Created by lk on 2016/6/23.
 */
public class ClothesInfoAdapter extends BaseListAdapter<ClothesInfo> {
    public ClothesInfoAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_with_order_clothes_info;
    }

    @SuppressLint({"NewApi"})
    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        final ClothesInfo item = (ClothesInfo) getItem(position);
        if (item == null) {
            return;
        }

        ImageView ivClothImg = ViewHolder.get(convertView, R.id.iv_cloth_img);
        TextView tvClothesCode = ViewHolder.get(convertView, R.id.tv_clothes_code);
        TextView tvTypeName = ViewHolder.get(convertView, R.id.tv_type_name);
        TextView tvClothesName = ViewHolder.get(convertView, R.id.tv_clothes_name);

        tvClothesCode.setText(item.getClothesCode());
        tvTypeName.setText(item.getTypeName());
        tvClothesName.setText(item.getClothesName());
        // TODO: lk 2016/7/7 确认收衣的adapter应额外写 

           // ImageLoaderUtil.displayImage(context, item.getImgPath(), ivClothImg);
       // }

        if (item.isCheck()) {
            Drawable drawable = context.getResources().getDrawable(R.mipmap.scaned);
            ivClothImg.setImageDrawable(drawable);
            Bitmap backDrawable = KJFUtil.getUtil().getKJB().getMemoryCache(item.getImgPath());

            if(null!=backDrawable) {
                if (SystemTool.getSDKVersion() >= 16) {
                    ivClothImg.setBackground(new BitmapDrawable(ivClothImg.getResources(), backDrawable));
                } else {
                    ivClothImg.setBackgroundDrawable(new BitmapDrawable(ivClothImg.getResources(), backDrawable));
                }
            }

        }else{
            KJFUtil.getUtil().getKJB().display(ivClothImg,item.getImgPath());
        }
        setClickEvent(isNew, position, convertView);
    }
}