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

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;

/**
 * Created by jiangyh
 * Date       2016/9/8
 * Desc     首页通过时期获取到的List列表
 */
public class TaskDateStatusAdapter extends BaseAdapter {
    private List<DailyPerformanceInfoBean> infoBeans = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;

    public TaskDateStatusAdapter(Context cxt, List<DailyPerformanceInfoBean> infoBeans) {
        this.cxt = cxt;
        this.infoBeans = infoBeans;
        if (inflater == null && cxt != null) {
            inflater = LayoutInflater.from(cxt);
        }
    }

    public void updateAdapter(List<DailyPerformanceInfoBean> infoBeans) {
        this.infoBeans = infoBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return infoBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return infoBeans.get(position);
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

        DailyPerformanceInfoBean dailyPerformanceInfoBean=infoBeans.get(position);

        /**@author jiangyh */

//        infoBean.setAsignJobs(asignJobBeanList);
//        infoBean.setCreatetime("2016-09-10");
//        infoBean.setDay_valid_min("120");
//        infoBean.setDay_score("9");

        viewHolder.getUserDateTv().setText(dailyPerformanceInfoBean.getCreatetime());
        viewHolder.getUserScoreTv().setText(dailyPerformanceInfoBean.getDay_score());
        viewHolder.getUserAvaliabelTimeTv().setText(dailyPerformanceInfoBean.getDay_valid_min());


        List<AsignJobBean> asignJobs=dailyPerformanceInfoBean.getAsignJobs();


        TaskEvaluateStatusChildAdapter adapter = new TaskEvaluateStatusChildAdapter(cxt, asignJobs);
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
