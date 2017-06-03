package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignDetail;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultStudentDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.common.BMAngleBtn3View;
import cn.com.bluemoon.delivery.ui.common.BmCellParagraphView;
import cn.com.bluemoon.delivery.ui.common.BmCellTextView;
import cn.com.bluemoon.delivery.ui.common.BmRankStar2;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;

public class StudentDetailActivity extends BaseActivity {

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
    @Bind(R.id.view_teacher)
    BmCellTextView viewTeacher;
    @Bind(R.id.img_teacher)
    ImageView imgTeacher;
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
    private TextView txtRight;

    private ResultStudentDetail detail;
    private String courseCode;
    private String planCode;

    public static void startAction(Context context, String courseCode, String planCode) {
        Intent intent = new Intent(context, StudentDetailActivity.class);
        intent.putExtra("courseCode", courseCode);
        intent.putExtra("planCode", planCode);
        context.startActivity(intent);
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
        OffLineApi.cancel(getToken(), courseCode, planCode, "", getNewHandler(1, ResultBase.class));
    }

    @Override
    public void initView() {
        showWaitDialog();
        OffLineApi.courseDetail(getToken(), courseCode, planCode, getNewHandler(0,
                ResultSignDetail.class));
    }

    @Override
    public void initData() {
        // TODO: 2017/6/3 测试数据
        detail = new ResultStudentDetail();
        if (detail == null) return;
        setStatus(detail.data.status,detail.data.signTime==0,detail.data.evaluateDetail!=null);
        viewName.setContentText(detail.data.courseName);
        viewState.setContentText(detail.data.status);
        viewTime.setContentText(DateUtil.getTimes(detail.data.startTime, detail.data.endTime));
        viewCode.setContentText(detail.data.courseCode);
        viewTheme.setContentText(detail.data.topic);
        viewTeacher.setContentText(detail.data.teacherName);
        viewContacts.setContentText(detail.data.contactsName);
        txtPhone.setText(detail.data.contactsPhone);
        viewRoom.setContentText(detail.data.room);
        viewAddress.setContentText(detail.data.address);
        viewPurpose.setContentText(detail.data.purpose);
    }

    private void setStatus(String status, boolean isSign, boolean isEvaluate) {
        //已签到，未评价，未关闭：显示评价按钮
        //已签到，未评价，未结束：显示签到按钮
        //已签到显示签到信息
        //已评价显示评价信息
        if(isSign){
            ViewUtil.setViewVisibility(viewSignTime,View.VISIBLE);
            viewSignTime.setContentText(DateUtil.getTime(detail.data.signTime, "yyyy-MM-dd HH:mm"));
            if(isEvaluate){
                ViewUtil.setViewVisibility(layoutEvaluate,View.VISIBLE);
                viewContentStar.setRating(detail.data.evaluateDetail.courseStar);
                viewTeacherStar.setRating(detail.data.evaluateDetail.teacherStar);
                // TODO: 2017/6/3 这两句刷新待修改了控件就去掉
                viewContentStar.initView();
                viewTeacherStar.initView();
                viewSuggest.setContentText(detail.data.evaluateDetail.comment);
            }else{
                if(!Constants.OFFLINE_STATUS_CLOSE_CLASS.equals(status)){
                    ViewUtil.setViewVisibility(btnEvaluate, View.VISIBLE);
                    if (!Constants.OFFLINE_STATUS_END_CLASS.equals(status)) {
                        ViewUtil.setViewVisibility(txtRight, View.VISIBLE);
                    }
                }
            }
        }
        /*if (isSign && !isEvaluate && !Constants.OFFLINE_STATUS_CLOSE_CLASS.equals(status)) {
            ViewUtil.setViewVisibility(btnEvaluate, View.VISIBLE);
            if (!Constants.OFFLINE_STATUS_END_CLASS.equals(status)) {
                ViewUtil.setViewVisibility(txtRight, View.VISIBLE);
            }
        }
        ViewUtil.setViewVisibility(viewSignTime, isSign ? View.VISIBLE : View.GONE);
        ViewUtil.setViewVisibility(layoutEvaluate, isEvaluate ? View.VISIBLE : View.GONE);*/
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            detail = (ResultStudentDetail) result;
            initData();
        } else if (requestCode == 1) {
            toast(result.getResponseMsg());
        }

    }

    @OnClick({R.id.img_teacher, R.id.txt_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_teacher:
                // TODO: 2017/6/2  讲师信息
                toast("讲师信息");
                break;
            case R.id.txt_phone:
                break;
        }
    }

}
