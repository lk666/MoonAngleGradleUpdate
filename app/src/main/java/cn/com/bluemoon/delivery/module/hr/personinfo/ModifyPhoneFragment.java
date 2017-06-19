package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.hr.personinfo.ResultGetVerifyCode;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;

/**
 * 修改手机号
 */

public class ModifyPhoneFragment extends BaseFragment<CommonActionBar> implements BMFieldBtn1View
        .FieldButtonListener {
    private static final int REQUEST_CODE_SAVE_MOBILE = 0x666;

    private static final String EXTRA_PHONE = "EXTRA_PHONE";
    private static final int REQUEST_CODE_GET_VERIFY_CODE = 0x777;
    @Bind(R.id.bctv_phone)
    BMFieldText1View bctvPhone;
    @Bind(R.id.bfbv_verify)
    BMFieldBtn1View bfbvVerify;
    @Bind(R.id.btn_submit)
    BMAngleBtn1View btnSubmit;

    private String phone;

    public static Fragment newInstance(String phone) {
        ModifyPhoneFragment fragment = new ModifyPhoneFragment();
        Bundle data = new Bundle();
        data.putSerializable(EXTRA_PHONE, phone);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    protected void onGetArguments() {
        super.onGetArguments();
        phone = getArguments().getString(EXTRA_PHONE);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.txt_modify_phone);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_modify_phone;
    }

    @Override
    protected void initContentView(View mainView) {
        bfbvVerify.setListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 获取验证码
            case REQUEST_CODE_GET_VERIFY_CODE:
                ResultGetVerifyCode vailCodeResult = (ResultGetVerifyCode) result;
                toast(vailCodeResult.getResponseMsg());
                startTime(vailCodeResult.time);
                break;
            // 保存
            case REQUEST_CODE_SAVE_MOBILE:
                toast(result.getResponseMsg());
                back();
                break;
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        switch (requestCode) {
            // 获取验证码
            case REQUEST_CODE_GET_VERIFY_CODE:
                ResultGetVerifyCode vailCodeResult = (ResultGetVerifyCode) result;
                toast(vailCodeResult.getResponseMsg());
                startTime(vailCodeResult.time);
                break;
            default:
                super.onErrorResponse(requestCode, result);
        }
    }

    TimeCount time;

    /**
     * 验证码倒计时
     */
    private void startTime(long delayTime) {
        time = new TimeCount(delayTime * 1000, 1000);
        bfbvVerify.setBtnColor(getResources().getColor(R.color.text_hint));
        bfbvVerify.setRightClickable(false);
        time.start();
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        // 点击保存
        if (!StringUtil.isPhone(bctvPhone.getContent())) {
            toast(R.string.error_message_input_phone);
        } else if (bctvPhone.getContent().equals(phone)) {
            toast(getString(R.string.err_phone_same));
        } else if (TextUtils.isEmpty(bfbvVerify.getContent())) {
            toast(getString(R.string.login_right_code_hint));
        } else {
            showWaitDialog(false);
            HRApi.saveMobile(bctvPhone.getContent(), getToken(), bfbvVerify.getContent(),
                    getNewHandler(REQUEST_CODE_SAVE_MOBILE, ResultBase.class));
        }
    }

    private class TimeCount extends CountDownTimer {
        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            bfbvVerify.setBtnTxt(getString(R.string.login_check_again));
            bfbvVerify.setBtnColor(getResources().getColor(R.color.text_blue_1fb8ff));
            bfbvVerify.setRightClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            bfbvVerify.setBtnTxt(String.format(getString(R.string.get_code_again),
                    millisUntilFinished / 1000));
        }
    }

    @Override
    public void onClickLayout(View view) {

    }

    @Override
    public void afterTextChanged(BMFieldBtn1View bmFieldBtn1View, String s) {

    }

    @Override
    public void onClickRight(View view) {
        // 点击获取验证码
        getValidCode();
    }

    public void getValidCode() {
        if (!StringUtil.isPhone(bctvPhone.getContent())) {
            toast(R.string.error_message_input_phone);
        } else if (bctvPhone.getContent().equals(phone)) {
            toast(getString(R.string.err_phone_same));
        } else {
            showWaitDialog(false);
            HRApi.getVerifyCode(bctvPhone.getContent(), getToken(),
                    getNewHandler(REQUEST_CODE_GET_VERIFY_CODE, ResultGetVerifyCode.class));
        }
    }

    @Override
    public void onDestroy() {
        if (time != null) {
            time.cancel();
        }
        super.onDestroy();
    }
}
