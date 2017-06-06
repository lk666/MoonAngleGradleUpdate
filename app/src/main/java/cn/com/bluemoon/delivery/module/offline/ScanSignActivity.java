package cn.com.bluemoon.delivery.module.offline;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultSignDetail;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.delivery.module.offline.utils.OfflineUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;

public class ScanSignActivity extends BaseScanCodeActivity {

    public static void actStart(Activity context, int requestCode) {
        ViewUtil.showActivityForResult(context, ScanSignActivity.class, requestCode);
    }


    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        String roomCode = OfflineUtil.getUrlParamsByCode(str);
        if (TextUtils.isEmpty(roomCode)) {
            toast(getString(R.string.scan_fail));
            startDelay();
            return;
        }
        showWaitDialog();
        OffLineApi.signDetail(getToken(), roomCode, getNewHandler(0, ResultSignDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if(result==null){
            startDelay();
            return;
        }
        ResultSignDetail.SignDetailData data = ((ResultSignDetail)result).data;
        SelectSignActivity.actionStart(ScanSignActivity.this,data,1);
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        startDelay();
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        startDelay();
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        startDelay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1&&resultCode==RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}
