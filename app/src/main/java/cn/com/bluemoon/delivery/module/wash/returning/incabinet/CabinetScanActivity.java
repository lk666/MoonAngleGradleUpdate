package cn.com.bluemoon.delivery.module.wash.returning.incabinet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;


public class CabinetScanActivity extends BaseScanCodeActivity {

    private String code;


    public static void actStart(Activity context, Fragment fragment, String title, String btnString, String code, int requestCode) {
        actStart(context, fragment, title, btnString, code, CabinetScanActivity.class, requestCode);
    }

    @Override
    protected void onBtnClick(View view) {
        toast("跳转到手动输入");
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        //暂停扫描
        pauseScan();
        if (TextUtils.isEmpty(code)) {
            code = str;
            setTxtCode(str);
        } else if (!code.equals(str)) {
            toast("请扫描：" + code);
        } else {
            toast(str + "入柜成功");
            code = null;
        }
        //重新调起扫描
        resumeScan();
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
    }
}
