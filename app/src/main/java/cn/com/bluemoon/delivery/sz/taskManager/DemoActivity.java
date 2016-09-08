package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Intent;
import android.view.View;

import org.kymjs.kjframe.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.module.account.InputPhoneActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;

public class DemoActivity extends BaseActivity{

    @Bind(R.id.et_user_name)
    ClearEditText etUserName;
    @Bind(R.id.et_user_psw)
    ClearEditText etUserPsw;

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
    public void initView() {
    }

    @Override
    public void initData() {

    }


    private void login(String name,String psw){
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(psw)) {
            LibViewUtil.toast(AppContext.getInstance(), AppContext.getInstance().getString(R.string.register_not_empty));
            return;
        }
        showWaitDialog();
        DeliveryApi.ssoLogin(name, psw, getNewHandler(0, ResultToken.class));
    }


    @OnClick({R.id.btn_login, R.id.txt_forget_psw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
//                login(getUserName(),getUserPsw());
                break;
            case R.id.txt_forget_psw:
                Intent intent = new Intent();
                intent.setClass(this, InputPhoneActivity.class);
//                intent.putExtra("id", getUserName());
                startActivityForResult(intent, 0);
                break;
        }
    }

}
