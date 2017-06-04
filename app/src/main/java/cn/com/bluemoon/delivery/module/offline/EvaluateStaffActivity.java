package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignStaffList;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.offline.adapter.SignStaffAdapter;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.ui.common.BmCellTextView;
import cn.com.bluemoon.delivery.ui.common.BmSegmentView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by tangqiwei on 2017/6/4.
 */

public class EvaluateStaffActivity extends BaseActivity implements OnListItemClickListener {


    private static final int HTTP_REQUEST_CODE_GET_MORE = 0x1000;
    private static final int HTTP_REQUEST_CODE_GET_DATA = 0x1001;
    @Bind(R.id.ctxt_course_name)
    BmCellTextView ctxtCourseName;
    @Bind(R.id.ctxt_course_state)
    BmCellTextView ctxtCourseState;
    @Bind(R.id.ctxt_course_time)
    BmCellTextView ctxtCourseTime;
    @Bind(R.id.ctxt_sign_number)
    BmCellTextView ctxtSignNumber;
    @Bind(R.id.ptrlv)
    PullToRefreshListView ptrlv;
    @Bind(R.id.segment_tab)
    BmSegmentView segmentTab;


    private CurriculumsTable curriculumsTable;

    public static void actionStart(Context context, CurriculumsTable curriculumsTable) {
        Intent intent = new Intent(context, EvaluateStaffActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("curriculumsTable", curriculumsTable);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        curriculumsTable = (CurriculumsTable) getIntent().getSerializableExtra("curriculumsTable");
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

    }

    @Override
    public void initData() {
        ctxtCourseName.setContentText(curriculumsTable.courseName);
        ctxtCourseState.setContentText(OfflineUtil.stateToString(curriculumsTable.status));
        ctxtCourseTime.setContentText(DateUtil.getTimes(curriculumsTable.startTime, curriculumsTable.endTime));
        ctxtSignNumber.setContentText(curriculumsTable.signNum + "人");
        OffLineApi.teacherEvaluateStudentList(getToken(),curriculumsTable.courseCode,10,curriculumsTable.planCode,0, SignStaffAdapter.TYPE_UN_EVALUATE,getNewHandler(HTTP_REQUEST_CODE_GET_DATA, ResultSignStaffList.class));
    }

    /**
     * 根据未评价和已评价的人数返回字符串List
     * @param unNumber
     * @param unmber
     * @return
     */
    private List<String> getArrString(int unNumber, int unmber) {
        List<String> list = new ArrayList<>();
        list.add(new StringBuffer().append(R.string.offline_un_evaluate).append("（").append(unNumber).append("）").toString());
        list.add(new StringBuffer().append(R.string.offline_yet_evaluate).append("（").append(unmber).append("）").toString());
        return list;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}
