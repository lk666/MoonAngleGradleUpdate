package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.clothescheck.Clothes;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;

/**
 * 扫描衣物编码界面
 */
public class ScanClothesCodeActivity extends BaseScanCodeActivity {

    public static final String EXTRA_LIST = "LIST";
    private static final int REQUEST_CODE = 0x777;

    private ArrayList<Clothes> list = new ArrayList<>();

    /**
     * 扫描界面调起方法
     */
    public static void actionStart(Activity context, Fragment fragment, int requestCode,
                                   ArrayList<Clothes> list) {
        Intent intent = new Intent(context, ScanClothesCodeActivity.class);
        intent.putExtra("title", context.getString(R.string
                .incabinet_cloth_title));
        intent.putExtra("btnString", context.getString(R.string
                .with_order_collect_manual_input_code_btn));
        intent.putExtra(EXTRA_LIST, list);
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
            list = (ArrayList<Clothes>) getIntent().getSerializableExtra(EXTRA_LIST);
        }
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (list == null) {
            finish();
            return;
        }

        check(str);
        if (isScanFinished()) {
            toast(getString(R.string.clothes_check_finish));
            finish();
        } else {
            startDelay();
        }
    }

    /**
     * 本地检查当前扫的码是否正确
     */
    private boolean check(String code) {
        if (TextUtils.isEmpty(code)) {
            toast(getString(R.string.scan_fail));
            return false;
        }

        // 扫的码是否正确
        boolean isIn = false;
        for (Clothes item : list) {
            if (item.getClothesCode().equals(code)) {
                isIn = true;
                if (item.isScaned()) {
                    toast(getString(R.string.duplicate_code));
                    return false;
                }
                item.setScaned(true);
                break;
            }
        }
        if (!isIn) {
            toast(getString(R.string.clothes_check_not_in));
            return false;
        }

        toast(getString(R.string.scan_succeed));

        return true;
    }

    /**
     * 是否已完成扫描
     */
    private boolean isScanFinished() {
        for (Clothes item : list) {
            if (!item.isScaned()) {
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