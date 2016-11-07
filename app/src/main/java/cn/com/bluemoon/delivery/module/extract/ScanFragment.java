package cn.com.bluemoon.delivery.module.extract;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrderInfoPickup;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.ClearEditText;

public class ScanFragment extends BaseFragment {

    @Bind(R.id.et_number)
    ClearEditText etNumber;
    @Bind(R.id.btn_sign)
    Button btnSign;
    private Activity context;
    private String signType = "digital";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_extract_scan;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_extract_scan_code_title);
    }

    @Override
    public void initView() {
        context = getActivity();
        checkBtnState();
        etNumber.setCallBack(new CommonEditTextCallBack() {

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkBtnState();
            }

        });
    }

    @Override
    public void initData() {

    }

    private void checkBtnState() {
        if (etNumber.getText().toString().trim().length() > 0) {
            btnSign.setClickable(true);
            btnSign.setBackgroundResource(R.drawable.btn_blue_shape);
        } else {
            btnSign.setClickable(false);
            btnSign.setBackgroundResource(R.drawable.btn_blue_shape_disable);
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

        etNumber.setText("");
        Intent intent = new Intent(context, OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", (ResultOrderInfoPickup) result);
        bundle.putString("signType", signType);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED)
            return;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_SCAN:
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    signType = "scan";
                    DeliveryApi.getOrderInfo(getToken(), resultStr, getNewHandler(1,
                            ResultOrderInfoPickup.class));
                    break;
            }
        }
    }


    @OnClick({R.id.btn_sign, R.id.btn_scan, R.id.num_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign:
                ViewUtil.hideIM(view);
                if (!TextUtils.isEmpty(etNumber.getText().toString())) {
                    showWaitDialog();
                    signType = "digital";
                    DeliveryApi.getOrderInfo(ClientStateManager.getLoginToken(), etNumber
                            .getText().toString(), getNewHandler(0, ResultOrderInfoPickup.class));
                } else {
                    toast(R.string.extract_scan_code_input_number);
                }
                break;
            case R.id.btn_scan:
                PublicUtil.openScanOrder(context, ScanFragment.this,
                        getString(R.string.tab_extract_scan_code_title),
                        getString(R.string.extract_input_code_btn),
                        Constants.REQUEST_SCAN, 4);
                break;
            case R.id.num_layout:
                ViewUtil.hideIM(view);
                break;
        }
    }

}
