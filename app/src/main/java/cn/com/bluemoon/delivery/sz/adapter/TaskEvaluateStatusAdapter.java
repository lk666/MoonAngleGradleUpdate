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
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoBean;
import cn.com.bluemoon.delivery.sz.taskManager.SzTaskEvaluateStatusFragment;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc      可根据待评价与已评价的数据模型结构来评估是否可共用此adapter
 */
public class TaskEvaluateStatusAdapter extends BaseAdapter {
    private List<DailyPerformanceInfoBean> mDatas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mCxt;
    private int activityType = 0;//记录需要展示的类型（0;待评价  1;已评价）

    public TaskEvaluateStatusAdapter(Context cxt, List<DailyPerformanceInfoBean> datas, int dataType) {
        this.mCxt = cxt;
        if (dataType == 0 || dataType == 1) {
            this.activityType = dataType;
        }
        if (inflater == null && cxt != null) {
            inflater = LayoutInflater.from(mCxt);
        }
        if (datas == null) {
            mDatas.clear();
        } else {
            this.mDatas.clear();
            this.mDatas.addAll(datas);
        }
    }

    public void updateAdapter(List<DailyPerformanceInfoBean> datas) {
        if (datas == null) {
            mDatas.clear();
        } else {
            this.mDatas.clear();
            this.mDatas.addAll(datas);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public DailyPerformanceInfoBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DailyPerformanceInfoBean itemBean = mDatas.get(position);
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
        //显示被评价人的头像和名字
        UserInfoBean user = itemBean.getUser();
        if (user != null) {
            ImageLoaderUtil.displayImage(user.getUAvatar(), viewHolder.getUserAvatarIv(), R.mipmap.sz_default_user_icon, R.mipmap.sz_default_user_icon,
                    R.mipmap.sz_default_user_icon);
            viewHolder.getUserNameTv().setText(user.getUName());
        }
        //工作日期
        viewHolder.getUserDateTv().setText(itemBean.getWork_date());

        //得分
        if (activityType == SzTaskEvaluateStatusFragment.ACTIVITY_TYPE_HAVE_EVALUATED) {
            //已评价
            //有效工作时间（单位：分钟）
            viewHolder.getUserAvaliabelTimeTv().setText(itemBean.getDay_valid_min());
            viewHolder.getUserScoreTv().setText(itemBean.getDay_score() + mCxt.getString(R.string.sz_task_quality_score_unit));
            viewHolder.getUserScoreTv().setTextColor(mCxt.getResources().getColor(R.color.red));
        } else if (activityType == SzTaskEvaluateStatusFragment.ACTIVITY_TYPE_TO_EVALUATE) {
            //未评价
            //有效工作时间（单位：分钟）
            viewHolder.getUserAvaliabelTimeTv().setText(itemBean.getDay_usage_time());
            viewHolder.getUserScoreTv().setText(mCxt.getString(R.string.sz_evaluate_not_valid_label));
            viewHolder.getUserScoreTv().setTextColor(mCxt.getResources().getColor(R.color.red));
        }
        //任务列表
        TaskEvaluateStatusChildAdapter adapter = new TaskEvaluateStatusChildAdapter(mCxt, itemBean.getAsignJobs());
        viewHolder.getUserTaskLv().setAdapter(adapter);
        setListViewHeight(viewHolder.getUserTaskLv());
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
