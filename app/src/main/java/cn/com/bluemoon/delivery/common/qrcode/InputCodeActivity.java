/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: Eric Liang
 * @version 1.0
 * @date: 2016/1/13
 */
package cn.com.bluemoon.delivery.common.qrcode;


import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.IconButton;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.view.ClearEditText;

/**
 * 手动输入条形码界面
 */
public class InputCodeActivity extends BaseActivity {
    @BindView(R.id.btn_sign)
    Button btnSign;
    @BindView(R.id.btn_scan)
    IconButton btnScan;
    @BindView(R.id.et_number)
    ClearEditText etNumber;
    @BindView(R.id.txt_code)
    TextView txtCode;
    @BindView(R.id.layout_code)
    RelativeLayout layoutCode;
    private String title;
    private String hint;
    private String btnString;
    private String code;

    public static void actStart(Activity context, String title, String hint, String btnString,
                                String code, int requestCode) {
        Intent intent = new Intent(context, InputCodeActivity.class);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra("title", title);
        }
        if (!TextUtils.isEmpty(hint)) {
            intent.putExtra("hint", hint);
        }
        if (!TextUtils.isEmpty(btnString)) {
            intent.putExtra("btnString", btnString);
        }
        if (!TextUtils.isEmpty(code)) {
            intent.putExtra("code", code);
        }
        context.startActivityForResult(intent, requestCode);
    }

    public static void actStart(Activity context, String title, String hint, String btnString,
                                int requestCode) {
        actStart(context, title, hint, btnString, null, requestCode);
    }

    public static void actStart(Activity context, String title,String code,int requestCode) {
        actStart(context, title, null, null, code, requestCode);
    }

    public static void actStart(Activity context, int requestCode) {
        actStart(context, null, null, null, requestCode);
    }

    @Override
    final protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        title = getIntent().getStringExtra("title");
        hint = getIntent().getStringExtra("hint");
        btnString = getIntent().getStringExtra("btnString");
        code = getIntent().getStringExtra("code");
    }

    @Override
    final protected int getLayoutId() {
        return R.layout.activity_sign;
    }

    @Override
    final protected String getTitleString() {
        if (TextUtils.isEmpty(title)) {
            return getString(R.string.with_order_collect_manual_input_code_btn);
        } else {
            return title;
        }
    }

    @Override
    final public void initView() {
        if (!TextUtils.isEmpty(hint)) {
            setInputHint(hint);
        }
        if (!TextUtils.isEmpty(btnString)) {
            setScanTxt(btnString);
        }
        setTxtCode(code);
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

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

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

    @OnClick({R.id.btn_sign, R.id.btn_scan})
    public void onClick(View view) {
        if (PublicUtil.isFastDoubleClick(1000)) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_sign:
                clickInput(etNumber.getText().toString());
                break;
            case R.id.btn_scan:
                clickScan();
                break;
        }
    }

    //可重写

    /**
     * 点击扫描按钮，返回resultCode为Constants.RESULT_INPUT
     */
    protected void clickScan() {
        setResult(Constants.RESULT_INPUT);
        finish();
    }

    /**
     * 点击确定输入按钮,默认返回输入内容，key值为onstants.RESULT_CODE
     *
     * @param code
     */
    protected void clickInput(String code) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.RESULT_CODE, code);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //公共方法

    /**
     * 设置扫描按钮的文字
     *
     * @param scanTxt
     */
    final protected void setScanTxt(String scanTxt) {
        btnScan.setText(scanTxt);
    }

    /**
     * 设置输入框的提示文字
     *
     * @param inputHint
     */
    final protected void setInputHint(String inputHint) {
        etNumber.setHint(inputHint);
    }

    /**
     * 设置顶部显示的内容
     *
     * @param code null则不显示
     */
    final protected void setTxtCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            txtCode.setText(code);
            ViewUtil.setViewVisibility(layoutCode, View.VISIBLE);
        } else {
            ViewUtil.setViewVisibility(layoutCode, View.GONE);
        }
    }

}
