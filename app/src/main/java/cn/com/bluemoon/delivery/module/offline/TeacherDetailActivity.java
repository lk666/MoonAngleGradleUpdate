package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.ui.common.BMFieldArrow1View;
import cn.com.bluemoon.delivery.ui.common.BmCellParagraphView;
import cn.com.bluemoon.delivery.ui.common.BmCellTextView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;

/**
 * 教师 课程详情
 * Created by tangqiwei on 2017/6/4.
 */

public class TeacherDetailActivity extends BaseActivity implements BMFieldArrow1View.FieldArrowListener{

    @Bind(R.id.ctxt_course_name)
    BmCellTextView ctxtCourseName;
    @Bind(R.id.ctxt_course_state)
    BmCellTextView ctxtCourseState;
    @Bind(R.id.ctxt_course_time)
    BmCellTextView ctxtCourseTime;
    @Bind(R.id.ctxt_course_actual_class_time)
    BmCellTextView ctxtCourseActualClassTime;
    @Bind(R.id.ctxt_course_actual_finish_time)
    BmCellTextView ctxtCourseActualFinishTime;
    @Bind(R.id.far_sign_staff)
    BMFieldArrow1View farSignStaff;
    @Bind(R.id.txt_course_info)
    TextView txtCourseInfo;
    @Bind(R.id.ctxt_course_code)
    BmCellTextView ctxtCourseCode;
    @Bind(R.id.ctxt_course_theme)
    BmCellTextView ctxtCourseTheme;
    @Bind(R.id.ctxt_course_number_participants)
    BmCellTextView ctxtCourseNumberParticipants;
    @Bind(R.id.ctxt_contacts)
    BmCellTextView ctxtContacts;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.ctxt_course_room)
    BmCellTextView ctxtCourseRoom;
    @Bind(R.id.ctxt_course_address)
    BmCellTextView ctxtCourseAddress;
    @Bind(R.id.ctxt_course_purpose)
    BmCellParagraphView ctxtCoursePurpose;

    private String courseCode;
    private String planCode;

    private ResultTeacherDetail teacherDetail;

    public static void startAction(Context context, String courseCode, String planCode) {
        Intent intent = new Intent(context, TeacherDetailActivity.class);
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
    protected String getTitleString() {
        return getString(R.string.offline_course_detail_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_teacher_course_details;
    }

    @Override
    public void initView() {
        showWaitDialog();
    }

    @Override
    public void initData() {
        OffLineApi.teacherCourseDetail(getToken(), courseCode, planCode, getNewHandler(0, ResultTeacherDetail.class));
    }



    /**
     * 根据签到人话
     *
     * @param signNumber
     * @param evaluateNumber
     * @return
     */
    private String signAndEvaluateNumberToString(int signNumber, int evaluateNumber,String state) {
        signNumber = signNumber < 0 ? 0 : signNumber;
        evaluateNumber = evaluateNumber < 0 ? 0 : evaluateNumber;
        StringBuffer buffer = new StringBuffer();
        if (signNumber >= 0) {
            buffer.append("签到").append(signNumber).append("人");
            if (state.equals(Constants.OFFLINE_STATUS_CLOSE_CLASS)) {
                buffer.append("，已评价").append(evaluateNumber).append("人");
            }
        }
        return buffer.toString();
    }

    public void initNetWordData() {
        ctxtCourseName.setContentText(teacherDetail.data.courseName);
        ctxtCourseState.setContentText(OfflineUtil.stateToString(teacherDetail.data.status));
        ctxtCourseTime.setContentText(DateUtil.getTimes(teacherDetail.data.startTime, teacherDetail.data.endTime));
        if (!teacherDetail.data.status.equals(Constants.OFFLINE_STATUS_WAITING_CLASS)) {
            ctxtCourseActualClassTime.setVisibility(View.VISIBLE);
            ctxtCourseActualClassTime.setContentText(DateUtil.getTimeToYMDHM(teacherDetail.data.realStartTime));
        } else {
            ctxtCourseActualClassTime.setVisibility(View.GONE);
        }
        if (teacherDetail.data.status.equals(Constants.OFFLINE_STATUS_END_CLASS) || teacherDetail.data.status.equals(Constants.OFFLINE_STATUS_CLOSE_CLASS)) {
            ctxtCourseActualFinishTime.setVisibility(View.VISIBLE);
            ctxtCourseActualFinishTime.setContentText(DateUtil.getTimeToYMDHM(teacherDetail.data.realEndTime));
        } else {
            ctxtCourseActualFinishTime.setVisibility(View.GONE);
        }
        if (!teacherDetail.data.status.equals(Constants.OFFLINE_STATUS_WAITING_CLASS)) {
            farSignStaff.setVisibility(View.VISIBLE);
            farSignStaff.setListener(this);
            farSignStaff.setContent(signAndEvaluateNumberToString(teacherDetail.data.signNum,teacherDetail.data.evaluateNum,teacherDetail.data.status));
        }else{
            farSignStaff.setVisibility(View.GONE);
        }
        ctxtCourseCode.setContentText(teacherDetail.data.courseCode);
        ctxtCourseTheme.setContentText(teacherDetail.data.topic);
        ctxtCourseNumberParticipants.setContentText(teacherDetail.data.enrollNum+"人");
        ctxtContacts.setContentText(teacherDetail.data.contactsName);
        txtPhone.setText(teacherDetail.data.contactsPhone);
        ctxtCourseRoom.setContentText(teacherDetail.data.room);
        ctxtCourseAddress.setContentText(teacherDetail.data.address);
        ctxtCoursePurpose.setContentText(teacherDetail.data.purpose);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                teacherDetail = (ResultTeacherDetail) result;
                if(teacherDetail.data!=null)
                initNetWordData();
                break;
        }
    }

    @Override
    public void onClickLayout() {
        SignStaffListActivity.actionStart(this,teacherDetail.data.courseCode,teacherDetail.data.planCode);
    }

    @Override
    public void onClickRight() {

    }

}
