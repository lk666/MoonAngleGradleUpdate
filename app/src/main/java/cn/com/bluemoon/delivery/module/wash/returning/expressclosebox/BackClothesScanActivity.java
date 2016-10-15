package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.expressclosebox.ResultScanBackOrder;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * Created by ljl on 2016/10/15.
 */
public class BackClothesScanActivity extends BaseScanCodeActivity{
    private List<String> codes = new ArrayList<>();
    private static String boxCode;
    private String backCode;
    /**
     * Activity调起扫描界面方法
     * @param context
     * @param title 界面标题，默认标题为“扫一扫”
     * @param btnString 手动输入按钮的文字，null则不显示
     * @param code 标题下面的编码，null则不显示
     * @param requestCode
     */
    public static void actStart(Fragment fragment, String title, String btnString, String code, String boxCode, int requestCode) {
        actStart(null, fragment, title, btnString, code, BackClothesScanActivity.class, requestCode);
        BackClothesScanActivity.boxCode = boxCode;
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (codes.contains(str)) {
            toast(getString(R.string.duplicate_code));
            resumeScan();
            return;
        }
        backCode = str;
        showWaitDialog();
        ReturningApi.scanExpressBackOrder(str, boxCode, getToken(), getNewHandler(1, ResultScanBackOrder.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultScanBackOrder resultScanBackOrder = (ResultScanBackOrder) result;
        BackClothesScanActivity.boxCode = resultScanBackOrder.getBoxCode();
        codes.add(backCode);
        toast(getString(R.string.scan_succeed));
        resumeScan();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("codes", (ArrayList<String>) codes);
        intent.putExtra("boxCode", boxCode);
        setResult(RESULT_OK, intent);
        super.finish();
    }

}
