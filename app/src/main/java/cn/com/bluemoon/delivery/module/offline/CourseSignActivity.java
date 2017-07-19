package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultTeacherScanCourse;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 教师扫码课程详情
 * Created by tangqiwei on 2017/7/18.
 */

public class CourseSignActivity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.cell_text_student_info)
    BmCellTextView cellTextStudentInfo;
    @Bind(R.id.cell_text_course__name)
    BmCellTextView cellTextCourseName;
    @Bind(R.id.cell_text_record_date)
    BmCellTextView cellTextRecordDate;
    @Bind(R.id.cell_text_evaluate_time)
    BmCellTextView cellTextEvaluateTime;
    @Bind(R.id.cell_text_course_room)
    BmCellTextView cellTextCourseRoom;
    @Bind(R.id.cell_text_course_state)
    BmCellTextView cellTextCourseState;
    @Bind(R.id.cell_text_sign_time)
    BmCellTextView cellTextSignTime;
    @Bind(R.id.angle_btn3_sign)
    BMAngleBtn3View angleBtn3Sign;

    private String courseCode;
    private String planCode;
    private String userMark;
    private String userType;

    public static void actStart(Activity activity,String courseCode,String planCode,String userMark,String userType,int requestCode){
        Intent intent=new Intent(activity,CourseSignActivity.class);
        intent.putExtra("courseCode",courseCode);
        intent.putExtra("planCode",planCode);
        intent.putExtra("userMark",userMark);
        intent.putExtra("userType",userType);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_sign);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        Intent intent=getIntent();
        courseCode=intent.getStringExtra("courseCode");
        planCode=intent.getStringExtra("planCode");
        userMark=intent.getStringExtra("userMark");
        userType=intent.getStringExtra("userType");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_sign;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        OffLineApi.teacherScanCourse(getToken(),planCode,courseCode,userMark,userType,getNewHandler(0, ResultTeacherScanCourse.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if(requestCode==0){
            if(result instanceof ResultTeacherScanCourse){
                ResultTeacherScanCourse teacherScanCourse= (ResultTeacherScanCourse) result;
                intNetWordData(teacherScanCourse.data);
            }
        }else if(requestCode==1){
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * 初始化网络数据
     * @param data
     */
    private void intNetWordData(ResultTeacherScanCourse.Data data){
        cellTextStudentInfo.setContentText(new StringBuffer().append(data.userInfo.userName).append(" ").append(data.userInfo.userMark).toString());
        cellTextCourseName.setContentText(data.courseInfo.courseName);
        cellTextRecordDate.setContentText(DateUtil.getTime(data.courseInfo.startTime));
        cellTextEvaluateTime.setContentText(new StringBuffer().append(DateUtil.getTimeToHours(data.courseInfo.startTime)).append("-").append(DateUtil.getTimeToHours(data.courseInfo.endTime)).toString());
        cellTextCourseRoom.setContentText(data.courseInfo.room);
        cellTextCourseState.setContentText(OfflineUtil.stateToString(data.courseInfo.status));
        if(data.courseInfo.signTime>0){
            cellTextSignTime.setVisibility(View.VISIBLE);
            cellTextSignTime.setContentText(DateUtil.getTimeToHours(data.courseInfo.signTime));
        }
        if(data.isShowSignButt){
            angleBtn3Sign.setVisibility(View.VISIBLE);
            angleBtn3Sign.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.angle_btn3_sign:
                List<String> list=new ArrayList<>();
                list.add(courseCode);
                OffLineApi.teacherSign(getToken(),planCode,list,userMark,userType,getNewHandler(1,ResultBase.class));
                break;
        }
    }
}
