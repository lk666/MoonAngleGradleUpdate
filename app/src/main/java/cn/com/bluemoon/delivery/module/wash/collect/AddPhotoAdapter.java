package cn.com.bluemoon.delivery.module.wash.collect;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;

/**
 * 最后一项为添加照片的adapter，当达到最大数目时，隐藏最后一项
 * Created by lk on 2016/6/15.
 */
public class AddPhotoAdapter extends BaseListAdapter<ClothingPic> {
    /**
     * 标记最后一个添加按钮
     */
    public final static String ADD_IMG_ID = "ADD_IMG_ID";

    /**
     * 提示信息，默认为:(最多10张)
     */
    private String addMsg;

    public AddPhotoAdapter(Context context, OnListItemClickListener listener) {
        this(context, listener, null);
    }

    public AddPhotoAdapter(Context context, OnListItemClickListener listener, String addMsg) {
        super(context, listener);
        if (addMsg != null) {
            this.addMsg = addMsg;
        } else {
            this.addMsg = context.getString(R.string.clothing_book_in_phote_most);
        }
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
        View rlAdd = ViewHolder.get(convertView, R.id.rl_add);
        TextView tv = ViewHolder.get(convertView, R.id.tv_msg);

        // 添加相片按钮
        if (pic.getImgId().equals(ADD_IMG_ID)) {
            rlAdd.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.GONE);
            ivPic.setVisibility(View.GONE);
        }

        // 已上传图片
        else {
            ImageLoaderUtil.displayImage(pic.getImgPath(), ivPic);

            rlAdd.setVisibility(View.GONE);
            ivDelete.setVisibility(View.VISIBLE);
            ivPic.setVisibility(View.VISIBLE);
        }

        if (isNew) {
            tv.setText(addMsg);
        }

        setClickEvent(isNew, position, ivPic, ivDelete, rlAdd);
    }

    /**
     * 获取图片IDs，多个用逗号隔开，如：3232,3234223
     */
    public String getAllIdsString() {
        StringBuilder builder = new StringBuilder("");
        for (ClothingPic pic : list) {
            String id = pic.getImgId();
            if (!TextUtils.isEmpty(id) && !ADD_IMG_ID.equals(id)) {
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
