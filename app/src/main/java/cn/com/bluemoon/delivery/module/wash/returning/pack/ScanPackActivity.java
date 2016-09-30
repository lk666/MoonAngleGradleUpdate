package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ResultScanBoxCode;
import cn.com.bluemoon.delivery.module.base.BaseScanCodeActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

public class ScanPackActivity extends BaseScanCodeActivity {


    /*集中装箱扫描*/
    public final static int MODE_INBOX = 0;
    /*单独装箱扫描*/
    public final static int MODE_SINGER = 1;
    private int mode;
    private String backOrderCode;
    private String boxCode;
    private TextView txtTitle;

    /*从待装箱跳转过来*/
    public static void actStart(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), ScanPackActivity.class);
        intent.putExtra("title", AppContext.getInstance().getString(R.string.close_box_scan_back_code_title));
        intent.putExtra("btnString", AppContext.getInstance().getString(R.string.with_order_collect_manual_input_code_btn));
        intent.putExtra("mode", MODE_INBOX);
        fragment.startActivityForResult(intent, 0);
    }

    /**
     * 从待打包跳转过来
     *
     * @param aty
     * @param backOrderCode 还衣单号
     * @param boxCode       分配箱号
     */
    public static void actStart(Activity aty, String backOrderCode, String boxCode) {
        Intent intent = new Intent(aty, ScanPackActivity.class);
        intent.putExtra("title", AppContext.getInstance().getString(R.string.close_box_scan_box_code_title));
        intent.putExtra("btnString", AppContext.getInstance().getString(R.string.with_order_collect_manual_input_code_btn));
        intent.putExtra("mode", MODE_SINGER);
        intent.putExtra("backOrderCode", backOrderCode);
        intent.putExtra("code", boxCode);
        aty.startActivityForResult(intent, 0);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        mode = getIntent().getIntExtra("mode", -1);
        if (MODE_SINGER == mode) {
            backOrderCode = getIntent().getStringExtra("backOrderCode");
            boxCode = getIntent().getStringExtra("code");
            if (TextUtils.isEmpty(backOrderCode) || TextUtils.isEmpty(boxCode)) {
                toast(getString(R.string.pack_get_error_data));
                finish();
                return;
            }
        }
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        txtTitle = titleBar.getTitleView();
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if (TextUtils.isEmpty(str)) {
            toast(getString(R.string.scan_fail));
            startDelay();
            return;
        }
        switch (mode) {
            case MODE_INBOX:
                if (TextUtils.isEmpty(boxCode)) {
                    backOrderCode = str;
                    showWaitDialog();
                    ReturningApi.scanBackOrder(backOrderCode, getToken(), getNewHandler(0, ResultScanBoxCode.class));
                } else if (boxCode.equals(str)) {
                    showWaitDialog();
                    ReturningApi.scanClothesBox(backOrderCode, boxCode, getToken(), getNewHandler(1, ResultBase.class));
                } else {
                    toast(getString(R.string.pack_box_error, boxCode));
                    startDelay();

                }
                break;
            case MODE_SINGER:
                if (boxCode.equals(str)) {
                    showWaitDialog();
                    ReturningApi.scanClothesBox(backOrderCode, boxCode, getToken(), getNewHandler(2, ResultBase.class));
                } else {
                    toast(getString(R.string.pack_box_error, boxCode));
                    startDelay();
                }
                break;
        }

    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            ResultScanBoxCode scanBoxCode = (ResultScanBoxCode) result;
            boxCode = scanBoxCode.getBoxCode();
            setTxtCode(boxCode);
            txtTitle.setText(R.string.close_box_scan_box_code_title);
            startDelay();
        } else if (requestCode == 1) {
            toast(result.getResponseMsg());
            boxCode = null;
            backOrderCode = null;
            clearTxtCode();
            txtTitle.setText(R.string.close_box_scan_back_code_title);
            startDelay();
        } else if (requestCode == 2) {
            toast(result.getResponseMsg());
            setResult(RESULT_OK);
            finish();
        }
    }

}
