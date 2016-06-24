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
 * 最后一项为添加照片的adapter，当达到最大数目时，隐藏最后一项
 * Created by lk on 2016/6/15.
 */
public class AddPhotoAdapter extends BaseListAdapter<ClothingPic> {
    /**
     * 标记最后一个添加按钮
     */
    public final static String ADD_IMG_ID = "ADD_IMG_ID";

    public AddPhotoAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_deletable_pic;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        final ClothingPic pic = (ClothingPic) getItem(position);
        if (pic == null) {
            return;
        }

        ImageView ivPic = ViewHolder.get(convertView, R.id.iv_pic);
        ImageView ivDelete = ViewHolder.get(convertView, R.id.iv_delete);
        ImageView ivAdd = ViewHolder.get(convertView, R.id.iv_add);
        TextView tvAdd = ViewHolder.get(convertView, R.id.tv_add);

        // 添加相片按钮
        if (pic.getImgId().equals(ADD_IMG_ID)) {
            ivPic.setImageResource(R.drawable.btn_border_grep_shape4);
            ivDelete.setVisibility(View.GONE);
            ivAdd.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.VISIBLE);
        }

        // 已上传图片
        else {
            ImageLoaderUtil.displayImage(context, pic.getImgPath(), ivPic);
            ivDelete.setVisibility(View.VISIBLE);
            ivAdd.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
        }

        setClickEvent(isNew, position, ivPic, ivDelete);
    }

    /**
     * 获取图片IDs，多个用逗号隔开，如：3232,3234223
     *
     * @return
     */
    public String getAllIdsString() {
        StringBuilder builder = new StringBuilder("");
        for (ClothingPic pic : list) {
            if (!TextUtils.isEmpty(pic.getImgId())) {
                builder.append(",").append(pic.getImgId());
            }
        }
        String ids = builder.toString();
        if (!TextUtils.isEmpty(ids)) {
            ids = ids.substring(1);
        }
        return ids;
    }
}