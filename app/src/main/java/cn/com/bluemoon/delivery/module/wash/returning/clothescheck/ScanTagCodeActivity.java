package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.ResultCloseBoxSign;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * 扫描封箱标签
 */
public class ScanTagCodeActivity extends BaseScanCodeActivity {

    private static final int REQUEST_CODE = 0x777;
    private String tagCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment) {
        Intent intent = new Intent(context, ScanTagCodeActivity.class);
        intent.putExtra("title", context.getString(R.string
                .scan_tag));
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
        if (TextUtils.isEmpty(str)) {
            toast(getString(R.string.scan_fail));
            startDelay();
            return;
        }

        tagCode = str;
        ReturningApi.closeBoxSign(str, getToken(), getNewHandler
                (REQUEST_CODE, ResultCloseBoxSign.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultCloseBoxSign obj = (ResultCloseBoxSign) result;
        BackOrderListDetailActivity.actionStart(this, tagCode, obj.getBackOrderList());
        finish();
    }
}