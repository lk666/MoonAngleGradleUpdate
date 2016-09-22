package cn.com.bluemoon.delivery.module.base.example;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 *返回码的内容key值为Constants.RESULT_CODE
 */
public class TestScanCodeActivity extends BaseScanCodeActivity {

    /**
     * Activity调起扫描界面方法
     * @param context
     * @param title 界面标题，默认标题为“扫一扫”
     * @param btnString 手动输入按钮的文字，null则不显示
     * @param code 标题下面的编码，null则不显示
     * @param requestCode
     */
    public static void actStart(Activity context,String title, String btnString,String code, int requestCode) {
        actStart(context, title, btnString, code, TestScanCodeActivity.class, requestCode);
    }

    /**
     * fragment调起扫描界面
     */
    public static void actStart(Fragment fragment,String title, String btnString,String code, int requestCode) {
        actStart(fragment, title, btnString, code, TestScanCodeActivity.class, requestCode);
    }

    /**
     * 返回结果事件重写
     * @param str  返回的扫描内容
     * @param type 二维码类型
     * @param barcode 扫描区域的图像
     */
    @Override
    protected void onResult(String str, String type, Bitmap barcode) {

        /*不处理，直接将结果onResult返回
        finishWithData(str,type);*/

        /*需要连续扫描，则在此方法中处理，重新调起*/
        //此处可以调用api
        DeliveryApi.checkScanCode(getToken(),str,getNewHandler(0, ResultBase.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        //处理完结果
        toast(result.getResponseMsg());
        //重启扫描，可调用startDelay(time)方法，默认延时1秒
        startDelay();
    }


    //可重写手动输入的处理操作，默认已处理
    @Override
    protected void onBtnClick(View view) {
        super.onBtnClick(view);
    }
}
