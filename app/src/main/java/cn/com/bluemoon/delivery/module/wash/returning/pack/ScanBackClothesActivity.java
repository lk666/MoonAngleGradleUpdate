package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ClothesItem;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * Created by allenli on 2016/10/10.
 */
public class ScanBackClothesActivity extends BaseScanCodeActivity {

    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE = 0x777;
    private static final String EXTRA_CUPBOARD_CODE = "EXTRA_CUPBOARD_CODE";

    private ArrayList<ClothesItem> list = new ArrayList<>();
    private String cupboardCode;
    private String backOrderCode;

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, int requestCode,
                                   String cupboardCode, ArrayList<ClothesItem> list) {
        Intent intent = new Intent(context, ScanBackClothesActivity.class);
        intent.putExtra("title", context.getString(R.string.incabinet_cloth_title));
        intent.putExtra("btnString", context.getString(R.string
                .with_order_collect_manual_input_code_btn));
        intent.putExtra(EXTRA_LIST, list);
        intent.putExtra(EXTRA_CUPBOARD_CODE, cupboardCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent().hasExtra(EXTRA_LIST)) {
            list = (ArrayList<ClothesItem>) getIntent().getSerializableExtra(EXTRA_LIST);
        }
        if (getIntent().hasExtra(EXTRA_CUPBOARD_CODE)) {
            cupboardCode = getIntent().getStringExtra(EXTRA_CUPBOARD_CODE);
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
            ReturningApi.scanClothesCode(cupboardCode, str, getToken(), getNewHandler
                    (REQUEST_CODE, ResultBase.class));
        } else {
            checkFinished();
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        for (ClothesItem item : list) {
            if (item.getClothesCode().equalsIgnoreCase(backOrderCode)) {
                item.isCheck = true;
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
        for (ClothesItem item : list) {
            if (item.getClothesCode().equalsIgnoreCase(code)) {
                isIn = true;
                if (item.isCheck) {
                    toast(getString(R.string.pack_scan_repeat));
                    return false;
                }
                break;
            }
        }
        if (!isIn) {
            toast(getString(R.string.pack_scan_not_exist));
            return false;
        }
        return true;
    }

    /**
     * 是否已完成扫描
     */
    private boolean isScanFinished() {
        for (ClothesItem item : list) {
            if (!item.isCheck) {
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
