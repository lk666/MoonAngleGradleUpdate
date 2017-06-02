package cn.com.bluemoon.delivery.module.offline;

import android.content.Intent;
import android.view.View;

import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.utils.LibConstants;

public class StudentActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_my_train);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (data == null) return;
                    String result = data.getStringExtra(LibConstants.SCAN_RESULT);
                    SelectSignActivity.actionStart(StudentActivity.this, result);
                    break;
            }
        }
    }


    @OnClick({R.id.btn_scan, R.id.btn_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
//                PublicUtil.openScanView(this, null, null, 0);
                SelectSignActivity.actionStart(this, "111");
                break;
            case R.id.btn_detail:
                ViewUtil.showActivity(this,StudentDetailActivity.class);
                break;
        }
    }
}
