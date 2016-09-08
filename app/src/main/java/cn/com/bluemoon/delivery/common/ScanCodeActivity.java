package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseScanActivity;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * 带二维码和条形图案的扫描类
 */
public class ScanCodeActivity extends BaseScanActivity {

    @Bind(R.id.btn_input)
    Button btnInput;

    public static void actStart(Activity context, Fragment fragment, String title, String btnString, int requestCode, int resultCode) {
        Intent intent = new Intent(context, ScanCodeActivity.class);
        intent.putExtra("title", title);
        if(!TextUtils.isEmpty(btnString)){
            intent.putExtra("btn", btnString);
            intent.putExtra("result", resultCode);
        }
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            context.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_code;
    }

    @Override
    protected String getTitleString() {
        return title;
    }

    @Override
    protected void initView() {
        super.initView();
        if(getIntent().hasExtra("btn")){
            String btnString = getIntent().getStringExtra("btn");
            btnInput.setText(btnString);
            ViewUtil.setViewVisibility(btnInput, View.VISIBLE);
        }
    }

    @Override
    protected int getSurfaceViewId() {
        return R.id.preview_view;
    }

    @Override
    protected int getViewfinderViewId() {
        return R.id.viewfinder_view;
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        finishWithData(str, type);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick(R.id.btn_input)
    public void onClick() {
        int resultCode = getIntent().getIntExtra("result", 0);
        setResult(resultCode);
        finish();
    }

}
