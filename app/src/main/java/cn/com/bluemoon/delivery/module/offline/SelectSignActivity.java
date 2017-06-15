package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BMRadioListView;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;
import cn.com.bluemoon.lib_widget.module.form.entity.RadioItem;
import cn.com.bluemoon.delivery.utils.DateUtil;

public class SelectSignActivity extends BaseActivity implements BMRadioListView.ClickListener {

    @Bind(R.id.view_radio)
    BMRadioListView viewRadio;
    @Bind(R.id.layout_room)
    BmCellTextView layoutRoom;
    @Bind(R.id.layout_sign_date)
    BmCellTextView layoutSignDate;
    @Bind(R.id.btn_sign)
    BMAngleBtn1View btnSign;

    private ResultSignDetail.SignDetailData data;

    public static void actionStart(Activity context, ResultSignDetail.SignDetailData data, int
            requestCode) {
        Intent intent = new Intent(context, SelectSignActivity.class);
        intent.putExtra("data", data);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        data = (ResultSignDetail.SignDetailData) getIntent().getSerializableExtra("data");
        if (data == null) {
            finish();
        }
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
        viewRadio.setListener(this);
    }

    @Override
    public void initData() {
        if (data == null) return;
        layoutRoom.setContentText(data.room);
        layoutSignDate.setContentText(DateUtil.getTime(data.date));
        viewRadio.setData(getRadioList(data.courses));
    }

    private void checkSignButton() {
        btnSign.setEnabled(viewRadio.getValue() != null);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        toast(result.getResponseMsg());
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        if (45101 == result.getResponseCode() || 45104 == result.getResponseCode()) {
            setResult(RESULT_OK);
            finish();
        }
    }

    //数据转化
    private List<RadioItem> getRadioList(List<ResultSignDetail.SignDetailData.Course> courses) {
        List<RadioItem> list = new ArrayList<>();
        for (ResultSignDetail.SignDetailData.Course course : courses) {
            String text = DateUtil.getTimeToHours(course.startTime) + "-" + DateUtil
                    .getTimeToHours(course.endTime) + "\n"
                    + getString(R.string.offline_sign_course, course.courseName) + "\n"
                    + getString(R.string.offline_sign_teacher, course.teacherName);
            list.add(new RadioItem(course, text, course.isSign==1?-1:0));
        }
        return list;
    }

    @OnClick(R.id.btn_sign)
    public void onClick() {
        if (viewRadio.getValue() instanceof ResultSignDetail.SignDetailData.Course) {
            ResultSignDetail.SignDetailData.Course course = (ResultSignDetail.SignDetailData
                    .Course) viewRadio.getValue();
            OffLineApi.sign(getToken(), course.courseCode, course.planCode, getNewHandler(0,
                    ResultBase.class));
        }
    }

    @Override
    public void onSelected(int position, Object value) {
        checkSignButton();
    }

    @Override
    public void onClickDisable(int position, Object value) {
        if (value instanceof ResultSignDetail.SignDetailData.Course) {
            ResultSignDetail.SignDetailData.Course obj = (ResultSignDetail.SignDetailData.Course)
                    value;
            toast(obj.message);
        }
    }
}
