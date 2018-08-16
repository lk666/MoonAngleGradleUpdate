package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;

/**
 * 排课详情
 */
public class TeacherCourseSchedulingActivity extends BaseActivity {

    private String planCode;

    public static void startAction(Context context,String planCode) {
        Intent intent = new Intent(context, TeacherCourseSchedulingActivity.class);
        intent.putExtra("planCode", planCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        planCode=getIntent().getStringExtra("planCode");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_plan_detail);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_course_scheduling;
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
}
