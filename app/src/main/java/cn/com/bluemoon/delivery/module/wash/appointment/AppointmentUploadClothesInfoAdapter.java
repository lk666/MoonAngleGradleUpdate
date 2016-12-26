package cn.com.bluemoon.delivery.module.wash.appointment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.UploadAppointClothesInfo;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;

/**
 * 收衣明细Adapter
 * Created by lk on 2016/12/24.
 */

class AppointmentUploadClothesInfoAdapter extends BaseListAdapter<UploadAppointClothesInfo> {
    AppointmentUploadClothesInfoAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_with_order_clothes_info;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        UploadAppointClothesInfo item = (UploadAppointClothesInfo) getItem(position);
        if (item == null) {
            return;
        }

        ImageView ivClothImg = ViewHolder.get(convertView, R.id.iv_cloth_img);
        TextView tvClothesCode = ViewHolder.get(convertView, R.id.tv_clothes_code);
        TextView tvTypeName = ViewHolder.get(convertView, R.id.tv_type_name);
        TextView tvClothesName = ViewHolder.get(convertView, R.id.tv_clothes_name);

        tvClothesCode.setText(item.getClothesCode());
        tvTypeName.setText(item.getOneLevelName());
        tvClothesName.setText(item.getWashName());

        ImageLoaderUtil.displayImage(item.getImgPath(), ivClothImg);

        setClickEvent(isNew, position, convertView);
    }
}
