package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
 * Desc       ${TODO}
 */
public class TaskOrEvaluateDetailAdapter extends BaseAdapter {

    private List<AsignJobBean> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;
    /**0：//任务详情 1：任务评价详情*/
    private int showType=0;
    public static final int ACTIVITY_TYPE_TASK_DETAIL = 0;//任务详情
    public static final int ACTIVITY_TYPE_EVALUATE_DETAIL = 1;//任务评价详情

    public TaskOrEvaluateDetailAdapter(Context cxt,int showType, List<AsignJobBean> datas) {
        this.cxt = cxt;
        this.showType = showType;
        if (datas == null) {
            this.datas.clear();
        } else {
            this.datas = datas;
        }
        if (inflater == null && cxt != null) {
            inflater = LayoutInflater.from(cxt);
        }
    }

    public void updateAdapter(List<AsignJobBean> datas) {
        if (datas == null) {
            this.datas.clear();
        } else {
            this.datas = datas;
        }
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

        /**任务详情 @author jiangyh*/
        if (showType==ACTIVITY_TYPE_TASK_DETAIL){
//            业绩以下没有 taskAvailabelLl ,ll_task_quality, ll_task_evaluate_content
            viewHolder.getTaskAvailabelLl().setVisibility(View.GONE);
            viewHolder.getTaskQualityLl().setVisibility(View.GONE);
            viewHolder.getTaskEvaluateContentLl().setVisibility(View.GONE);

            AsignJobBean asignJobBean=datas.get(position);
            viewHolder.getTaskContentTv().setText(asignJobBean.getTask_cont());
            viewHolder.getTaskOutputTv().setText(asignJobBean.getProduce_cont());
            viewHolder.getTaskStartEndtimeTv().setText(asignJobBean.getCreatetime());
            viewHolder.getTaskStartEndtimeTv().setText(asignJobBean.getEnd_time());
            viewHolder.getTaskCompleteStateTv().setText(asignJobBean.getState());

        }else{



        }


        return convertView;
    }

    class MyViewHolder {
        @Bind(R.id.task_rank_num_tv)
        TextView taskRankNumTv;

        @Bind(R.id.task_detail_content)
        TextView taskContentTv;

        @Bind(R.id.task_output_tv)
        TextView taskOutputTv;

        @Bind(R.id.task_starttime_tv)
        TextView taskStartEndtimeTv;

        @Bind(R.id.task_complete_state_tv)
        TextView taskCompleteStateTv;

        @Bind(R.id.ll_task_availabel)
        LinearLayout taskAvailabelLl;

        @Bind(R.id.task_avalilabel_tv)
        TextView taskAvalilabelTv;

        @Bind(R.id.ll_task_quality)
        LinearLayout taskQualityLl;

        @Bind(R.id.task_quality_tv)
        TextView taskQualityTv;

        @Bind(R.id.ll_task_evaluate_content)
        LinearLayout taskEvaluateContentLl;

        @Bind(R.id.task_evaluate_tv)
        TextView taskEvaluateTv;

        public MyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public TextView getTaskRankNumTv() {
            return taskRankNumTv;
        }

        public TextView getTaskContentTv() {
            return taskContentTv;
        }

        public TextView getTaskOutputTv() {
            return taskOutputTv;
        }

        public TextView getTaskStartEndtimeTv() {
            return taskStartEndtimeTv;
        }

        public TextView getTaskCompleteStateTv() {
            return taskCompleteStateTv;
        }

        public LinearLayout getTaskAvailabelLl() {
            return taskAvailabelLl;
        }

        public TextView getTaskAvalilabelTv() {
            return taskAvalilabelTv;
        }

        public LinearLayout getTaskQualityLl() {
            return taskQualityLl;
        }

        public TextView getTaskQualityTv() {
            return taskQualityTv;
        }

        public LinearLayout getTaskEvaluateContentLl() {
            return taskEvaluateContentLl;
        }

        public TextView getTaskEvaluateTv() {
            return taskEvaluateTv;
        }
    }
}
