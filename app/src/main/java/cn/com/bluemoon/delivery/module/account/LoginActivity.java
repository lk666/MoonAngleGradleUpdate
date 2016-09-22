package cn.com.bluemoon.delivery.module.account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class LoginActivity extends BaseActivity{

    @Bind(R.id.et_user_name)
    ClearEditText etUserName;
    @Bind(R.id.et_user_psw)
    ClearEditText etUserPsw;
    private String jumpCode;

    @Override
    protected int getLayoutId() {
        return R.layout.login;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ClientStateManager.setLoginToken(((ResultToken) result).getToken());
        ClientStateManager.setUserName(getUserName());
        MobclickAgent.onProfileSignIn(getUserName());
        toMainActivity();
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        jumpCode = getJumpCode(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        jumpCode = getJumpCode(intent);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        ClientStateManager.clearData();
        setUserName(ClientStateManager.getUserName());

    }

    private String getJumpCode(Intent intent) {
        return intent == null ? null : intent.getStringExtra(Constants.KEY_JUMP);
    }

    public String getUserName() {
        return etUserName.getText().toString();
    }

    public String getUserPsw() {
        return etUserPsw.getText().toString();
    }

    public void setUserName(String name) {
        etUserName.setText(name);
        etUserName.setSelection(etUserName.length());
        etUserName.updateCleanable(0, false);
    }

    private void login(String name,String psw){
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(psw)) {
            LibViewUtil.toast(AppContext.getInstance(), AppContext.getInstance().getString(R.string.register_not_empty));
            return;
        }
        showWaitDialog();
        DeliveryApi.ssoLogin(name, psw, getNewHandler(0, ResultToken.class));
    }

    public void toMainActivity() {
        MainActivity.actStart(this, jumpCode);
        finish();
    }

    @OnClick({R.id.btn_login, R.id.txt_forget_psw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login(getUserName(),getUserPsw());
                break;
            case R.id.txt_forget_psw:
                Intent intent = new Intent();
                intent.setClass(this, InputPhoneActivity.class);
                intent.putExtra("id", getUserName());
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new CommonAlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.exit_app_msg)
                    .setPositiveButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    LoginActivity.this.finish();
                                }
                            }).setNegativeButton(R.string.btn_cancel, null)
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                setUserName(ClientStateManager.getUserName());
                break;
        }
    }

    public static void actStart(Context context,String jumpCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        if(!TextUtils.isEmpty(jumpCode)){
            intent.putExtra(Constants.KEY_JUMP,jumpCode);
        }
        context.startActivity(intent);
    }

    public static void actStart(Context context) {
        actStart(context,null);
    }

}
