package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;

import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.delivery.utils.Constants;


public class ScanCodeActivity extends BaseScanCodeActivity {

    public static void actStart(Activity context, Fragment fragment, String title, String btnString,String code, int requestCode) {
        actStart(context,fragment,title,btnString,code,ScanCodeActivity.class,requestCode);
    }

    @Override
    protected void onBtnClick(View view) {
        setResult(Constants.RESULT_SCAN);
        finish();
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        finishWithData(str,type);
    }
}
