package cn.com.bluemoon.delivery.common.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseScanActivity;

/**
 * 默认简单扫描界面
 */
public class ScanActivity extends BaseScanActivity {

    @Bind(R.id.txt_title)
    TextView txtTitle;

    public static void actStart(Activity context,Fragment fragment,String title,int requestCode){
        actStart(context,fragment,ScanActivity.class,title,requestCode);
    }

    public static void actStart(Activity context,String title,int requestCode){
        actStart(context,null,ScanActivity.class,title,requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    protected String getTitleString() {
        return null;
    }

    @Override
    protected void initView() {
        super.initView();
        txtTitle.setText(title);
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

    @OnClick(R.id.img_back)
    public void onClick() {
        finish();
    }
}
