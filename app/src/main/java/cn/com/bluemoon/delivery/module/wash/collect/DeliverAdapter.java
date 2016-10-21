package cn.com.bluemoon.delivery.module.wash.collect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesDeliverInfo;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;

/**
 * 衣物转交adapter
 * Created by allenli on 2016/6/28.
 */
public class DeliverAdapter extends BaseListAdapter<ClothesDeliverInfo> {

    public DeliverAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_deliver_log;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        final ClothesDeliverInfo deliver = (ClothesDeliverInfo) getItem(position);
        if (deliver == null) {
            return;
        }

        TextView tvContent = ViewHolder.get(convertView, R.id.txt_content);

        tvContent.setText(String.format("%s,%s,%s,%s\n%s", DateUtil.getTime(deliver.getTransmitTime(), "yyyy-MM-dd HH:mm:ss"),
                deliver.getReceiverCode(),
                deliver.getReceiverName(),
                deliver.getReceiverPhone(),
                deliver.getRefusalReason()));

    }
}
