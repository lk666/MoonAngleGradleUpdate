package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherAndStudentList;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.delivery.ui.CourseQcodeView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * 我的培训列表
 * Created by tangqiwei on 2017/6/3.
 */

public class MyTrainActivity extends OfflineListBaseActivity {

    private Button btnSign;

    private PopupWindow popupWindow;
    private CourseQcodeView viewInfo;

    @Override
    public void initView() {
        super.initView();
        btnSign = (Button) findViewById(R.id.btn_sign);
        btnSign.setOnClickListener(this);
        if (getCheckPosition() == 0) {
            ViewUtil.setViewVisibility(btnSign, View.VISIBLE);
        }
        initPopupWindow();
    }

    public static void actionStart(Context context) {
        actionStart(context, 0);
    }

    public static void actionStart(Context context, int defPosition) {
        actionStart(context, defPosition, MyTrainActivity.class);
    }

    @Override
    protected void requestListData(long time, int requestCode) {
        OffLineApi.studentTrainlist(getToken(), time, positionTogState(getCheckPosition())
                , getNewHandler(requestCode,
                        ResultTeacherAndStudentList.class));
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

    //初始化讲师弹框
    private void initPopupWindow() {
        popupWindow = new PopupWindow(this);
        viewInfo = new CourseQcodeView(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setContentView(viewInfo);
        viewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (getCheckPosition() == 0)
                    requestData(true);
            }
        });
    }

    @Override
    protected boolean isShowHead() {
        return getCheckPosition() == 2;
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
            final int type = (int) view.getTag(R.id.tag_type);
            CurriculumsTable curriculumsTable = (CurriculumsTable) item;
            switch (type) {
                case OfflineAdapter.TO_NEXT_DETAILS:
                    StudentDetailActivity.startAction(this, curriculumsTable.courseCode,
                            curriculumsTable.planCode, 0);
                    break;
                case OfflineAdapter.TO_NEXT_EVALUATE:
                    EvaluateEditStudentActivity.startAction(this, curriculumsTable.courseCode,
                            curriculumsTable.planCode, 1);
                    break;
                case OfflineAdapter.TO_QCODE:
                    // TODO: 2017/7/18 点击弹出二维码
//                    toast("点击弹出二维码");
                    viewInfo.setData(curriculumsTable.courseQrCodeMark, curriculumsTable.courseName, curriculumsTable.startTime, curriculumsTable.endTime);
                    popupWindow.showAtLocation(btnSign, Gravity.NO_GRAVITY, 0, 0);
                    break;
            }
        }
    }

    protected String positionTogState(int position) {
        switch (position) {
            case 0:
                return Constants.OFFLINE_STATUS_WAITING_CLASS;
            case 1:
                return Constants.OFFLINE_STATUS_IN_CLASS;
            case 2:
                return Constants.OFFLINE_STATUS_END_CLASS;
            default:
                return Constants.OFFLINE_STATUS_WAITING_CLASS;
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_CANCELED) {
//            return;
//        }
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case 0:
//                    requestData(false);
//                    break;
//            }
//        }
//    }

}
