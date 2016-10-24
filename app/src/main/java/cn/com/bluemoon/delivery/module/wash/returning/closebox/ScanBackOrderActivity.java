package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * 扫描还衣单标签
 */
public class ScanBackOrderActivity extends BaseScanCodeActivity {

    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE = 0x777;
    private static final String EXTRA_BOX_CODE = "EXTRA_BOX_CODE";

    private ArrayList<BackOrderItem> list = new ArrayList<>();
    private String boxCode;
    private String backOrderCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment, int requestCode,
                                   String boxCode, ArrayList<BackOrderItem> list) {
        Intent intent = new Intent(context, ScanBackOrderActivity.class);
        intent.putExtra("title", context.getString(R.string
                .close_box_scan_back_code_title));
        intent.putExtra("code", String.format(context.getString(R.string
                .close_box_scan_back_code), boxCode));
        intent.putExtra("btnString", context.getString(R.string
                .with_order_collect_manual_input_code_btn));
        intent.putExtra(EXTRA_LIST, list);
        intent.putExtra(EXTRA_BOX_CODE, boxCode);
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            context.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent().hasExtra(EXTRA_LIST)) {
            list = (ArrayList<BackOrderItem>) getIntent().getSerializableExtra(EXTRA_LIST);
        }
        if (getIntent().hasExtra(EXTRA_BOX_CODE)) {
            boxCode = getIntent().getStringExtra(EXTRA_BOX_CODE);
        }
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (list == null) {
            finish();
        }

        if (check(str)) {
            // 服务端校验
            showWaitDialog();
            backOrderCode = str;
            ReturningApi.scanBackOrder(str, boxCode, getToken(), getNewHandler
                    (REQUEST_CODE, ResultBase.class));
        } else {
            checkFinished();
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        for (BackOrderItem item : list) {
            if (item.code.equals(backOrderCode)) {
                item.state = 1;
                break;
            }
        }

        toast(getString(R.string.scan_succeed));
        checkFinished();
    }

    /**
     * 判断是否已完成
     */
    private void checkFinished() {
        if (isScanFinished()) {
            toast(getString(R.string.scan_finish));
            finish();
        } else {
            startDelay();
        }
    }

    /**
     * 本地检查
     *
     * @return 是否继续服务端校验
     */
    private boolean check(String code) {
        boolean isIn = false;
        for (BackOrderItem item : list) {
            if (item.code.equals(code)) {
                isIn = true;
                if (item.state == 1) {
                    toast(getString(R.string.duplicate_code));
                    return false;
                }
                break;
            }
        }
        if (!isIn) {
            toast(getString(R.string.not_in_code));
            return false;
        }
        return true;
    }

    /**
     * 是否已完成扫描
     */
    private boolean isScanFinished() {
        for (BackOrderItem item : list) {
            if (item.state != 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra(EXTRA_LIST, list);
        setResult(Activity.RESULT_OK, i);
        super.finish();
    }
}