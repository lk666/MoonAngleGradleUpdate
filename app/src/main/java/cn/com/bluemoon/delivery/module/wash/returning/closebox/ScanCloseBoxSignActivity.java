package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.CloseBoxTag;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * 扫描封箱标签
 */
public class ScanCloseBoxSignActivity extends BaseScanCodeActivity {

    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE = 0x777;
    private static final String EXTRA_BOX_CODE = "EXTRA_BOX_CODE";

    private ArrayList<CloseBoxTag> list = new ArrayList<>();
    private String boxCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment, int requestCode,
                                   String boxCode, ArrayList<CloseBoxTag> list) {
        Intent intent = new Intent(context, ScanCloseBoxSignActivity.class);
        intent.putExtra("title", context.getString(R.string
                .close_box_tag_scan_tag));
        intent.putExtra("code", context.getString(R.string
                .close_box_scan_tag_code));
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
            list = (ArrayList<CloseBoxTag>) getIntent().getSerializableExtra(EXTRA_LIST);
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
            ArrayList<String> l = new ArrayList<>();
            for (CloseBoxTag tag : list) {
                l.add(tag.getTagCode());
            }
            ReturningApi.scanCloseBoxSign(boxCode, l, getToken(), getNewHandler
                    (REQUEST_CODE, ResultBase.class));
        } else {
            startDelay();
        }
    }

    /**
     * 本地检查
     *
     * @return 是否已完成本地校验
     */
    private boolean check(String code) {
        // 扫的码是否正确
        boolean isIn = false;
        for (CloseBoxTag item : list) {
            if (item.getTagCode().equals(code)) {
                isIn = true;
                if (item.isScaned()) {
                    toast(getString(R.string.duplicate_tag_code));
                    return false;
                }
                item.setScaned(true);
                break;
            }
        }
        if (!isIn) {
            toast(getString(R.string.not_in_tag_code));
            return false;
        }

        toast(String.format(getString(R.string.close_box_success), code));

        // 是否全部扫完
        return isScanFinished();
    }

    /**
     * 是否已完成扫描
     */
    private boolean isScanFinished() {
        for (CloseBoxTag item : list) {
            if (!item.isScaned()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        toast(getString(R.string.close_box_finish));
        setResult(RESULT_OK);
        finish();
    }
}