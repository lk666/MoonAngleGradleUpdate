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
    private List<Object> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private Context cxt;

    public TaskWriteEvaluateApater(Context cxt, List<Object> datas) {
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

    public List<Object> getDatas() {
        return datas;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sz_activity_write_evaluate_item, null);
            viewHolder = new MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        ///*************************************显示数据************************************************/


        ///*************************************设置监听器************************************************/
        viewHolder.getTaskStateRg().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.do_task_state_completed_rb) {

                } else if (checkedId == R.id.do_task_state_completing_rb) {

                } else if (checkedId == R.id.do_task_state_completed_rb) {

                } else {

                }
            }
        });
        viewHolder.getTaskAvaliabelCb().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

//        viewHolder.getTaskAvaliabelTimeEt().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtil.i("onClick");
////                v.setFocusable(true);
//                v.requestFocus();
//            }
//        });
        viewHolder.getTaskAvaliabelTimeEt().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtil.i("onFocusChange:" + hasFocus);
            }
        });
        viewHolder.getTaskAvaliabelTimeEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    if (Integer.valueOf(s.toString()) >= 1440) {
                        new CommonAlertDialog.Builder(cxt).setMessage("有效工时不能超过1440分钟").show();
                        viewHolder.getTaskAvaliabelTimeEt().clearFocus();
                    }
                }
            }
        });
        viewHolder.getTaskQualityEvaluateRl().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("actionType", SzWriteEvaluateActivity.EVENT_ACTION_TYPE_QUALITY_SCORE);
                bundle.putString("viewPosition", position + "");
                PageJumps.PageJumps(cxt, TaskQualityScoreActivity.class, bundle);
            }
        });
        viewHolder.getTaskEvaluateContentRl().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("intentNum", SzWriteEvaluateActivity.EVENT_ACTION_TYPE_EVALUATE_CONTENT);
                bundle.putInt("maxTextLenght", 500);
                bundle.putString("inputContent", "");
                bundle.putString("viewName", position + "");
                PageJumps.PageJumps(cxt, InputToolsActivity.class, bundle);
            }
        });
        return convertView;
    }

    class MyViewHolder {
        @Bind(R.id.task_rank_num_tv)
        TextView taskRankNumTv;

        @Bind(R.id.task_detail_content)
        TextView taskContentTv;

        @Bind(R.id.task_output_tv)
        TextView taskOutputTv;

        @Bind(R.id.task_start_end_time_tv)
        TextView taskStartEndTimeTv;

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

        @Bind(R.id.do_evaluate_content_rl)
        RelativeLayout taskEvaluateContentRl;

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

        public TextView getTaskStartEndTimeTv() {
            return taskStartEndTimeTv;
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

        public RelativeLayout getTaskEvaluateContentRl() {
            return taskEvaluateContentRl;
        }
    }
}
