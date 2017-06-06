package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherAndStudentList;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.utils.LibConstants;

/**
 * 我的培训列表
 * Created by tangqiwei on 2017/6/3.
 */

public class MyTrainActivity extends OfflineListBaseActivity {

    private Button btnSign;

    @Override
    public void initView() {
        super.initView();
        btnSign = (Button) findViewById(R.id.btn_sign);
        btnSign.setOnClickListener(this);
        if (Constants.OFFLINE_STATUS_WAITING_CLASS.equals(getStatus())) {
            ViewUtil.setViewVisibility(btnSign, View.VISIBLE);
        }
    }

    public static void actionStart(Context context) {
        actionStart(context, Constants.OFFLINE_STATUS_WAITING_CLASS);
    }

    public static void actionStart(Context context, String status) {
        actionStart(context, status, MyTrainActivity.class);
    }

    @Override
    protected void requestListData(long time) {
        OffLineApi.studentTrainlist(getToken(), time, getStatus(), getNewHandler(segmentTab
                .getCurrentPosition(),ResultTeacherAndStudentList.class));
    }

    @Override
    protected String getTeacherOrStudent() {
        return OfflineAdapter.LIST_STUDENTS;
    }

    @Override
    public void checkListener(int position) {
        super.checkListener(position);
        ViewUtil.setViewVisibility(btnSign, position == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btnSign) {
            ScanSignActivity.actStart(this, 0);
        }
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_my_train);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        if (item instanceof CurriculumsTable && view.getTag(R.id.tag_type) != null) {
            int type = (int) view.getTag(R.id.tag_type);
            CurriculumsTable curriculumsTable = (CurriculumsTable) item;
            switch (type) {
                case OfflineAdapter.TO_NEXT_DETAILS:
                    StudentDetailActivity.startAction(this, curriculumsTable.courseCode,
                            curriculumsTable.planCode);
                    break;
                case OfflineAdapter.TO_NEXT_EVALUATE:
                    EvaluateEditStudentActivity.startAction(this, curriculumsTable.courseCode,
                            curriculumsTable.planCode);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    requestListData(0);
                    break;
            }
        }
    }
}
