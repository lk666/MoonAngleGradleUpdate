package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignStaffList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.ShowEvaluateDetailPopView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;

/**
 * 签到人员列表
 * Created by tangqiwei on 2017/6/4.
 */

public class SignStaffListActivity extends BasePullToRefreshListViewActivity implements ShowEvaluateDetailPopView.DismissListener {


    private String courseCode;
    private String planCode;
    private long timeStamp;

    public static void actionStart(Context context,String courseCode,String planCode) {
        Intent intent=new Intent(context,SignStaffListActivity.class);
        intent.putExtra("courseCode",courseCode);
        intent.putExtra("planCode",planCode);
        context.startActivity(intent);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new SignStaffAdapter(this,this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultSignStaffList signStaffList= (ResultSignStaffList) result;
        timeStamp=signStaffList.data.lastTimeStamp;
        return signStaffList.data.students;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultSignStaffList signStaffList= (ResultSignStaffList) result;
        timeStamp=signStaffList.data.lastTimeStamp;
        return signStaffList.data.students;
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        OffLineApi.teacherEvaluateStudentList(getToken(),courseCode,10,planCode,0,1,getNewHandler(requestCode,ResultSignStaffList.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        OffLineApi.teacherEvaluateStudentList(getToken(),courseCode,10,planCode,timeStamp,1,getNewHandler(requestCode,ResultSignStaffList.class));
    }
    private ShowEvaluateDetailPopView window;
    @Override
    public void onItemClick(Object item, View view, int position) {
        if (window==null) {
            window=new ShowEvaluateDetailPopView(this,this);
        }
        window.showEva(getWindow().getDecorView(), (ResultSignStaffList.Data.Students) item);
    }

    @Override
    public void cancelReceiveValue() {

    }

    class SignStaffAdapter extends BaseListAdapter<ResultSignStaffList.Data.Students>{

        public SignStaffAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
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
            LinearLayout layoutDetail=getViewById(R.id.llayout_detail);

            ResultSignStaffList.Data.Students student= (ResultSignStaffList.Data.Students) getItem(position);
            nameView.setText(student.studentName);
            codeView.setText(student.studentCode);
            signTimeView.setText(DateUtil.getTimeToYMDHM(student.assignTime));

            if(student.score>0){
                layoutDetail.setVisibility(View.VISIBLE);
                gradeView.setText(String.valueOf(student.score));
                evaluateView.setText(getString(R.string.offline_type_evaluate)+student.comment);
            }else{
                layoutDetail.setVisibility(View.GONE);
            }

            setClickEvent(isNew,position,convertView);
        }
    }
}
