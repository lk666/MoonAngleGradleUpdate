package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherAndStudentList;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 我的授课列表
 * Created by tangqiwei on 2017/6/3.
 */

public class MyCoursesActivity extends OfflineListBaseActivity {

    public static void actionStart(Context context) {
        actionStart(context, Constants.OFFLINE_STATUS_WAITING_CLASS);
    }

    public static void actionStart(Context context, String status) {
        actionStart(context, status, MyCoursesActivity.class);
    }

    @Override
    protected void requestListData(long time) {
        OffLineApi.teacherCourseList(getToken(), time, getStatus(), getNewHandler(0, ResultTeacherAndStudentList.class));
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
                    toast("进入详情");
                    break;
                case OfflineAdapter.TO_NEXT_EVALUATE:
                    toast("进入评价");
                    break;
                case OfflineAdapter.REQUEST_START:
                    toast("开始上课");
                    break;
                case OfflineAdapter.REQUEST_END:
                    toast("结束上课");
                    break;
            }
        }
    }
}
