package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultEvaluateNum;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignStaffList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.offline.adapter.SignStaffAdapter;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.ui.ShowEvaluateDetailPopView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib_widget.module.form.BMListPaginationView;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;
import cn.com.bluemoon.lib_widget.module.form.BmSegmentView;
import cn.com.bluemoon.lib_widget.utils.WidgeUtil;

/**
 * Created by tangqiwei on 2017/6/4.
 */

public class EvaluateStaffActivity extends BaseActivity implements OnListItemClickListener,
        BmSegmentView.CheckCallBack, ShowEvaluateDetailPopView.DismissListener {


    private static final int HTTP_REQUEST_CODE_GET_MORE = 0x1000;
    private static final int HTTP_REQUEST_CODE_GET_DATA = 0x1001;
    private static final int HTTP_REQUEST_CODE_GET_NUM = 0x1002;
    private static int SIZE = 10;
    @BindView(R.id.ctxt_course_name)
    BmCellTextView ctxtCourseName;
    @BindView(R.id.ctxt_course_state)
    BmCellTextView ctxtCourseState;
    @BindView(R.id.ctxt_course_time)
    BmCellTextView ctxtCourseTime;
    @BindView(R.id.ctxt_sign_number)
    BmCellTextView ctxtSignNumber;
    @BindView(R.id.ptrlv)
    PullToRefreshListView ptrlv;
    @BindView(R.id.segment_tab)
    BmSegmentView segmentTab;


    private CurriculumsTable curriculumsTable;

    private SignStaffAdapter adapter;

    private int defPosition;
    private long time;

    public static void actionStart(Context context, CurriculumsTable curriculumsTable) {
        actionStart(context, curriculumsTable, 0);
    }

    public static void actionStart(Context context, CurriculumsTable curriculumsTable, int
            position) {
        Intent intent = new Intent(context, EvaluateStaffActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("curriculumsTable", curriculumsTable);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        curriculumsTable = (CurriculumsTable) getIntent().getSerializableExtra("curriculumsTable");
        defPosition = getIntent().getIntExtra("position", 0);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_btn_evaluation_student);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_evaluate_staff;
    }

    @Override
    public void initView() {
        ptrlv.getRefreshableView().setDivider(new ColorDrawable(getResources().getColor(R.color.line_soild_bg)));
        ptrlv.getRefreshableView().setDividerHeight(WidgeUtil.dip2px(this,0.5f));
        ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        footView = new BMListPaginationView(this);
        ptrlv.getRefreshableView().addFooterView(footView);
        footView.setVisibility(View.GONE);
        ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData(segmentTab.getCurrentPosition());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestMore(segmentTab.getCurrentPosition(), time);
            }
        });
    }

    @Override
    public void initData() {
        adapter = new SignStaffAdapter(this, this, currentPositionToType(defPosition),
                curriculumsTable.status);
        ptrlv.setAdapter(adapter);
        ctxtCourseName.setContentText(curriculumsTable.courseName);
        ctxtCourseState.setContentText(OfflineUtil.stateToString(curriculumsTable.status));
        ctxtCourseTime.setContentText(DateUtil.getTimes(curriculumsTable.startTime,
                curriculumsTable.endTime));
        ctxtSignNumber.setContentText(curriculumsTable.signNum + "人");
        requestNum();
    }

    /**
     * 根据未评价和已评价的人数返回字符串List
     *
     * @param unNumber
     * @param unmber
     * @return
     */
    private List<String> getArrString(int unNumber, int unmber) {
        List<String> list = new ArrayList<>();
        list.add(new StringBuffer().append(getString(R.string.offline_un_evaluate)).append("（")
                .append(unNumber).append("）").toString());
        list.add(new StringBuffer().append(getString(R.string.offline_yet_evaluate)).append("（")
                .append(unmber).append("）").toString());
        return list;
    }

    private View footView;

    private void footViewIsShow(int size, boolean isRefresh) {
        if (size < SIZE) {
            ptrlv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            if (size == 0 && isRefresh) {
                footView.setVisibility(View.GONE);
            } else {
                footView.setVisibility(View.VISIBLE);
            }
        } else {
            ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
            footView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFinishResponse(int requestCode) {
        switch (requestCode) {
            case HTTP_REQUEST_CODE_GET_DATA:
            case HTTP_REQUEST_CODE_GET_MORE:
                hideWaitDialog();
                ptrlv.onRefreshComplete();
                break;
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case HTTP_REQUEST_CODE_GET_DATA:
                ResultSignStaffList signStaffData = (ResultSignStaffList) result;
                adapter.setList(signStaffData.data.students, currentPositionToType(segmentTab
                        .getCurrentPosition()));
                adapter.notifyDataSetChanged();
                time = signStaffData.data.lastTimeStamp;
                footViewIsShow(signStaffData.data.students.size(), true);
                break;
            case HTTP_REQUEST_CODE_GET_MORE:
                ResultSignStaffList signStaffMore = (ResultSignStaffList) result;
                adapter.addList(signStaffMore.data.students);
                adapter.notifyDataSetChanged();
                time = signStaffMore.data.lastTimeStamp;
                footViewIsShow(signStaffMore.data.students.size(), false);
                break;
            case HTTP_REQUEST_CODE_GET_NUM:
                ResultEvaluateNum evaluateNum = (ResultEvaluateNum) result;
                ctxtSignNumber.setContentText((evaluateNum.data.evaluatedNum + evaluateNum.data
                        .unEvaluatedNum) + "人");
                segmentTab.setTextList(getArrString(evaluateNum.data.unEvaluatedNum, evaluateNum
                        .data.evaluatedNum));
                segmentTab.setCheckCallBack(null);
                if(defPosition>=0){
                    segmentTab.changeTab(defPosition);
                    defPosition=-1;
                }else{
                    segmentTab.changeTab(segmentTab.getCurrentPosition());
                }
                segmentTab.setCheckCallBack(this);
                requestData(segmentTab.getCurrentPosition());
                break;
        }
    }

    /**
     * 根据选中的tab返回 已评价 未评价 类型
     *
     * @param position
     * @return
     */
    private int currentPositionToType(int position) {
        if (position == 0) {
            return SignStaffAdapter.TYPE_UN_EVALUATE;
        } else {
            return SignStaffAdapter.TYPE_EVALUATE;
        }
    }

    private ShowEvaluateDetailPopView window;

    @Override
    public void onItemClick(Object item, View view, int position) {
        if (view.getTag(R.id.tag_type) != null && view.getTag(R.id.tag_type) instanceof Integer
                && item instanceof ResultSignStaffList.Data.Students) {
            int type = (int) view.getTag(R.id.tag_type);
            ResultSignStaffList.Data.Students student = (ResultSignStaffList.Data.Students) item;
            switch (type) {
                case SignStaffAdapter.CLICK_EVALUATE:
                    EvaluateEditTeacherActivity.startAction(this, 0,curriculumsTable.courseCode,
                            curriculumsTable.planCode, student.studentCode, student.studentName);
                    break;
                case SignStaffAdapter.CLICK_ITEM:
                    if (currentPositionToType(segmentTab.getCurrentPosition()) ==
                            SignStaffAdapter.TYPE_UN_EVALUATE) {
                        return;
                    }
                    if (window == null) {
                        window = new ShowEvaluateDetailPopView(this, this);
                    }
                    window.showEva(getWindow().getDecorView(), student);
                    break;
            }
        }
    }

    @Override
    public void checkListener(int position) {
        adapter.setList(new ArrayList<ResultSignStaffList.Data.Students>(), currentPositionToType
                (position));
        adapter.notifyDataSetChanged();
        footView.setVisibility(View.GONE);
        requestData(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            requestNum();
        }
    }

    /**
     * 刷新数据
     */
    private void requestData(int position) {
        requestList(position, 0, HTTP_REQUEST_CODE_GET_DATA);
    }

    /**
     * 更多数据
     */
    private void requestMore(int position, long time) {
        requestList(position, time, HTTP_REQUEST_CODE_GET_MORE);
    }

    /**
     * 请求数量
     */
    private void requestNum() {
        showWaitDialog();
        OffLineApi.taecherEvaluateNum(getToken(), curriculumsTable.courseCode, curriculumsTable
                .planCode, getNewHandler(HTTP_REQUEST_CODE_GET_NUM, ResultEvaluateNum.class));
    }

    /**
     * 请求列表数据
     *
     * @param position
     * @param time
     * @param requestCode
     */
    private void requestList(int position, long time, int requestCode) {
        OffLineApi.teacherEvaluateStudentList(getToken(), curriculumsTable.courseCode, SIZE,
                curriculumsTable.planCode, time, currentPositionToType(position), getNewHandler
                        (requestCode, ResultSignStaffList.class));
    }

    @Override
    public void cancelReceiveValue() {

    }
}
