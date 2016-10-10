package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 扫描还衣单标签(还衣单清点)
 */
public class ScanBackOrderActivity extends BaseScanCodeActivity {

    private static final String EXTRA_TAG_CODE = "EXTRA_TAG_CODE";
    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE_SCAN_BACK_ORDER = 0x777;

    private ArrayList<CheckBackOrder> list = new ArrayList<>();
    private String tagCode;
    private String backOrderCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment, int requestCode,
                                   String tagCode, ArrayList<CheckBackOrder> list) {
        Intent intent = new Intent(context, ScanBackOrderActivity.class);
        intent.putExtra("title", context.getString(R.string
                .close_box_scan_back_code_title));
        intent.putExtra("btnString", context.getString(R.string
                .with_order_collect_manual_input_code_btn));
        intent.putExtra(EXTRA_LIST, list);
        intent.putExtra(EXTRA_TAG_CODE, tagCode);
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
            list = (ArrayList<CheckBackOrder>) getIntent().getSerializableExtra(EXTRA_LIST);
        }
        if (getIntent().hasExtra(EXTRA_TAG_CODE)) {
            tagCode = getIntent().getStringExtra(EXTRA_TAG_CODE);
        }
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (TextUtils.isEmpty(str)) {
            toast(getString(R.string.scan_fail));
            return;
        }

        if (list == null) {
            finish();
            return;
        }

        if (check(str)) {
            // 服务端校验(8.9)
            showWaitDialog();
            backOrderCode = str;
            ReturningApi.scanCheckBackOrder(backOrderCode, tagCode, getToken(), getNewHandler
                    (REQUEST_CODE_SCAN_BACK_ORDER, ResultBase.class));
        } else {
            checkFinished();
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 8.9衣物清点/还衣单清点-还衣单扫描校验
            case REQUEST_CODE_SCAN_BACK_ORDER:
                CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(this);
                dialog.setMessage(R.string.scan_back_order_detail_dialog_msg);
                dialog.setTitle(R.string.scan_succeed);
                dialog.setPositiveButton(R.string.scan_back_order_detail_dialog_abnormal,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 异常记录
                                showAbnormalDialog();
                            }
                        });
                dialog.setNegativeButton(R.string.continue_scan, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (CheckBackOrder item : list) {
                            if (item.getBackOrderCode().equals(backOrderCode)) {
                                item.setCheckStatus(CheckBackOrder.NORMAL);
                                break;
                            }
                        }

                        checkFinished();
                    }
                });
                dialog.setPositiveButtonTextColor(getResources().getColor(R.color.text_red));
                dialog.show();
                break;
        }
    }

    /**
     * 显示异常记录弹窗
     */
    private void showAbnormalDialog() {
        // TODO: lk 2016/10/10  
    }

    /**
     * 判断是否已完成
     */
    private void checkFinished() {
        if (isScanFinished()) {
            toast(getString(R.string.back_order_check_finish));
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
        for (CheckBackOrder item : list) {
            if (item.getBackOrderCode().equals(code)) {
                isIn = true;

                if (CheckBackOrder.EXCEPTION.equals(item.getCheckStatus())) {
                    toast(getString(R.string.duplicate_abnormal));
                    return false;
                } else if (CheckBackOrder.NORMAL.equals(item.getCheckStatus())) {
                    toast(getString(R.string.duplicate_code));
                    return false;
                }
                break;
            }
        }
        if (!isIn) {
            toast(getString(R.string.back_order_check_not_in));
            return false;
        }
        return true;
    }

    /**
     * 是否已完成扫描
     */
    private boolean isScanFinished() {
        for (CheckBackOrder item : list) {
            if (CheckBackOrder.NONEXIST.equals(item.getCheckStatus())) {
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