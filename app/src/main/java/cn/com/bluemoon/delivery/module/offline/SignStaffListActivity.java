package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignStaffList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewActivity;
import cn.com.bluemoon.delivery.module.offline.adapter.SignStaffAdapter;
import cn.com.bluemoon.delivery.ui.ShowEvaluateDetailPopView;
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
    protected String getTitleString() {
        return getString(R.string.offline_course_detail_sign_staff);
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

}
