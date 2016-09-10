package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc      待评价与已评价的子listview(任务序号+任务概述)数据adapter
 */
public class TaskEvaluateStatusChildAdapter extends BaseAdapter {
    private List<AsignJobBean> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;

    public TaskEvaluateStatusChildAdapter(Context cxt, List<AsignJobBean> datas) {
        this.cxt = cxt;
        this.datas = datas;
        if (inflater == null && cxt != null) {
            inflater = LayoutInflater.from(cxt);
        }
    }

    public void updateAdapter(List<AsignJobBean> datas) {
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
        MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sz_fragment_task_status_childitem, null);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        ///*************************************显示数据************************************************/
        if (position < (datas.size() - 1)) {
            viewHolder.getDivider().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getDivider().setVisibility(View.GONE);
        }

        /**@author jiangyh */
//        任务记录首页item 展示
        AsignJobBean asignJobBean=datas.get(position);

        viewHolder.getTaskRankNumTv().setText(asignJobBean.getTask_cont());

        return convertView;
    }

    public class MyViewHolder {

        @Bind(R.id.task_rank_num_tv)
        TextView task_rank_num_tv;//用户任务序号

        @Bind(R.id.task_content)
        TextView task_content;//用户任务描述

        @Bind(R.id.divider)
        View divider;//分割线

        public MyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public TextView getTaskRankNumTv() {
            return task_rank_num_tv;
        }

        public TextView getTaskContent() {
            return task_content;
        }

        public View getDivider() {
            return divider;
        }
    }
}
