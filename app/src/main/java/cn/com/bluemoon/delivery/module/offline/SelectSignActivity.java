package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.common.BMRadioListView;
import cn.com.bluemoon.delivery.ui.common.RadioItem;
import cn.com.bluemoon.delivery.utils.DateUtil;

public class SelectSignActivity extends BaseActivity {

    @Bind(R.id.view_radio)
    BMRadioListView viewRadio;
    private String roomCode;
    private String planCode;

    public static void actionStart(Context context, String roomCode) {
        Intent intent = new Intent(context, SelectSignActivity.class);
        intent.putExtra("code", roomCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        roomCode = getIntent().getStringExtra("code");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_select_sign_course);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_sign;
    }

    @Override
    public void initView() {
//        showWaitDialog();
//        OffLineApi.signDetail(getToken(), roomCode, getNewHandler(0, ResultSignDetail.class));
        toast(roomCode);
    }

    @Override
    public void initData() {
        onSuccessResponse(0, null, mockData());
    }

    private ResultSignDetail mockData() {
        ResultSignDetail result = new ResultSignDetail();
        ResultSignDetail.SignDetailData data = result.new SignDetailData();
        List<ResultSignDetail.SignDetailData.Course> courses = new ArrayList<>();
        ResultSignDetail.SignDetailData.Course course = data.new Course();
        course.courseCode = "0";
        course.startTime = System.currentTimeMillis();
        course.endTime = course.startTime;
        course.courseName = "课程名称课程名称0";
        course.teacherName = "讲师名称0";
        ResultSignDetail.SignDetailData.Course course1 = data.new Course();
        course1.courseCode = "1";
        course1.startTime = System.currentTimeMillis();
        course1.endTime = course.startTime;
        course1.courseName = "课程名称课程名称1";
        course1.teacherName = "讲师名称1";
        ResultSignDetail.SignDetailData.Course course2 = data.new Course();
        course2.courseCode = "2";
        course2.startTime = System.currentTimeMillis();
        course2.endTime = course.startTime;
        course2.courseName = "课程名称课程名称2";
        course2.teacherName = "讲师名称2";
        courses.add(course);
        courses.add(course1);
        courses.add(course2);
        courses.add(course);
        courses.add(course1);
        courses.add(course2);
        data.courses = courses;
        data.date = System.currentTimeMillis();
        data.planCode = "111";
        data.room = "904室";
        result.data = data;
        return result;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode){
            case 0:
                ResultSignDetail detail = (ResultSignDetail) result;
                planCode = detail.data.planCode;
                viewRadio.setData(getRadioList(detail.data.courses));
                break;
            case 1:
                toast(result.getResponseMsg());
                break;
        }

    }

    private List<RadioItem> getRadioList(List<ResultSignDetail.SignDetailData.Course> courses) {
        List<RadioItem> list = new ArrayList<>();
        for (ResultSignDetail.SignDetailData.Course course : courses) {
            String text = DateUtil.getTimeToHours(course.startTime) + "-" + DateUtil
                    .getTimeToHours(course.endTime) + "\n"
                    + getString(R.string.offline_sign_course, course.courseName) + "\n"
                    + getString(R.string.offline_sign_teacher, course.teacherName);
            list.add(new RadioItem(course.courseCode, text, 0));
        }
        return list;
    }

    @OnClick({R.id.btn_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign:
                String courseCode = viewRadio.getCurKey();
                OffLineApi.assign(getToken(), courseCode, planCode, getNewHandler(1, ResultBase
                        .class));
//                toast(courseCode + "==" + planCode);
                break;
        }
    }
}
