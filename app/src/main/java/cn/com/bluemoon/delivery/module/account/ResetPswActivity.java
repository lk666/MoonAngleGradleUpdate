package cn.com.bluemoon.delivery.module.account;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.ResultVailCode;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;

public class ResetPswActivity extends BaseActivity {
    @Bind(R.id.ed_userid)
    ClearEditText edUserid;
    @Bind(R.id.ed_phone)
    ClearEditText edPhone;
    @Bind(R.id.ed_code)
    ClearEditText edCode;
    @Bind(R.id.btn_code)
    Button btnCode;
    @Bind(R.id.ed_psw)
    ClearEditText edPsw;
    @Bind(R.id.ed_con_psw)
    ClearEditText edConPsw;
    @Bind(R.id.txt_toast)
    TextView txtToast;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    private ResetPswActivity aty;
    private String userid;


    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent() != null) {
            userid = getIntent().getStringExtra("userId");
        }
        aty = this;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_psw;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.user_findpwd);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        ImageView imageView = titleBar.getImgRightView();
        imageView.setImageResource(R.mipmap.ico_customer_service);
        ViewUtil.setViewVisibility(imageView,View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        DialogUtil.showServiceDialog2(this);
    }

    @Override
    public void initView() {
        edUserid.setMaxLength(9);
        edPhone.setMaxLength(11);
        if (!TextUtils.isEmpty(userid)) {
            edUserid.setText(userid);
            edUserid.setSelection(userid.length());
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            ResultVailCode vailCodeResult = (ResultVailCode) result;
            toast(vailCodeResult.getResponseMsg());
            startTime(vailCodeResult.getTime());
        } else if (requestCode == 1) {
            ClientStateManager.setUserName(userid);
            toast(R.string.login_reset_success);
            finish();
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        if (requestCode == 0 && result.getResponseCode() == Constants.RESPONSE_RESULT_SMS_VAILD) {
            ResultVailCode vailCodeResult = (ResultVailCode) result;
            toast(vailCodeResult.getResponseMsg());
            startTime(vailCodeResult.getTime());
        }else{
            txtToast.setText(result.getResponseMsg());
            ViewUtil.showSubmitAmin(btnSubmit, txtToast);
        }
    }

    public void getValidCode() {
        userid = edUserid.getText().toString().trim();
        String phone = edPhone.getText().toString().trim();
        if (TextUtils.isEmpty(userid)) {
            toast(R.string.login_name_empty);
        } else if (phone.length() != 11) {
            toast(R.string.login_phone_right);
        } else {
            showWaitDialog();
            DeliveryApi.getVerifyCode(phone, userid, getNewHandler(0, ResultVailCode.class));
        }
    }

    private void startTime(int delayTime) {
        TimeCount time = new TimeCount(delayTime * 1000, 1000);
        time.start();
        btnCode.setBackgroundResource(R.drawable.btn_disable_shape);
    }

    private void submit() {

        String phone = edPhone.getText().toString().trim();
        String code = edCode.getText().toString().trim();
        String pass = edPsw.getText().toString();
        String copass = edConPsw.getText().toString();
        boolean isTrue = false;
        if (TextUtils.isEmpty(code) || code.length() != 4) {
            txtToast.setText(R.string.login_right_code_hint);
        } else if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(copass)) {
            txtToast.setText(R.string.login_not_empty);
        } else if (!pass.equals(copass)) {
            txtToast.setText(R.string.login_same_psw);
        }  else {
            isTrue = true;
            showWaitDialog();
            DeliveryApi.resetPassword(phone, code, pass, getNewHandler(1, ResultBase.class));
        }
        if(!isTrue){
            ViewUtil.showSubmitAmin(btnSubmit, txtToast);
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnCode.setText(getString(R.string.login_check_again));
            btnCode.setBackgroundResource(R.drawable.btn_blue_shape);
            btnCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnCode.setClickable(false);
            btnCode.setText(String.format(getString(R.string.login_get_code_again), millisUntilFinished / 1000));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED, null);
            finish();
        }
        return false;
    }

    @OnClick({R.id.btn_code, R.id.btn_submit})
    public void onClick(View view) {
        if (PublicUtil.isFastDoubleClick(1000))
            return;
        switch (view.getId()) {
            case R.id.btn_code:
                getValidCode();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    public static void actStart(Context context, String userId) {
        Intent intent = new Intent(context, ResetPswActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }
}
