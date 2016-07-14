package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.LaundryLog;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;

/**
 * Created by allenli on 2016/6/28.
 */
public class DeliverLogAdapter extends BaseListAdapter<LaundryLog> {

    public DeliverLogAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_laugh_log;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        final LaundryLog deliver = (LaundryLog) getItem(position);
        if (deliver == null) {
            return;
        }

        TextView tvContent = ViewHolder.get(convertView, R.id.txt_content);

        tvContent.setText(String.format("%s,%s,%s\n%s", deliver.getNodeName(),deliver.getPhone()
                ,deliver.getAction(),DateUtil.getTime(deliver.getOpTime(), "yyyy-MM-dd HH:mm:ss"))
               );

    }
}
