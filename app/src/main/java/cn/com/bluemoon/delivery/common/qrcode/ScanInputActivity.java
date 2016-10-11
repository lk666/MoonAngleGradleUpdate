package cn.com.bluemoon.delivery.common.qrcode;

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
 * 带输码按钮的扫描类
 */
public class ScanInputActivity extends BaseScanActivity {

    @Bind(R.id.btn_input)
    Button btnInput;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_count)
    TextView txtCount;

    public static void actStart(Activity context, Fragment fragment, String title, String btnString,String name,String count, int requestCode, int resultCode) {
        Intent intent = new Intent(context, ScanInputActivity.class);
        intent.putExtra("title", title);
        if(!TextUtils.isEmpty(btnString)){
            intent.putExtra("btn", btnString);
            intent.putExtra("result", resultCode);
        }
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(count)){
            intent.putExtra("name", name);
            intent.putExtra("count", count);
        }
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void actStart(Activity context, Fragment fragment, String title, String btnString, int requestCode, int resultCode) {
        actStart(context,fragment,title,btnString,null,null,requestCode,resultCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_input;
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
        if(getIntent().hasExtra("name")){
            String name = getIntent().getStringExtra("name");
            txtName.setText(name);
            String count = getIntent().getStringExtra("count");
            txtCount.setText(count);
            ViewUtil.setViewVisibility(txtName, View.VISIBLE);
            ViewUtil.setViewVisibility(txtCount, View.VISIBLE);
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
