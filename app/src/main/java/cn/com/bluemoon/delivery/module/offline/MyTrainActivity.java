package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.view.View;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherAndStudentList;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * 我的授课列表
 * Created by tangqiwei on 2017/6/3.
 */

public class MyTrainActivity extends OfflineListBaseActivity {

    public static void actionStart(Context context) {
        actionStart(context, Constants.OFFLINE_STATUS_WAITING_CLASS);
    }

    public static void actionStart(Context context, String status) {
        actionStart(context,status,MyTrainActivity.class);
    }
    @Override
    protected void requestListData(long time) {
        OffLineApi.studentTrainlist(getToken(), time, getStatus(), getNewHandler(0, ResultTeacherAndStudentList.class));
    }

    @Override
    protected String getTeacherOrStudent() {
        return OfflineAdapter.LIST_STUDENTS;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_my_courses);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}
