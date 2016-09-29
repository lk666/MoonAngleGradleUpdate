package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * 扫描衣物箱标签
 */
public class ScanBoxCodeActivity extends BaseScanCodeActivity {

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment, String boxCode,
                                   int requestCode) {
        actStart(context, fragment, context.getString(R.string.close_box_scan_box_code_title),
                context.getString(R.string.with_order_collect_manual_input_code_btn),
                boxCode, ScanBoxCodeActivity.class, requestCode);
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (str != null && str.equals(getCode())) {
            ClothesBoxBackOrderListActivity.actionStart(this, getCode());
            finish();
        } else {
            toast(String.format(getString(R.string.close_box_scan_box_code_error), getCode()));
        }
    }
}