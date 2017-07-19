package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherScanPlan;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.choice.BMCheckList2View;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.choice.entity.RadioItem;
import cn.com.bluemoon.lib_widget.module.choice.interf.CheckListener;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

public class TeacherScanPlanActivity extends BaseActivity implements CheckListener {

    @Bind(R.id.view_all)
    BMRadioItemView viewAll;
    @Bind(R.id.btn_sign)
    BMAngleBtn3View btnSign;
    @Bind(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @Bind(R.id.view_info)
    BmCellTextView viewInfo;
    @Bind(R.id.view_code)
    BmCellTextView viewCode;
    @Bind(R.id.view_theme)
    BmCellTextView viewTheme;
    @Bind(R.id.list_check)
    BMCheckList2View listCheck;
    @Bind(R.id.activity_teacher_scan_plan)
    RelativeLayout activityTeacherScanPlan;

    private String planCode;
    private String userMark;
    private String userType;
    private List<ResultTeacherScanPlan.Data.Courses> listCourses;

    public static void actStart(Activity activity, String planCode, String userMark, String
            userType, int requestCode) {
        Intent intent = new Intent(activity, TeacherScanPlanActivity.class);
        intent.putExtra("planCode", planCode);
        intent.putExtra("userMark", userMark);
        intent.putExtra("userType", userType);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        Intent intent = getIntent();
        planCode = intent.getStringExtra("planCode");
        userMark = intent.getStringExtra("userMark");
        userType = intent.getStringExtra("userType");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_sign);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_scan_plan;
    }

    @Override
    public void initView() {
        listCheck.setListener(this);
    }

    @Override
    public void initData() {
        showWaitDialog();
        OffLineApi.teacherScanPlan(getToken(), planCode, userMark, userType, getNewHandler(0,
                ResultTeacherScanPlan.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            setData(((ResultTeacherScanPlan) result).data);
        } else if (requestCode == 1) {
            toast(result.getResponseMsg());
            finish();
        }
    }

    /**
     * 设置界面显示数据
     */
    private void setData(ResultTeacherScanPlan.Data data) {
        listCourses = data.courses;
        if (data.isTeacher) {
            listCheck.setCheckDisable(true);
            ViewUtil.setViewVisibility(layoutBottom, View.GONE);
        }
        viewInfo.setContentText(data.userInfo.userName + " " + data.userInfo.userMark);
        viewCode.setContentText(data.planInfo.planCode);
        viewTheme.setContentText(data.planInfo.topic);

        listCheck.setData(getListData(listCourses,data.isTeacher));
    }

    /**
     * 获取多选项的显示列表
     */
    private List<RadioItem> getListData(List<ResultTeacherScanPlan.Data.Courses> courses, boolean
            isTeacher) {
        List<RadioItem> list = new ArrayList<>();
        for (ResultTeacherScanPlan.Data.Courses course : courses) {
            String status = OfflineUtil.stateToString(course.status);
            String isSign = course.isSign ? getString(R.string.offline_signed) : getString(R
                    .string.offline_unsign);
            String text = DateUtil.getTimes(course.startTime, course.endTime) + "\n"
                    + getString(R.string.offline_sign_course, course.courseName) + "\n"
                    + getString(R.string.offline_sign_teacher, course.teacherName) + "\n"
                    + getString(R.string.offline_sign_status, status + "-" + isSign);
            int type = BMRadioItemView.TYPE_NORMAL;
            if (isTeacher && course.isDisable == 1) {
                type = BMRadioItemView.TYPE_DISABLE;
            }
            list.add(new RadioItem(null, text, type));
        }
        return list;
    }

    @OnClick({R.id.view_all, R.id.btn_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_all:
                if (viewAll.getType() == BMRadioItemView.TYPE_NORMAL) {
                    viewAll.setType(BMRadioItemView.TYPE_SELECT);
                    listCheck.selectAll();
                } else if (viewAll.getType() == BMRadioItemView.TYPE_SELECT) {
                    viewAll.setType(BMRadioItemView.TYPE_NORMAL);
                    listCheck.clearAll();
                }
                checkBtn();
                break;
            case R.id.btn_sign:
                List<String> list = new ArrayList<>();
                for (int i : listCheck.getValues()) {
                    if (i >= 0 && i < listCourses.size()) {
                        list.add(listCourses.get(i).courseCode);
                    }
                }
                showWaitDialog();
                OffLineApi.teacherSign(getToken(), planCode, list, userMark, userType,
                        getNewHandler(1,
                                ResultBase.class));
                break;
        }
    }

    /**
     * 检测是否全选
     */
    private void checkAll() {
        if (listCheck.isSelectAll()) {
            viewAll.seleced();
        } else {
            viewAll.cancel();
        }
    }

    /**
     * 检测签到按钮
     */
    private void checkBtn(){
        if (listCheck.getValues().size() > 0) {
            if (!btnSign.isEnabled()) {
                btnSign.setEnabled(true);
            }
        } else if (btnSign.isEnabled()) {
            btnSign.setEnabled(false);
        }
    }

    @Override
    public void onSelected(View view, int position) {
        checkAll();
        checkBtn();
    }

    @Override
    public void onCancel(View view, int position) {
        checkAll();
        checkBtn();
    }

    @Override
    public void onClickDisable(View view, int position) {
        toast(listCourses.get(position).message);
    }
}
