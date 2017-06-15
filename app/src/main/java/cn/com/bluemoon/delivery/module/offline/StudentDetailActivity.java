package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultStudentDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.TeacherInfoView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellParagraphView;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;
import cn.com.bluemoon.lib_widget.module.form.BmRankStar2;

public class StudentDetailActivity extends BaseActivity implements BMFieldArrow1View
        .FieldArrowListener {

    @Bind(R.id.view_name)
    BmCellTextView viewName;
    @Bind(R.id.view_state)
    BmCellTextView viewState;
    @Bind(R.id.view_time)
    BmCellTextView viewTime;
    @Bind(R.id.view_code)
    BmCellTextView viewCode;
    @Bind(R.id.view_theme)
    BmCellTextView viewTheme;
    @Bind(R.id.view_contacts)
    BmCellTextView viewContacts;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.view_room)
    BmCellTextView viewRoom;
    @Bind(R.id.view_address)
    BmCellTextView viewAddress;
    @Bind(R.id.view_purpose)
    BmCellParagraphView viewPurpose;
    @Bind(R.id.btn_evaluate)
    BMAngleBtn3View btnEvaluate;
    @Bind(R.id.view_sign_time)
    BmCellTextView viewSignTime;
    @Bind(R.id.view_content_star)
    BmRankStar2 viewContentStar;
    @Bind(R.id.view_teacher_star)
    BmRankStar2 viewTeacherStar;
    @Bind(R.id.view_suggest)
    BmCellParagraphView viewSuggest;
    @Bind(R.id.layout_evaluate)
    LinearLayout layoutEvaluate;
    @Bind(R.id.view_teacher)
    BMFieldArrow1View viewTeacher;
    private TextView txtRight;

    private PopupWindow popupWindow;
    private TeacherInfoView viewInfo;

    private ResultStudentDetail.StudentDetail detail;
    private String courseCode;
    private String planCode;

    public static void startAction(Activity context, String courseCode, String planCode, int
            requestCode) {
        Intent intent = new Intent(context, StudentDetailActivity.class);
        intent.putExtra("courseCode", courseCode);
        intent.putExtra("planCode", planCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        courseCode = getIntent().getStringExtra("courseCode");
        planCode = getIntent().getStringExtra("planCode");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_course_detail_title);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        txtRight = titleBar.getTvRightView();
        txtRight.setText(R.string.offline_sign_cancel);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        showWaitDialog();
        OffLineApi.cancel(getToken(), courseCode, planCode, ClientStateManager.getUserName(),
                getNewHandler(1, ResultBase.class));
    }

    @Override
    public void initView() {
        getData();
        viewTeacher.setListener(this);
        initPopupWindow();
    }

    @Override
    public void initData() {
        if (detail == null) return;
        setStatus(detail.status, detail.signTime > 0, detail.isEvaluated == 1 && detail
                .evaluateDetail != null);
        viewName.setContentText(detail.courseName);
        viewState.setContentText(OfflineUtil.getTextByStatus(detail.status, detail.signTime > 0));
        viewTime.setContentText(DateUtil.getTimes(detail.startTime, detail.endTime));
        viewCode.setContentText(detail.courseCode);
        viewTheme.setContentText(detail.topic);
        viewTeacher.setContent(detail.teacherName);
        viewContacts.setContentText(detail.contactsName);
        txtPhone.setText(detail.contactsPhone);
        viewRoom.setContentText(detail.room);
        viewAddress.setContentText(detail.address);
        viewPurpose.setContentText(detail.purpose);
        viewInfo.setData(detail.avatar, detail.teacherName);
    }

    private void getData() {
        showWaitDialog();
        OffLineApi.courseDetail(getToken(), courseCode, planCode, getNewHandler(0,
                ResultStudentDetail.class));
    }

    //设置状态显示
    private void setStatus(String status, boolean isSign, boolean isEvaluate) {
        //已签到，未评价，未关闭：显示评价按钮
        //已签到，未评价，未结束：显示签到按钮
        //已签到显示签到信息
        //已评价显示评价信息
        if (isSign) {
            ViewUtil.setViewVisibility(viewSignTime, View.VISIBLE);
            viewSignTime.setContentText(DateUtil.getTimeToYMDHM(detail.signTime));
            if (isEvaluate) {
                ViewUtil.setViewVisibility(layoutEvaluate, View.VISIBLE);
                ViewUtil.setViewVisibility(btnEvaluate, View.GONE);
                viewContentStar.setRating(detail.evaluateDetail.courseStar);
                viewTeacherStar.setRating(detail.evaluateDetail.teacherStar);
                viewSuggest.setContentText(TextUtils.isEmpty(detail.evaluateDetail.comment) ?
                        getString(R.string.none) : detail.evaluateDetail.comment);
            } else {
                if (!Constants.OFFLINE_STATUS_CLOSE_CLASS.equals(status)) {
                    ViewUtil.setViewVisibility(btnEvaluate, View.VISIBLE);
                    if (!Constants.OFFLINE_STATUS_END_CLASS.equals(status)) {
                        ViewUtil.setViewVisibility(txtRight, View.VISIBLE);
                    }
                }
            }
        }
    }

    //初始化讲师弹框
    private void initPopupWindow() {
        popupWindow = new PopupWindow(this);
        viewInfo = new TeacherInfoView(this);
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
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            detail = ((ResultStudentDetail) result).data;
            initData();
        } else if (requestCode == 1) {
            toast(result.getResponseMsg());
            setResult(RESULT_OK);
            finish();
        }

    }

    @OnClick({R.id.txt_phone, R.id.btn_evaluate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_phone:
                break;
            case R.id.btn_evaluate:
                EvaluateEditStudentActivity.startAction(this, courseCode, planCode, 0);
                break;
        }
    }

    @Override
    public void onClickLayout() {

    }

    @Override
    public void onClickRight() {
        popupWindow.showAtLocation(viewTeacher, Gravity.NO_GRAVITY, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
//            getData();
            setResult(RESULT_OK);
            finish();
        }
    }
}