package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.EvaluateDetail;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultEvaluateDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.common.BMAngleBtn1View;
import cn.com.bluemoon.delivery.ui.common.BMFieldParagraphView;
import cn.com.bluemoon.delivery.ui.common.BmCellTextView;
import cn.com.bluemoon.delivery.ui.common.BmRankStar1;
import cn.com.bluemoon.delivery.utils.DateUtil;

public class EvaluateEditActivity extends BaseActivity implements BMFieldParagraphView
        .FieldListener, BmRankStar1.RatingListener {

    @Bind(R.id.view_name)
    BmCellTextView viewName;
    @Bind(R.id.view_time)
    BmCellTextView viewTime;
    @Bind(R.id.view_teacher)
    BmCellTextView viewTeacher;
    @Bind(R.id.view_star_content)
    BmRankStar1 viewStarContent;
    @Bind(R.id.view_star_teacher)
    BmRankStar1 viewStarTeacher;
    @Bind(R.id.view_suggest)
    BMFieldParagraphView viewSuggest;
    @Bind(R.id.btn_submit)
    BMAngleBtn1View btnSubmit;
    private String courseCode;
    private String planCode;

    private EvaluateDetail detail;

    public static void startAction(Context context, String courseCode, String planCode) {
        Intent intent = new Intent(context, EvaluateEditActivity.class);
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
        return R.layout.activity_evaluate_edit;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_evaluate);
    }

    @Override
    public void initView() {
        viewSuggest.setListener(this);
        viewStarContent.setListener(this);
        viewStarTeacher.setListener(this);
        showWaitDialog();
        OffLineApi.evaluateDetail(getToken(), courseCode, planCode, getNewHandler(0,
                ResultEvaluateDetail.class));
    }

    @Override
    public void initData() {
        detail = new ResultEvaluateDetail().evaluateDetail;
        if (detail != null) {
            viewName.setContentText(detail.courseName);
            viewTime.setContentText(DateUtil.getTimes(detail.startTime, detail.endTime));
            viewTeacher.setContentText(detail.teacherName);
            viewStarContent.setRating(detail.courseStar);
            viewStarTeacher.setRating(detail.teacherStar);
            viewSuggest.setContent(detail.comment);
        }
        checkBtn();
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    private void checkBtn() {
        if (viewStarContent.getRating() > 0 && viewStarTeacher.getRating() > 0 && viewSuggest
                .getContent().length() > 0) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        // TODO: 2017/6/3
//        showWaitDialog();
//        OffLineApi.evaluate(getToken(), viewSuggest.getContent(), courseCode, viewStarContent
//                .getRating(), planCode, viewStarTeacher.getRating(), getNewHandler(0, ResultBase
//                .class));
        toast(viewStarContent.getRating() + "=" + viewStarTeacher.getRating() + "\n" +
                viewSuggest.getContent());
    }

    @Override
    public void onChangeText(String text) {
        checkBtn();
    }

    @Override
    public void onRatingBarChange() {
        checkBtn();
    }
}
