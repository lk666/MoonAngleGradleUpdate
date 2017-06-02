package cn.com.bluemoon.delivery.module.offline;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.common.BMAngleBtn3View;
import cn.com.bluemoon.delivery.ui.common.BmCellParagraphView;
import cn.com.bluemoon.delivery.ui.common.BmCellTextView;

public class StudentDetailActivity extends BaseActivity {

    @Bind(R.id.view_name)
    BmCellTextView viewName;
    @Bind(R.id.view_state)
    BmCellTextView viewState;
    @Bind(R.id.view_time)
    BmCellTextView viewTime;
    @Bind(R.id.view_code)
    BmCellTextView viewCode;
    @Bind(R.id.view_theme)
    BmCellTextView viewTheme;
    @Bind(R.id.view_teacher)
    BmCellTextView viewTeacher;
    @Bind(R.id.img_teacher)
    ImageView imgTeacher;
    @Bind(R.id.view_contacts)
    BmCellTextView viewContacts;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.view_room)
    BmCellTextView viewRoom;
    @Bind(R.id.view_address)
    BmCellTextView viewAddress;
    @Bind(R.id.view_purpose)
    BmCellParagraphView viewPurpose;
    @Bind(R.id.btn_evaluate)
    BMAngleBtn3View btnEvaluate;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_course_detail_title);
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

    @OnClick({R.id.img_teacher, R.id.txt_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_teacher:
                break;
            case R.id.txt_phone:
                break;
        }
    }
}
