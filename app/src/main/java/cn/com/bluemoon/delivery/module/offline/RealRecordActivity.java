package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultRecord;
import cn.com.bluemoon.delivery.app.api.model.offline.request.RecordData;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.selectordialog.OnButtonClickListener;
import cn.com.bluemoon.delivery.ui.selectordialog.TimeSelectDialog;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldParagraphView;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

public class RealRecordActivity extends BaseActivity {

    @BindView(R.id.view_name)
    BmCellTextView viewName;
    @BindView(R.id.view_date)
    BmCellTextView viewDate;
    @BindView(R.id.time_start)
    TextView timeStart;
    @BindView(R.id.time_end)
    TextView timeEnd;
    @BindView(R.id.view_comment)
    BMFieldParagraphView viewComment;
    private String courseCode;
    private String planCode;
    private RecordData data;

    public static void actionStart(Context context, String courseCode, String planCode) {
        Intent intent = new Intent(context, RealRecordActivity.class);
        intent.putExtra("courseCode", courseCode);
        intent.putExtra("planCode", planCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        courseCode = getIntent().getStringExtra("courseCode");
        planCode = getIntent().getStringExtra("planCode");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_real_record;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_record_title);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        showWaitDialog();
        OffLineApi.recordDetail(getToken(), courseCode, planCode, getNewHandler(0, ResultRecord
                .class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            data = ((ResultRecord) result).data;
            viewName.setContentText(data.courseName);
            viewDate.setContentText(DateUtil.getTime(data.date));
            timeStart.setText(DateUtil.getTimeToHours(data.realStartTime));
            timeEnd.setText(DateUtil.getTimeToHours(data.realEndTime));
            viewComment.setContent(data.comment);
        } else if (requestCode == 1) {
            toast(result.getResponseMsg());
            setResult(RESULT_OK);
            finish();
        }
    }

    @OnClick({R.id.time_start, R.id.time_end, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_start:
                showTimeDialog(true);
                break;
            case R.id.time_end:
                showTimeDialog(false);
                break;
            case R.id.btn_submit:
                submitData();
                break;
        }
    }

    /**
     * 提交时间
     */
    private void submitData() {
        if (data == null) return;
        if (data.realStartTime > data.realEndTime) {
            toast(getString(R.string.offline_record_time_compare));
            return;
        }
        data.comment = viewComment.getContent();
        showWaitDialog();
        OffLineApi.record(getToken(), data, getNewHandler(1, ResultBase.class));
    }

    /**
     * 展示时间对话框
     * @param isStart
     */
    private void showTimeDialog(final boolean isStart) {
        if (data == null) return;
        long time = isStart ? data.realStartTime : data.realEndTime;
        new TimeSelectDialog(this, "", time, new OnButtonClickListener() {
            @Override
            public void onOKButtonClick(long timeStamp) {
                if (isStart) {
                    data.realStartTime = timeStamp;
                    timeStart.setText(DateUtil.getTimeToHours(timeStamp));
                } else {
                    data.realEndTime = timeStamp;
                    timeEnd.setText(DateUtil.getTimeToHours(timeStamp));
                }
            }

            @Override
            public void onCancleButtonClick() {

            }

            @Override
            public String getCompareTips() {
                return null;
            }
        }).show();
    }
}
