package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.hr.personinfo.ResultGetVerifyCode;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldBtn1View;

/**
 * 修改手机号
 */

public class ModifyPhoneFragment extends BaseFragment<CommonActionBar> implements BMFieldBtn1View
        .FieldButtonListener {
    private static final int REQUEST_CODE_SAVE_MOBILE = 0x666;

    private static final String EXTRA_PHONE = "EXTRA_PHONE";
    private static final int REQUEST_CODE_GET_VERIFY_CODE = 0x777;
    @Bind(R.id.bctv_phone)
    ModifyPhoneEditText bctvPhone;
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
    protected void back() {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        LibViewUtil.hideKeyboard(bctvPhone);
        super.back();
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
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        bctvPhone.setHint(phone);
        bfbvVerify.setListener(this);
        bctvPhone.showIM();
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
            toast(R.string.err_input_phone);
        } else if (bctvPhone.getContent().equals(phone)) {
            toast(getString(R.string.err_input_phone));
        } else if (TextUtils.isEmpty(bfbvVerify.getContent())) {
            toast(getString(R.string.err_verify_empty));
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
            if (bfbvVerify == null) {
                this.cancel();
                return;
            }
            bfbvVerify.setBtnTxt(getString(R.string.login_check_again));
            bfbvVerify.setBtnColor(getResources().getColor(R.color.text_blue_1fb8ff));
            bfbvVerify.setRightClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (bfbvVerify == null) {
                this.cancel();
                return;
            }
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
        if (TextUtils.isEmpty(bctvPhone.getContent())) {
            toast(getString(R.string.err_phone_same));
        } else if (!StringUtil.isPhone(bctvPhone.getContent())) {
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
        super.onDestroy();
        if (time != null) {
            time.cancel();
        }
    }
}
