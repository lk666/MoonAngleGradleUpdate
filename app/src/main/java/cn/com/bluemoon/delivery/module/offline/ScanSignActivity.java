package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignDetail;
import cn.com.bluemoon.delivery.common.qrcode.ScanActivity;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;

public class ScanSignActivity extends ScanActivity {

    public static void actStart(Activity context, int requestCode) {
        ViewUtil.showActivityForResult(context, ScanSignActivity.class, requestCode);
    }


    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        String code = OfflineUtil.getUrlParamsByCode(str);
        if (TextUtils.isEmpty(code)) {
            toast(getString(R.string.scan_fail));
            startDelay();
            return;
        }
        showWaitDialog();
        OffLineApi.signDetail(getToken(), code, getNewHandler(0, ResultSignDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (result == null) {
            startDelay();
            return;
        }
        ResultSignDetail.SignDetailData data = ((ResultSignDetail) result).data;
        if (data.courses != null && !data.courses.isEmpty()) {
            if ("room".equals(data.type)) {
                SelectSignActivity.actionStart(ScanSignActivity.this, data, 1);
            } else if ("plan".equals(data.type)) {
                //排课签到
                StudentScanPlanActivity.actionStart(ScanSignActivity.this, data, 1);
            }
        } else {
            DialogUtil.getMessageDialog(this, null, getString(R.string.offline_sign_enable),
                    getString(R.string.btn_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDelay();
                        }
                    }).setCancelable(false).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

}
