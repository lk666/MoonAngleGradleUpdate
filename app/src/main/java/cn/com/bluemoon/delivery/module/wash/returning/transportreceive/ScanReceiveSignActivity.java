package cn.com.bluemoon.delivery.module.wash.returning.transportreceive;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.CarriageTag;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.delivery.utils.DialogUtil;

/**
 * 扫描封箱标签
 */
public class ScanReceiveSignActivity extends BaseScanCodeActivity {

    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE = 0x777;
    private static final String EXTRA_CARRIAGE_CODE = "EXTRA_CARRIAGE_CODE";

    private ArrayList<CarriageTag> list = new ArrayList<>();
    private String carriageCode;
    private String tagCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment, int requestCode,
                                   String carriageCode, ArrayList<CarriageTag> list) {
        Intent intent = new Intent(context, ScanReceiveSignActivity.class);
        intent.putExtra("title", context.getString(R.string
                .wait_sign_scan_tag_title));
        intent.putExtra("btnString", context.getString(R.string
                .with_order_collect_manual_input_code_btn));
        intent.putExtra(EXTRA_LIST, list);
        intent.putExtra(EXTRA_CARRIAGE_CODE, carriageCode);
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
            list = (ArrayList<CarriageTag>) getIntent().getSerializableExtra(EXTRA_LIST);
        }
        if (getIntent().hasExtra(EXTRA_CARRIAGE_CODE)) {
            carriageCode = getIntent().getStringExtra(EXTRA_CARRIAGE_CODE);
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
            tagCode = str;
            ReturningApi.scanReceiveSign(carriageCode, tagCode, getToken(), getNewHandler
                    (REQUEST_CODE, ResultBase.class));
        } else {
            checkFinished();
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        for (CarriageTag item : list) {
            if (item.getTagCode().equals(tagCode)) {
                item.setSign(true);
                break;
            }
        }

        if (isScanFinished()) {
            toast(getString(R.string.sign_finish));
            finish();
        } else {
            DialogUtil.showInfoDialog(this, getString(R.string.sign_succeed),
                    getString(R.string.back), getString(R.string.sign_continue),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDelay();
                        }
                    });
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        checkFinished();
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        checkFinished();
    }

    /**
     * 判断是否已完成
     */
    private void checkFinished() {
        if (isScanFinished()) {
            toast(getString(R.string.sign_finish));
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
        for (CarriageTag item : list) {
            if (item.getTagCode().equals(code)) {
                isIn = true;
                if (item.isSign()) {
                    toast(getString(R.string.wait_sign_duplicate_code));
                    return false;
                }
                break;
            }
        }
        if (!isIn) {
            toast(getString(R.string.wait_sign_not_in_code));
            return false;
        }
        return true;
    }

    /**
     * 是否已完成扫描
     */
    private boolean isScanFinished() {
        for (CarriageTag item : list) {
            if (!item.isSign()) {
                return false;
            }
        }
        return true;
    }
}