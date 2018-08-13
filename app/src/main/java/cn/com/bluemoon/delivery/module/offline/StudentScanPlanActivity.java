package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.choice.BMCheckList2View;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.choice.entity.RadioItem;
import cn.com.bluemoon.lib_widget.module.choice.interf.CheckListener;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

public class StudentScanPlanActivity extends BaseActivity implements CheckListener {

    @Bind(R.id.view_all)
    BMRadioItemView viewAll;
    @Bind(R.id.btn_sign)
    BMAngleBtn3View btnSign;
    @Bind(R.id.view_date)
    BmCellTextView viewDate;
    @Bind(R.id.view_theme)
    BmCellTextView viewTheme;
    @Bind(R.id.list_check)
    BMCheckList2View listCheck;
    @Bind(R.id.view_empty)
    TextView viewEmpty;
    @Bind(R.id.view_title)
    TextView viewTitle;

    private String planCode;
    private ResultSignDetail.SignDetailData data;

    public static void actionStart(Activity context, ResultSignDetail.SignDetailData data, int
            requestCode) {
        Intent intent = new Intent(context, SelectSignActivity.class);
        intent.putExtra("data", data);
        context.startActivityForResult(intent, requestCode);
    }

    //统一扫一扫进来
    public static void actionStart(Activity context, String planCode, int requestCode) {
        Intent intent = new Intent(context, SelectSignActivity.class);
        intent.putExtra("planCode", planCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        data = (ResultSignDetail.SignDetailData) getIntent().getSerializableExtra("data");
        planCode = getIntent().getStringExtra("planCode");
        if (data == null && TextUtils.isEmpty(planCode)) {
            finish();
        }
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_sign);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_scan_plan;
    }

    @Override
    public void initView() {
        //默认初始状态，全选不可点击
        viewAll.refresh(BMRadioItemView.TYPE_DISABLE);
        listCheck.setListener(this);
    }

    @Override
    public void initData() {
        if (data != null) {
            planCode = data.planInfo.planCode;
            setData();
        } else {
            OffLineApi.signDetail(getToken(), planCode, getNewHandler(0,
                    ResultSignDetail.class));
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            data = ((ResultSignDetail) result).data;
            setData();
        } else if (requestCode == 1) {
            toast(result.getResponseMsg());
            finish();
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        // TODO: 2018/8/13 错误码需要更新
        if (requestCode == 1 && result.getResponseCode() == 1) {
            DialogUtil.getMsgDialog(this, result.getResponseMsg(), getString(R.string.btn_good))
                    .show();
            OffLineApi.signDetail(getToken(), planCode, getNewHandler(0,
                    ResultSignDetail.class));
        } else {
            super.onErrorResponse(requestCode, result);
        }

    }

    /**
     * 设置界面显示数据
     */
    private void setData() {
        viewTheme.setContentText(data.planInfo.topic);
        viewDate.setContentText(DateUtil.getTime(data.planInfo.nowDate));

        //单列表数据大于0时才显示列表
        if (data.courses.size() > 0) {
            ViewUtil.setViewVisibility(viewEmpty, View.GONE);
            ViewUtil.setViewVisibility(viewTitle, View.VISIBLE);
            ViewUtil.setViewVisibility(listCheck, View.VISIBLE);
            listCheck.setData(getListData(data.courses));
        }
    }

    /**
     * 获取多选项的显示列表
     */
    private List<RadioItem> getListData(List<ResultSignDetail.SignDetailData.Course> courses) {
        List<RadioItem> list = new ArrayList<>();
        for (ResultSignDetail.SignDetailData.Course course : courses) {
            String text = DateUtil.getTimeToHours(course.startTime) + "-" + DateUtil
                    .getTimeToHours(course.endTime) + "\n"
                    + getString(R.string.offline_sign_course, course.courseName) + "\n"
                    + getString(R.string.offline_sign_teacher, course.teacherName) + "\n"
                    + getString(R.string.offline_sign_room, course.room);
            int type = course.isSign == 1 ? BMRadioItemView.TYPE_DISABLE : BMRadioItemView
                    .TYPE_NORMAL;
            list.add(new RadioItem(course, text, type));
        }
        return list;
    }

    @OnClick({R.id.view_all, R.id.btn_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_all:
                if (viewAll.getType() == BMRadioItemView.TYPE_NORMAL) {
                    listCheck.selectAll();
                } else if (viewAll.getType() == BMRadioItemView.TYPE_SELECT) {
                    listCheck.clearAll();
                }
                break;
            case R.id.btn_sign:
                List<String> list = new ArrayList<>();
                for (int i : listCheck.getValues()) {
                    if (i >= 0 && i < data.courses.size()) {
                        list.add(data.courses.get(i).courseCode);
                    }
                }
                showWaitDialog();
                OffLineApi.sign(getToken(), null, list, planCode, getNewHandler(1, ResultBase
                        .class));
                break;
        }
    }

    @Override
    public void onSelected(View view, int position) {
    }

    @Override
    public void onCancel(View view, int position) {
    }

    @Override
    public void onClickDisable(View view, int position) {
        toast(data.courses.get(position).message);
    }

    @Override
    public void onRefresh(View view, int btnAllType, int selectedNum, int normalNum, int
            disableNum) {
        //更新全选按钮状态
        viewAll.refresh(btnAllType);
        //更新签到按钮状态
        if (selectedNum > 0) {
            if (!btnSign.isEnabled()) {
                btnSign.setEnabled(true);
            }
        } else if (btnSign.isEnabled()) {
            btnSign.setEnabled(false);
        }
    }
}
