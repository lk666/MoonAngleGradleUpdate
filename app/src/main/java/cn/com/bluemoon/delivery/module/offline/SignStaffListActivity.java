package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib_widget.module.form.BMListPaginationView;
import cn.com.bluemoon.lib_widget.utils.WidgeUtil;

/**
 * 签到人员列表
 * Created by tangqiwei on 2017/6/4.
 */

public class SignStaffListActivity extends BasePullToRefreshListViewActivity implements ShowEvaluateDetailPopView.DismissListener {

    private final static int SIZE=10;

    private String courseCode;
    private String planCode;
    private long timeStamp;
    private View footView;

    public static void actionStart(Context context,String courseCode,String planCode) {
        Intent intent=new Intent(context,SignStaffListActivity.class);
        intent.putExtra("courseCode",courseCode);
        intent.putExtra("planCode",planCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        courseCode=getIntent().getStringExtra("courseCode");
        planCode=getIntent().getStringExtra("planCode");
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
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        super.initPullToRefreshListView(ptrlv);
        ptrlv.getRefreshableView().setDivider(new ColorDrawable(getResources().getColor(R.color.line_soild_bg)));
        ptrlv.getRefreshableView().setDividerHeight(WidgeUtil.dip2px(this,0.5f));
        footView=new BMListPaginationView(this);
        ptrlv.getRefreshableView().addFooterView(footView);
        footView.setVisibility(View.GONE);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return getGetData(result,false);
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        return getGetData(result,true);
    }

    private List getGetData(ResultBase result,boolean isRefresh){
        ResultSignStaffList signStaffList= (ResultSignStaffList) result;
        timeStamp=signStaffList.data.lastTimeStamp;
        if(signStaffList.data.students.size()<SIZE){
            ptrlv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            if(signStaffList.data.students.size()==0&&isRefresh){
                footView.setVisibility(View.GONE);
            }else {
                footView.setVisibility(View.VISIBLE);
            }
        }else{
            ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
            footView.setVisibility(View.GONE);
        }
        return signStaffList.data.students;
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        OffLineApi.teacherEvaluateStudentList(getToken(),courseCode,SIZE,planCode,0,1,getNewHandler(requestCode,ResultSignStaffList.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        OffLineApi.teacherEvaluateStudentList(getToken(),courseCode,SIZE,planCode,timeStamp,1,getNewHandler(requestCode,ResultSignStaffList.class));
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
