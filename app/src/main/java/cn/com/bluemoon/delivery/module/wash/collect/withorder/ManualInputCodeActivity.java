/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: Eric Liang
 * @version 1.0
 * @date: 2016/1/13
 */
package cn.com.bluemoon.delivery.module.wash.collect.withorder;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.module.oldbase.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.IconButton;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.view.ClearEditText;

/**
 * 手动输入条形码界面
 */
public class ManualInputCodeActivity extends BaseActivity implements OnClickListener {
    @Bind(R.id.btn_sign)
    Button btnSign;
    @Bind(R.id.btn_scan)
    IconButton btnScan;
    @Bind(R.id.et_number)
    ClearEditText etNumber;

    /**
     * 输入的数字码
     */
    public final static String RESULT_EXTRA_CODE = "RESULT_EXTRA_CODE";
    /**
     * 数字码长度
     */
    public final static String EXTRA_MAX_LENGTH = "EXTRA_MAX_LENGTH";

    /**
     * 换成扫码输入
     */
    public final static int RESULT_CODE_SCANE_CODE = 0x30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);

        initCustomActionBar();

        int max = getIntent().getIntExtra(EXTRA_MAX_LENGTH, -1);
        if (max > 0) {
            etNumber.setMaxLength(max);
        }
        etNumber.setHint(getString(R.string.manual_input_code_hint));
        btnScan.setText(getString(R.string.coupons_scan_code_title));

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

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                ManualInputCodeActivity.this.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.with_order_collect_manual_input_code_btn));
            }

        });
    }


    @OnClick({R.id.btn_sign, R.id.btn_scan})
    public void onClick(View view) {
        if (PublicUtil.isFastDoubleClick(1000)) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_sign:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_EXTRA_CODE, etNumber.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.btn_scan:
                setResult(RESULT_CODE_SCANE_CODE);
                finish();
                break;
        }
    }
}
