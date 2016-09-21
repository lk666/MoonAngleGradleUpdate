package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.taskManager.AsignJobBean;
import cn.com.bluemoon.delivery.sz.taskManager.InputToolsActivity;
import cn.com.bluemoon.delivery.sz.taskManager.SzWriteEvaluateActivity;
import cn.com.bluemoon.delivery.sz.taskManager.TaskQualityScoreActivity;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * Created by Wan.N
 * Date       2016/9/8
 * Desc      写评价/修改评价的适配器
 */
public class TaskWriteEvaluateApater extends BaseAdapter {
    /*记录每个任务数据实体*/
    private List<AsignJobBean> datas = new ArrayList<>();

    private LayoutInflater inflater;
    private Context cxt;
    private boolean isShowDefaultValue = false;//是否显示默认值
    private int activityType = -1;

    public TaskWriteEvaluateApater(Context cxt, List<AsignJobBean> datas, int activityType) {
        this.cxt = cxt;
        this.activityType = activityType;

        if (activityType == SzWriteEvaluateActivity.ACTIVITY_TYPE_WRTTE_EVALUATE) {
            isShowDefaultValue = true;
        } else {
            isShowDefaultValue = false;
        }
        if (datas == null) {
            this.datas.clear();
        } else {
            this.datas = datas;
        }
        if (inflater == null && cxt != null) {
            inflater = LayoutInflater.from(cxt);
        }
        dealDatas();
    }

    public void updateAdapter(List<AsignJobBean> datas) {
        if (datas == null) {
            this.datas.clear();
        } else {
            this.datas = datas;
        }
        dealDatas();
        notifyDataSetChanged();
    }

    /**
     * 简单处理下数据
     */
    private void dealDatas() {
//        将数据实体中的state赋值给newState
        for (AsignJobBean itemt : datas) {
            itemt.setNewState(itemt.getState());
        }
    }

    /**
     * 获取任务实体数据（包含修改的有效工时，质量评分等（除了任务状态））
     */
    public List<AsignJobBean> getDatas() {
        return datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public AsignJobBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AsignJobBean asignJobBean = datas.get(position);
        LogUtil.i("getView:" + position + "--getIs_valid:" + asignJobBean.getIs_valid() + "--getNewState:" + asignJobBean.getNewState() + "--getValid_min:" + asignJobBean.getValid_min());
        final MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sz_activity_write_evaluate_item, null);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        viewHolder.getTaskStateRg().setOnCheckedChangeListener(null);
        viewHolder.getTaskAvaliabelCb().setOnCheckedChangeListener(null);
        for (MyTextWatcher mTextWatcher : mListeners) {
//            LogUtil.e("removeTextChangedListener--" + position);
            viewHolder.getTaskAvaliabelTimeEt().removeTextChangedListener(mTextWatcher);
        }
        if (mTextWatcher != null) {
            mTextWatcher = null;
        }
        viewHolder.getTaskQualityEvaluateRl().setOnClickListener(null);
        viewHolder.getTaskEvaluateContentRl().setOnClickListener(null);
        ///*************************************显示数据************************************************/
        viewHolder.getTaskRankNumTv().setText(cxt.getString(R.string.sz_task_label) + (position + 1));
        viewHolder.getTaskContentTv().setText(asignJobBean.getTask_cont());
        viewHolder.getTaskOutputTv().setText(asignJobBean.getProduce_cont());
        viewHolder.getTaskStartTimeTv().setText(asignJobBean.getBegin_time());
        viewHolder.getTaskEndTimeTv().setText(asignJobBean.getEnd_time());
        //原始的任务完成状态
        String completeState = asignJobBean.getState();
        if ("0".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_not_start_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.red));
        } else if ("1".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_completing_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.btn_blue));
        } else if ("2".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_completed_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.gray));
        } else if ("3".equalsIgnoreCase(completeState)) {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_pause_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.red));
        } else {
            viewHolder.getTaskCompleteStateTv().setText(R.string.sz_task_completed_label);
            viewHolder.getTaskCompleteStateTv().setTextColor(cxt.getResources().getColor(R.color.gray));
        }
        //修改后的任务完成状态
        String completeNewState = asignJobBean.getNewState();
        if ("0".equalsIgnoreCase(completeNewState)) {
            viewHolder.getTaskPauseRb().setChecked(true);
        } else if ("1".equalsIgnoreCase(completeNewState)) {
            viewHolder.getTaskCompletingRb().setChecked(true);
        } else if ("2".equalsIgnoreCase(completeNewState)) {
            viewHolder.getTaskCompletedRb().setChecked(true);
        } else if ("3".equalsIgnoreCase(completeNewState)) {
            viewHolder.getTaskPauseRb().setChecked(true);
        } else {
            viewHolder.getTaskCompletedRb().setChecked(true);
        }

        String is_valid = asignJobBean.getIs_valid();
        if ("0".equalsIgnoreCase(is_valid)) {
            viewHolder.getTaskAvaliabelCb().setChecked(true);
        } else if ("1".equalsIgnoreCase(is_valid)) {
            viewHolder.getTaskAvaliabelCb().setChecked(false);
        } else {
            viewHolder.getTaskAvaliabelCb().setChecked(true);
        }


        if (SzWriteEvaluateActivity.ACTIVITY_TYPE_UPDATE_EVALUATE == activityType) {
            //修改评价
            //有效用时
            if (TextUtils.isEmpty(asignJobBean.getValid_min())) {
                viewHolder.getTaskAvaliabelTimeEt().setText("0");
            } else {
                viewHolder.getTaskAvaliabelTimeEt().setText(asignJobBean.getValid_min());
            }
        } else if (SzWriteEvaluateActivity.ACTIVITY_TYPE_WRTTE_EVALUATE == activityType) {
            //写评价
            //有效用时
            if (TextUtils.isEmpty(asignJobBean.getUsage_time())) {
                viewHolder.getTaskAvaliabelTimeEt().setText("0");
            } else {
                viewHolder.getTaskAvaliabelTimeEt().setText(asignJobBean.getUsage_time());
            }
        } else {
        }
        /**质量评分*/
        if (isShowDefaultValue) {
            //质量评分，默认十分
            viewHolder.getTaskQualityEvaluateScoreTv().setText(10 + cxt.getResources().getString(R.string.sz_task_quality_score_unit));
            datas.get(position).setQuality_score("10");
        } else {
            if (!TextUtils.isEmpty(asignJobBean.getQuality_score())) {
                viewHolder.getTaskQualityEvaluateScoreTv().setText(asignJobBean.getQuality_score() + cxt.getResources().getString(R.string.sz_task_quality_score_unit));
            } else {
                //默认十分
                viewHolder.getTaskQualityEvaluateScoreTv().setText(10 + cxt.getResources().getString(R.string.sz_task_quality_score_unit));
                datas.get(position).setQuality_score("10");
            }
        }
        if (!TextUtils.isEmpty(asignJobBean.getReview_cont())) {
            viewHolder.getTaskEvaluateContentTv().setText(asignJobBean.getReview_cont());
        } else {
            viewHolder.getTaskEvaluateContentTv().setText(R.string.sz_do_task_evaluate_content_label2);
        }

        ///*************************************设置监听器************************************************/
        viewHolder.getTaskStateRg().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.do_task_state_completed_rb) {
                    datas.get(position).setNewState("2");
                } else if (checkedId == R.id.do_task_state_completing_rb) {
                    datas.get(position).setNewState("1");
                } else if (checkedId == R.id.do_task_pause_state_rb) {
                    datas.get(position).setNewState("3");
                } else {

                }
                LogUtil.e("修改" + position + "进展状态：" + datas.get(position).getNewState());
            }
        });
        viewHolder.getTaskAvaliabelCb().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    datas.get(position).setIs_valid("0");
                } else {
                    datas.get(position).setIs_valid("1");
                }
//                LogUtil.e("修改" + position + "有效性：" + datas.get(position).getIs_valid());
            }
        });
        if (mTextWatcher == null) {
            mTextWatcher = new MyTextWatcher(position, viewHolder.getTaskAvaliabelTimeEt());
        }
        viewHolder.getTaskAvaliabelTimeEt().addTextChangedListener(mTextWatcher);
        mListeners.add(mTextWatcher);

        viewHolder.getTaskQualityEvaluateRl().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowDefaultValue = false;
                Bundle bundle = new Bundle();
                bundle.putInt("actionType", SzWriteEvaluateActivity.EVENT_ACTION_TYPE_QUALITY_SCORE);
                bundle.putString("viewPosition", position + "");
                bundle.putString("score", datas.get(position).getQuality_score());
                PageJumps.PageJumps(cxt, TaskQualityScoreActivity.class, bundle);
            }
        });
        viewHolder.getTaskEvaluateContentRl().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(InputToolsActivity.INTENTITEMTAG, SzWriteEvaluateActivity.EVENT_ACTION_TYPE_EVALUATE_CONTENT);
                bundle.putInt(InputToolsActivity.MAXTEXTLENGHT, 200);
                bundle.putString(InputToolsActivity.INPUTTITELCONTENT, asignJobBean.getReview_cont());
                bundle.putString(InputToolsActivity.VIEWNAME, position + "");
                PageJumps.PageJumps(cxt, InputToolsActivity.class, bundle);
            }
        });
        return convertView;
    }

    private List<MyTextWatcher> mListeners = new ArrayList<>();

    private MyTextWatcher mTextWatcher;

    class MyTextWatcher implements TextWatcher {
        private int position;
        private EditText editText;

        public MyTextWatcher(int position, EditText editText) {
            this.position = position;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString())) {
                if (Integer.valueOf(s.toString()) >= 1440) {
                    new CommonAlertDialog.Builder(cxt).setMessage(cxt.getString(R.string.sz_valid_time_warning_label)).show();
                    datas.get(position).setValid_min("0");
                    editText.setText("0");
                } else {
                    datas.get(position).setValid_min(s.toString());
                }
            } else {
                datas.get(position).setValid_min("0");
//                LogUtil.e("修改" + position + "有效工时：0");
            }
//            LogUtil.e("修改" + position + "有效工时：" + datas.get(position).getValid_min());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

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

        @Bind(R.id.rg_task_complete_state)
        RadioGroup taskStateRg;

        @Bind(R.id.do_task_state_completed_rb)
        RadioButton taskCompletedRb;

        @Bind(R.id.do_task_state_completing_rb)
        RadioButton taskCompletingRb;

        @Bind(R.id.do_task_pause_state_rb)
        RadioButton taskPauseRb;

        @Bind(R.id.do_task_avalilabel_cb)
        CheckBox taskAvaliabelCb;

        @Bind(R.id.do_task_avalilabel_time_rl)
        RelativeLayout taskAvaliabelTimeRl;

        @Bind(R.id.do_task_avalilabel_time_et)
        EditText taskAvaliabelTimeEt;

        @Bind(R.id.do_quality_evaluate_rl)
        RelativeLayout taskQualityEvaluateRl;

        @Bind(R.id.do_quality_evaluate_socre_tv)
        TextView taskQualityEvaluateScoreTv;

        @Bind(R.id.do_evaluate_content_rl)
        RelativeLayout taskEvaluateContentRl;

        @Bind(R.id.do_evaluate_content_tv)
        TextView taskEvaluateContentTv;

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

        public RadioGroup getTaskStateRg() {
            return taskStateRg;
        }

        public RadioButton getTaskCompletedRb() {
            return taskCompletedRb;
        }

        public RadioButton getTaskCompletingRb() {
            return taskCompletingRb;
        }

        public RadioButton getTaskPauseRb() {
            return taskPauseRb;
        }

        public CheckBox getTaskAvaliabelCb() {
            return taskAvaliabelCb;
        }

        public RelativeLayout getTaskAvaliabelTimeRl() {
            return taskAvaliabelTimeRl;
        }

        public EditText getTaskAvaliabelTimeEt() {
            return taskAvaliabelTimeEt;
        }

        public RelativeLayout getTaskQualityEvaluateRl() {
            return taskQualityEvaluateRl;
        }

        public TextView getTaskQualityEvaluateScoreTv() {
            return taskQualityEvaluateScoreTv;
        }

        public TextView getTaskEvaluateContentTv() {
            return taskEvaluateContentTv;
        }

        public RelativeLayout getTaskEvaluateContentRl() {

            return taskEvaluateContentRl;
        }
    }
}
