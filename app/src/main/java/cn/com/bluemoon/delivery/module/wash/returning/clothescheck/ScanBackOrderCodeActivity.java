package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * 扫描还衣单标签
 */
public class ScanBackOrderCodeActivity extends BaseScanCodeActivity {

    private static final int REQUEST_CODE = 0x777;
    private String backOrderCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment) {
        Intent intent = new Intent(context, ScanBackOrderCodeActivity.class);
        intent.putExtra("title", context.getString(R.string
                .scan_back_order_code));
        intent.putExtra("btnString", context.getString(R.string
                .with_order_collect_manual_input_code_btn));
        if (fragment != null) {
            fragment.startActivity(intent);
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        backOrderCode = str;
        ReturningApi.scanCheckBackOrder(str, "", getToken(), getNewHandler
                (REQUEST_CODE, ResultBase.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ClothesListDetailActivity.actionStart(this, backOrderCode);
    }
}