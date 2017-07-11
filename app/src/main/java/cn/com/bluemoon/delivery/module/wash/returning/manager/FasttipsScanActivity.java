package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrder;
import cn.com.bluemoon.delivery.common.qrcode.ScanCodeActivity;
import cn.com.bluemoon.delivery.module.base.BaseScanActivity;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * Created by tangqiwei on 2017/7/10.
 */

public class FasttipsScanActivity extends BaseScanCodeActivity {

    private final String TYPE = "BACK_ORDER_WAIT_SIGN";
    public static void actStart(Activity context, String title, String btnString, String code, int requestCode) {
        actStart(context, title, btnString, code, FasttipsScanActivity.class, requestCode);
    }

    public static void actStart(Fragment fragment, String title, String btnString, String code, int requestCode) {
        actStart(fragment, title, btnString, code, FasttipsScanActivity.class, requestCode);
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if(TextUtils.isEmpty(str)){
            toast(R.string.returning_qr_code_errror_hint);
            return;
        }
        showWaitDialog();
        ReturningApi.queryBackOrderList(TYPE, 0, 0, 0, str,null,null,getToken(), getNewHandler(0, ResultBackOrder.class));
    }

    @Override
    protected void onBtnClick(View view) {
        ManualQueriesActivity.actStart(this);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        hideWaitDialog();
        if(requestCode==0){
            ResultBackOrder resultBackOrder= (ResultBackOrder) result;
            SearchResultActivity.actStart(this, (ArrayList<ResultBackOrder.BackOrderListBean>) resultBackOrder.getBackOrderList());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            finish();
        }
    }
}
