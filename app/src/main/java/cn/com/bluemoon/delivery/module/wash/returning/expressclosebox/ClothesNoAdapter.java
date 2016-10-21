package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;

/**
 * Created by ljl on 2016/9/29.
 */
public class ClothesNoAdapter extends BaseListAdapter<String> {

    public ClothesNoAdapter(Context context) {
        super(context, null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_item_return_clothes_number;
    }

    @Override
    protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
        TextView txtNo = getViewById(R.id.txt_no);
        TextView txtIndex = getViewById(R.id.txt_index);
        View line = getViewById(R.id.line_view);
        txtIndex.setText(String.valueOf(position + 1));
        txtNo.setText(list.get(position));
        if (position == list.size() - 1) {
            line.setVisibility(View.GONE);
        } else {
            line.setVisibility(View.VISIBLE);
        }
    }
}
