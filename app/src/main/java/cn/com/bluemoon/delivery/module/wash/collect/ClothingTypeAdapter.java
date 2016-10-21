package cn.com.bluemoon.delivery.module.wash.collect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OrderDetail;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.ViewHolder;

/**
 * 衣物类型（服务类型）adapter
 * Created by allenli on 2016/6/28.
 */
public class ClothingTypeAdapter extends BaseListAdapter<OrderDetail> {

    public ClothingTypeAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_clothing_type_text;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        final OrderDetail deliver = (OrderDetail) getItem(position);
        if (deliver == null) {
            return;
        }

        TextView txtType = ViewHolder.get(convertView, R.id.txt_type);
        TextView txtCount = ViewHolder.get(convertView, R.id.txt_count);
        txtType.setText(deliver.getTypeName());
        txtCount.setText(String.format(context.getString(R.string.text_count_format), deliver
                .getReceivableCount()));

    }
}
