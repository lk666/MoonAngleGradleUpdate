package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.view.View;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherAndStudentList;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 我的授课列表
 * Created by tangqiwei on 2017/6/3.
 */

public class MyCoursesActivity extends OfflineListBaseActivity {

    private static final int HTTP_REQUEST_CODE_START = 0x1001;
    private static final int HTTP_REQUEST_CODE_END = 0x1002;

    public static void actionStart(Context context) {
        actionStart(context, 0);
    }

    public static void actionStart(Context context, int defPosition) {
        actionStart(context, defPosition, MyCoursesActivity.class);
    }

    @Override
    protected void requestListData(long time,int requestCode) {
        OffLineApi.teacherCourseList(getToken(), time, positionTogState(getCheckPosition())
                , getNewHandler(requestCode, ResultTeacherAndStudentList.class));
    }

    @Override
    protected String getTeacherOrStudent() {
        return OfflineAdapter.LIST_TEACHER;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_my_courses);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        if (item instanceof CurriculumsTable && view.getTag(R.id.tag_type) != null) {
            int type = (int) view.getTag(R.id.tag_type);
            CurriculumsTable curriculumsTable = (CurriculumsTable) item;
            switch (type) {
                case OfflineAdapter.TO_NEXT_DETAILS:
                    TeacherDetailActivity.startAction(this,curriculumsTable.courseCode,curriculumsTable.planCode);
                    break;
                case OfflineAdapter.TO_NEXT_EVALUATE:
                    EvaluateStaffActivity.actionStart(this,curriculumsTable);
                    break;
                case OfflineAdapter.TO_REALITY:
                    RealRecordActivity.actionStart(this,curriculumsTable.courseCode,curriculumsTable.planCode);
                    break;
            }
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        switch (requestCode) {
            case HTTP_REQUEST_CODE_START:
                requestData(false);
                break;
            case HTTP_REQUEST_CODE_END:
                requestData(false);
                break;
        }
    }

    @Override
    protected boolean isShowHead() {
        return getCheckPosition()==1;
    }


    @Override
    protected String positionTogState(int position) {
        switch (position) {
            case 0:
                return Constants.OFFLINE_STATUS_IN_CLASS;
            case 1:
                return Constants.OFFLINE_STATUS_CLOSE_CLASS;
            default:
                return Constants.OFFLINE_STATUS_IN_CLASS;
        }
    }
}
