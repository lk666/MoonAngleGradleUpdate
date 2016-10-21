package cn.com.bluemoon.delivery.common.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 *检测code是否与扫描二维码一样，返回码的内容key值为Constants.RESULT_CODE
 */
public class ScanCheckCodeActivity extends BaseScanCodeActivity {

    /**
     * 扫描界面调起方法
     * @param context
     * @param title 界面标题，默认标题为“扫一扫”
     * @param btnString 手动输入按钮的文字，null则不显示
     * @param code 标题下面的编码，null则不显示
     * @param requestCode
     */
    
    public static void actStart(Activity context,String title, String btnString,String code, int requestCode) {
        actStart(context, title, btnString, code, ScanCheckCodeActivity.class, requestCode);
    }

    public static void actStart(Fragment fragment, String title, String btnString,String code, int requestCode) {
        actStart(fragment, title, btnString, code, ScanCheckCodeActivity.class, requestCode);
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if(!str.equals(getCode())){
            startDelay();
        }else{
            finishWithData(str,type);
        }
    }

}
