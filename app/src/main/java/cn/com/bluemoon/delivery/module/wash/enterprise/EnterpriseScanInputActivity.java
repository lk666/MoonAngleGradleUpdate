package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Button;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetWashEnterpriseScan;
import cn.com.bluemoon.delivery.module.base.BaseScanActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.createorder.AddClothesActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.createorder.CreateOrderActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ConfirmEvent;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.CreateOrderEvent;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.SaveOrderEvent;

/**
 * Created by liangjiangli on 2017/5/4.
 */

public class EnterpriseScanInputActivity extends BaseScanActivity {

    private static final int REQUEST_CODE_SCAN = 0x666;

    public static void actStart(Context context, String btnString) {
        Intent intent = new Intent(context, EnterpriseScanInputActivity.class);
        intent.putExtra("btnString", btnString);
        context.startActivity(intent);
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
        showWaitDialog();
        EnterpriseApi.getWashEnterpriseScan(str,
                getToken(), getNewHandler(REQUEST_CODE_SCAN, ResultGetWashEnterpriseScan
                        .class));
    }

    @Override
    protected void initView() {
        super.initView();
        btnInput.setText(getIntent().getStringExtra("btnString"));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        ResultGetWashEnterpriseScan resultGetWashEnterpriseScan =
                (ResultGetWashEnterpriseScan) result;
        if (resultGetWashEnterpriseScan.enterpriseOrderInfo != null) {
            // 跳到添加衣物
            AddClothesActivity.actionStart(this, resultGetWashEnterpriseScan);
        } else {
            CreateOrderActivity.actionStart(this, resultGetWashEnterpriseScan);
        }
    }

    @OnClick(R.id.btn_input)
    public void onClick() {
        EmployOrderQueryActivity.startAct(this);
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConfirmEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CreateOrderEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveOrderEvent event) {
        finish();
    }

}
