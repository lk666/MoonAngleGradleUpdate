package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc      可根据待评价与已评价的数据模型结构来评估是否可共用此adapter
 */
public class TaskEvaluateStatusAdapter extends BaseAdapter {
    private List<Object> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;

    public TaskEvaluateStatusAdapter(Context cxt, List<Object> datas) {
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
        MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sz_fragment_task_evaluate_status_item, null);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        viewHolder.getUserTaskLv().setDividerHeight(0);
        viewHolder.getUserTaskLv().setDivider(null);
        ///*************************************显示数据************************************************/
        //TODO 模拟数据
        List<Object> list = new ArrayList<>();
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        TaskEvaluateStatusChildAdapter adapter = new TaskEvaluateStatusChildAdapter(cxt, list);
        viewHolder.getUserTaskLv().setAdapter(adapter);
        setListViewHeight(viewHolder.getUserTaskLv());

        ///*************************************设置监听器************************************************/
        return convertView;
    }

    public class MyViewHolder {
        @Bind(R.id.user_avatar_iv)
        RoundImageView user_avatar_iv;//用户头像

        @Bind(R.id.user_name_tv)
        TextView user_name_tv;//用户名

        @Bind(R.id.user_score_tv)
        TextView user_score_tv;//用户绩效分

        @Bind(R.id.user_avaliabel_time_tv)
        TextView user_avaliabel_time_tv;//用户实际工作时间

        @Bind(R.id.user_date_tv)
        TextView user_date_tv;//用户任务时间

        @Bind(R.id.user_task_lv)
        ListView user_task_lv;

        public MyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public RoundImageView getUserAvatarIv() {
            return user_avatar_iv;
        }

        public TextView getUserNameTv() {
            return user_name_tv;
        }

        public TextView getUserScoreTv() {
            return user_score_tv;
        }

        public TextView getUserAvaliabelTimeTv() {
            return user_avaliabel_time_tv;
        }

        public TextView getUserDateTv() {
            return user_date_tv;
        }

        public ListView getUserTaskLv() {
            return user_task_lv;
        }
    }

    /**
     * 设置Listview的高度
     */
    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
