package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Context;
import android.content.Intent;
import android.view.View;

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
import cn.com.bluemoon.delivery.module.account.InputPhoneActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;

public class AddTaskActivity extends BaseActivity{

    @Bind(R.id.et_user_name)
    ClearEditText etUserName;
    @Bind(R.id.et_user_psw)
    ClearEditText etUserPsw;
    private String jumpCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sz_add_task;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
//        ClientStateManager.setLoginToken(((ResultToken) result).getToken());
//        ClientStateManager.setUserName(getUserName());
//        MobclickAgent.onProfileSignIn(getUserName());
//        toMainActivity();
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
        etUserName.setMaxLength(9);
        etUserPsw.setMaxLength(16);
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



    public static void actStart(Context context) {
        Intent intent = new Intent(context, AddTaskActivity.class);
        context.startActivity(intent);
    }

    public static void actStart(Context context,String jumpCode) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.KEY_JUMP,jumpCode);
        context.startActivity(intent);
    }
}
