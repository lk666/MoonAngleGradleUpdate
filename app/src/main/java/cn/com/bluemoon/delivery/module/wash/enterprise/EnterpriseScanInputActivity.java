package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.widget.Button;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseScanActivity;

/**
 * Created by liangjiangli on 2017/5/4.
 */

public class EnterpriseScanInputActivity extends BaseScanActivity{

    public static void actStart(Fragment fragment, String btnString, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), EnterpriseScanInputActivity.class);
        intent.putExtra("btnString", btnString);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Bind(R.id.btn_input)
    Button btnInput;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_enterprise_scan_input;
    }

    @Override
    protected String getTitleString() {
        return title;
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
    protected void initView() {
        super.initView();
        btnInput.setText(getIntent().getStringExtra("btnString"));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick(R.id.btn_input)
    public void onClick() {
        setResult(1);
        finish();
    }

}
