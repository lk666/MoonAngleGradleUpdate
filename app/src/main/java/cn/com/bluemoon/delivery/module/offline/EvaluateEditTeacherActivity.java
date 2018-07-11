package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.EvaluateDetail;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultEvaluateDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldParagraphView;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;
import cn.com.bluemoon.lib_widget.module.form.interf.BMFieldListener;
import cn.com.bluemoon.delivery.utils.DateUtil;

public class EvaluateEditTeacherActivity extends BaseActivity implements BMFieldListener {

    @BindView(R.id.view_student)
    BmCellTextView viewStudent;
    @BindView(R.id.view_name)
    BmCellTextView viewName;
    @BindView(R.id.view_time)
    BmCellTextView viewTime;
    @BindView(R.id.view_sign_time)
    BmCellTextView viewSignTime;
    @BindView(R.id.view_score)
    BMFieldText1View viewScore;
    @BindView(R.id.view_suggest)
    BMFieldParagraphView viewSuggest;
    @BindView(R.id.btn_submit)
    BMAngleBtn1View btnSubmit;
    private String courseCode;
    private String planCode;
    private String studentCode;
    private String studentName;

    private EvaluateDetail detail;

    public static void startAction(Activity context, int requestCode, String courseCode, String
            planCode, String
            studentCode, String studentName) {
        Intent intent = new Intent(context, EvaluateEditTeacherActivity.class);
        intent.putExtra("courseCode", courseCode);
        intent.putExtra("planCode", planCode);
        intent.putExtra("studentCode", studentCode);
        intent.putExtra("studentName", studentName);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        courseCode = getIntent().getStringExtra("courseCode");
        planCode = getIntent().getStringExtra("planCode");
        studentCode = getIntent().getStringExtra("studentCode");
        studentName = getIntent().getStringExtra("studentName");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_evaluate_edit_teacher;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_student_evaluate);
    }

    @Override
    public void initView() {
        viewScore.setInputType(InputType.TYPE_CLASS_NUMBER);
        viewScore.setListener(this);
        viewSuggest.setListener(this);
        showWaitDialog();
        OffLineApi.teacherGetEvaluateDetail(getToken(), courseCode, planCode, studentCode,
                getNewHandler(0, ResultEvaluateDetail.class));
    }

    @Override
    public void initData() {
        if (detail != null) {
            viewStudent.setContentText(studentName + " " + studentCode);
            viewName.setContentText(detail.courseName);
            viewTime.setContentText(DateUtil.getTimes(detail.startTime, detail.endTime));
            viewSignTime.setContentText(DateUtil.getTimeToYMDHM(detail.signTime));
            if (detail.score >= 0) {
                viewScore.setContent(String.valueOf(detail.score));
            }
            viewSuggest.setContent(detail.comment);
        }
        checkBtn();
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                ResultEvaluateDetail evaluateDetail = (ResultEvaluateDetail) result;
                detail = evaluateDetail.data;
                initData();
                break;
            case 1:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    private void checkBtn() {
        if (detail != null && viewSuggest.getContent().length() > 0) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }

    private int getScore() {
        if (viewScore.getContent().length() > 0) {
            return Integer.valueOf(viewScore.getContent());
        }
        return -1;
    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        showWaitDialog();
        OffLineApi.teacherEvaluate(getToken(), viewSuggest.getContent(), courseCode, planCode,
                getScore(), studentCode, studentName, getNewHandler(1, ResultBase
                        .class));
    }

    @Override
    public void afterTextChanged(View view, String text) {
        if (view == viewScore && !TextUtils.isEmpty(text) && Integer.parseInt(text) > 100) {
            viewScore.setContent("100");
            return;
        }
        checkBtn();
    }
}
