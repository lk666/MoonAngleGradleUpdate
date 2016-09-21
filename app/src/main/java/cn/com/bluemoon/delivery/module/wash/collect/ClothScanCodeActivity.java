package cn.com.bluemoon.delivery.module.wash.collect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;

import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.delivery.utils.Constants;


public class ClothScanCodeActivity extends BaseScanCodeActivity {

    /**
     * 扫描界面调起方法
     * @param context
     * @param fragment 由Activity调起则传null
     * @param title 界面标题，默认标题为“扫一扫”
     * @param btnString 手动输入按钮的文字，null则不显示
     * @param code 标题下面的编码，null则不显示
     * @param requestCode
     */
    public static void actStart(Activity context, Fragment fragment, String title, String btnString,String code, int requestCode) {
        actStart(context,fragment,title,btnString,code,ClothScanCodeActivity.class,requestCode);
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
