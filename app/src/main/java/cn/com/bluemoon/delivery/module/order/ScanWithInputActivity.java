package cn.com.bluemoon.delivery.module.order;

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
public class ScanWithInputActivity extends BaseScanActivity {

    @Bind(R.id.btn_input)
    Button btnInput;

    public static void actStart(Activity context, String title, String btnString, int requestCode) {
        Intent intent = new Intent(context, ScanWithInputActivity.class);
        intent.putExtra("title", title);
        if(!TextUtils.isEmpty(btnString)){
            intent.putExtra("btn", btnString);
        }
        context.startActivityForResult(intent, requestCode);
    }

    public static void actStart(Fragment fragment, String title, String btnString, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ScanWithInputActivity.class);
        intent.putExtra("title", title);
        if(!TextUtils.isEmpty(btnString)){
            intent.putExtra("btn", btnString);
        }
        fragment.startActivityForResult(intent, requestCode);
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
        }else{
            ViewUtil.setViewVisibility(btnInput,View.GONE);
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
        Intent intent = new Intent(this,SignActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            return;
        }
        if (resultCode==RESULT_OK&&requestCode==0&&data!=null){
            String code = data.getStringExtra("code");
            if(!TextUtils.isEmpty(code)){
                onResult(code,"TEXT",null);
            }
        }
    }
}
