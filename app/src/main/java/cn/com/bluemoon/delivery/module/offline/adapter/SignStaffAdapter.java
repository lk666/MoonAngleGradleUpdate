package cn.com.bluemoon.delivery.module.offline.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignStaffList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;

/**
 * 学生评价列表
 * Created by tangqiwei on 2017/6/4.
 */

public class SignStaffAdapter extends BaseListAdapter<ResultSignStaffList.Data.Students>{

    public final static int TYPE_SIGN=1,TYPE_EVALUATE=2,TYPE_UN_EVALUATE=3;
    public final static int CLICK_ITEM=1,CLICK_EVALUATE=2;

    private int type;
    private String state;

    public SignStaffAdapter(Context context, OnListItemClickListener listener) {
        this(context,listener,TYPE_SIGN);
    }

    public SignStaffAdapter(Context context, OnListItemClickListener listener,int type) {
        super(context, listener);
        this.type=type;
    }

    public SignStaffAdapter(Context context, OnListItemClickListener listener,int type,String state) {
        super(context, listener);
        this.type=type;
        this.state=state;
    }

    public void setList(List<ResultSignStaffList.Data.Students> list,int type) {
        setList(list);
        this.type=type;
    }

    public void addList(List<ResultSignStaffList.Data.Students> list) {
        this.list.addAll(list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_sign_staff;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        TextView nameView=getViewById(R.id.txt_name);
        TextView codeView=getViewById(R.id.txt_code);
        TextView signTimeView=getViewById(R.id.txt_sign_time);
        TextView gradeView=getViewById(R.id.txt_grade);
        TextView evaluateView=getViewById(R.id.txt_evaluate);
        TextView evaluateClickView=getViewById(R.id.txt_evaluate_click);
        LinearLayout layoutDetail=getViewById(R.id.llayout_detail);

        ResultSignStaffList.Data.Students student= (ResultSignStaffList.Data.Students) getItem(position);
        nameView.setText(student.studentName);
        codeView.setText(student.studentCode);
        signTimeView.setText(DateUtil.getTimeToYMDHM(student.assignTime));

        if(student.score>0){
            layoutDetail.setVisibility(View.VISIBLE);
            gradeView.setText(String.valueOf(student.score));
            evaluateView.setText(context.getString(R.string.offline_type_evaluate)+student.comment);
        }else{
            layoutDetail.setVisibility(View.GONE);
        }

        if (type!=TYPE_SIGN&&(TextUtils.isEmpty(state)||!state.equals(Constants.OFFLINE_STATUS_CLOSE_CLASS))) {
            evaluateClickView.setVisibility(View.VISIBLE);
            switch (type) {
                case TYPE_EVALUATE:
                    evaluateClickView.setText(R.string.offline_click_compile_evaluate);
                    break;
                case TYPE_UN_EVALUATE:
                    evaluateClickView.setText(R.string.offline_click_evaluate);
                    break;
            }
        }else{
            evaluateClickView.setVisibility(View.GONE);
        }

        convertView.setTag(R.id.tag_type,CLICK_ITEM);
        evaluateClickView.setTag(R.id.tag_type,CLICK_EVALUATE);
        setClickEvent(isNew,position,convertView,evaluateClickView);
    }
}
