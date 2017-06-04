package cn.com.bluemoon.delivery.module.offline;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.common.BMFieldArrow1View;
import cn.com.bluemoon.delivery.ui.common.BmCellParagraphView;
import cn.com.bluemoon.delivery.ui.common.BmCellTextView;

/**
 * 教师 课程详情
 * Created by tangqiwei on 2017/6/4.
 */

public class TeacherDetailActivity extends BaseActivity implements BMFieldArrow1View.FieldArrowListener {

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

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    public void onClickLayout() {

    }

    @Override
    public void onClickRight() {

    }
}
