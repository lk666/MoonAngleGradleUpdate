package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc       ${TODO}
 */
public class TaskOrEvaluateDetailAdapter extends BaseAdapter {

    private List<Object> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;

    public TaskOrEvaluateDetailAdapter(Context cxt, List<Object> datas) {
        this.cxt = cxt;
        this.datas = datas;
        if (inflater == null && cxt != null) {
            inflater = LayoutInflater.from(cxt);
        }
    }

    public void updateAdapter(List<Object> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sz_activity_task_or_evaluate_detail_item, null);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        ///*************************************显示数据************************************************/

        return convertView;
    }

    class MyViewHolder {
        public MyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
