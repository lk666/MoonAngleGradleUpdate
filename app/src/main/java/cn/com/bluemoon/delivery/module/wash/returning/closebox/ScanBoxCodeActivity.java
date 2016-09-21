package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.graphics.Bitmap;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * 扫描衣物箱标签
 */
public class ScanBoxCodeActivity extends BaseScanCodeActivity {


    @Override
    protected void onResult(String str, String type, Bitmap barcode) {

        if (str != null && str.equals(getCode())) {
//// TODO: lk 2016/9/21 跳到清点还衣单
            ClothesBoxBackOrderListActivity.actionStart(this, getCode());
            finish();
        } else {
            toast(String.format(getString(R.string.close_box_scan_box_code_error), getCode()));
        }
    }
}