package cn.com.bluemoon.delivery.module.account.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.account.presenter.LoginPresenter;
import cn.com.bluemoon.delivery.module.account.view.interf.ILoginView;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class LoginActivity extends BaseActivity implements ILoginView {

    @Bind(R.id.et_user_name)
    ClearEditText etUserName;
    @Bind(R.id.et_user_psw)
    ClearEditText etUserPsw;
    private LoginPresenter loginPresenter;
    private String jumpCode;

    @Override
    protected int getLayoutId() {
        return R.layout.login;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        loginPresenter.saveData(jsonString);
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
        loginPresenter = new LoginPresenter(this);
        etUserName.setMaxLength(9);
        etUserPsw.setMaxLength(16);
    }

    @Override
    public void initData() {
        loginPresenter.clearData();
        loginPresenter.setUserName();

    }

    private String getJumpCode(Intent intent) {
        return intent == null ? null : intent.getStringExtra(Constants.KEY_JUMP);
    }

    @Override
    public String getUserName() {
        return etUserName.getText().toString();
    }

    @Override
    public String getUserPsw() {
        return etUserPsw.getText().toString();
    }

    @Override
    public void setUserName(String name) {
        etUserName.setText(name);
        etUserName.setSelection(etUserName.length());
        etUserName.updateCleanable(0, false);
    }

    @Override
    public void toMainActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra(Constants.KEY_JUMP, jumpCode);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.btn_login, R.id.txt_forget_psw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                loginPresenter.login();
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
                loginPresenter.setUserName();
                break;
        }
    }
}
