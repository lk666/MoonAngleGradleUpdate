package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import cn.com.bluemoon.delivery.sz.util.LogUtil;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc      任务
 */
public class TaskOrEvaluateDetailAdapter extends BaseAdapter {

    private List<AsignJobBean> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;
    /**
     * 0：任务详情 1：评价详情
     */
    private int showType = 0;
    public static final int ACTIVITY_TYPE_TASK_DETAIL = 0;//任务详情
    public static final int ACTIVITY_TYPE_EVALUATE_DETAIL = 1;//评价详情
    public static final int ACTIVITY_TYPE_DETAIL = 2;//任务查看详情

    /**查看详情时，是否已评价区分，//model 0待审批(未评价)，1审批通过，2审批驳回（x）',*/
    public String  modleType="";

    public TaskOrEvaluateDetailAdapter(Context cxt, int showType, List<AsignJobBean> datas) {
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
        MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sz_activity_task_or_evaluate_detail_item, null);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        ///*************************************显示数据************************************************/
        AsignJobBean asignJobBean = datas.get(position);
        if (showType == ACTIVITY_TYPE_TASK_DETAIL) {
            /**任务详情*/
//            业绩以下没有 taskAvailabelLl ,ll_task_quality, ll_task_evaluate_content
            viewHolder.getTaskAvailabelLl().setVisibility(View.GONE);
            viewHolder.getTaskQualityLl().setVisibility(View.GONE);
            viewHolder.getTaskEvaluateContentLl().setVisibility(View.GONE);
        }else if(showType == ACTIVITY_TYPE_DETAIL){
            ////model 0待审批(未评价)，1审批通过，2审批驳回（x）',
//            if (asignJobBean.)
            String model=getModleType();
            LogUtil.i("modle----->"+model);
            if (!TextUtils.isEmpty(model)){
                if (model.equals("0")){//未评价的不显示
                    viewHolder.getTaskAvailabelLl().setVisibility(View.GONE);
                    viewHolder.getTaskQualityLl().setVisibility(View.GONE);
                    viewHolder.getTaskEvaluateContentLl().setVisibility(View.GONE);
                }else if(model.equals("1")){//已评价的显示评价内容
                    viewHolder.getTaskAvailabelLl().setVisibility(View.VISIBLE);
                    viewHolder.getTaskQualityLl().setVisibility(View.VISIBLE);
                    viewHolder.getTaskEvaluateContentLl().setVisibility(View.VISIBLE);
                }
            }

        } else {
            /**评价详情*/
            viewHolder.getTaskAvailabelLl().setVisibility(View.VISIBLE);
            viewHolder.getTaskQualityLl().setVisibility(View.VISIBLE);
            viewHolder.getTaskEvaluateContentLl().setVisibility(View.VISIBLE);
        }
        viewHolder.getTaskRankNumTv().setText(cxt.getString(R.string.sz_task_label2) + (position + 1));
        viewHolder.getTaskContentTv().setText(asignJobBean.getTask_cont());
        viewHolder.getTaskOutputTv().setText(asignJobBean.getProduce_cont());

        viewHolder.getTaskStartTimeTv().setText(
                asignJobBean.getBegin_time());
        viewHolder.getTaskEndTimeTv().setText(
                asignJobBean.getEnd_time());


        String completeState = asignJobBean.getState();
        if ("0".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_not_start_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.gray));
//            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.red));

        } else if ("1".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_completing_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.gray));
//            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.btn_blue));

        } else if ("2".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_completed_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.gray));

        } else if ("3".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_pause_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.gray));
//            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.red));

        }

        String is_valid = asignJobBean.getIs_valid();
        if ("0".equalsIgnoreCase(is_valid)) {
            viewHolder.getTaskAvalilabelTv().setText(R.string.sz_task_is_valid_label);
        } else {
            viewHolder.getTaskAvalilabelTv().setText(R.string.sz_task_not_valid_label);
        }

        if (!TextUtils.isEmpty(asignJobBean.getScore())) {
            viewHolder.getTaskQualityTv().setText(asignJobBean.getQuality_score() + cxt.getString(R.string.sz_task_quality_score_unit));
        } else {
            viewHolder.getTaskQualityTv().setText("0" + cxt.getString(R.string.sz_task_quality_score_unit));
        }
        viewHolder.getTaskEvaluateTv().setText(asignJobBean.getReview_cont());
        return convertView;
    }

    public String getModleType() {
        return modleType;
    }

    public void setModleType(String modleType) {
        this.modleType = modleType;
        notifyDataSetChanged();
    }

    class MyViewHolder {
        @Bind(R.id.task_rank_num_tv)
        TextView taskRankNumTv;

        @Bind(R.id.task_detail_content)
        TextView taskContentTv;

        @Bind(R.id.task_output_tv)
        TextView taskOutputTv;

        @Bind(R.id.task_starttime_tv)
        TextView taskStartTimeTv;

        @Bind(R.id.task_endtime_tv)
        TextView taskEndTimeTv;

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

        public TextView getTaskStartTimeTv() {
            return taskStartTimeTv;
        }

        public TextView getTaskEndTimeTv() {
            return taskEndTimeTv;
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
