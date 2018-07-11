/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: Eric Liang
 * @version 1.0
 * @date: 2016/1/13
 */
package cn.com.bluemoon.delivery.module.order;


import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.view.ClearEditText;

public class SignActivity extends BaseActivity {
    @BindView(R.id.et_number)
    ClearEditText etNumber;
    @BindView(R.id.btn_sign)
    Button btnSign;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.pending_order_receive_sign_title);
    }

    @Override
    public void initView() {
        checkBtnState();
        etNumber.setCallBack(new CommonEditTextCallBack() {

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkBtnState();
            }
        });
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
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick({R.id.btn_sign, R.id.btn_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("code", etNumber.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.btn_scan:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

}
