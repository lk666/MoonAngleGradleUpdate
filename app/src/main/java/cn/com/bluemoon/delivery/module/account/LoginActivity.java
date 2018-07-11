package cn.com.bluemoon.delivery.module.account;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.utils.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.card.alarm.Reminds;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.ClearEditText;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_user_name)
    ClearEditText etUserName;
    @BindView(R.id.et_user_psw)
    ClearEditText etUserPsw;
    //    @BindView(R.id.txt_toast)
//    TextView txtToast;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private String view;
    private String url;
    private static final String URL_APPLY =
            "view/cooperation/#/index?_compaign=cooperationApply&_adTag=other";


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ClientStateManager.setLoginToken(((ResultToken) result).getToken());
        ClientStateManager.setUserName(getUserName());
        MobclickAgent.onProfileSignIn(getUserName());
        toMainActivity();
        try {
            Reminds.SynAlarm(this);
        } catch (Exception ex) {
            LogUtils.e(getDefaultTag(), "Syn Alarms Error", ex);
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
//        txtToast.setText(result.getResponseMsg());
//        ViewUtil.showSubmitAmin(btnLogin, txtToast);
        super.onErrorResponse(requestCode, result);
        ViewUtil.showBtnAmin(btnLogin);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        view = PublicUtil.getPushView(getIntent());
        url = PublicUtil.getPushUrl(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        view = PublicUtil.getPushView(intent);
        url = PublicUtil.getPushUrl(intent);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        ClientStateManager.clearData();
        setUserName(ClientStateManager.getUserName());

    }

    private String getExtraValue(Intent intent, String key) {
        return intent == null ? null : intent.getStringExtra(key);
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

    private void login(String name, String psw) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(psw)) {
//            txtToast.setText(R.string.login_not_empty);
//            ViewUtil.showSubmitAmin(btnLogin, txtToast);
            toast(R.string.login_not_empty);
            ViewUtil.showBtnAmin(btnLogin);
            return;
        }
        DeliveryApi.ssoLogin(name, psw, getNewHandler(0, ResultToken.class));
    }

    public void toMainActivity() {
        MainActivity.actStart(this, view, url);
        finish();
    }

    @OnClick({R.id.btn_login, R.id.txt_forget_psw, R.id.txt_apply})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login(getUserName(), getUserPsw());
                break;
            case R.id.txt_forget_psw:
                ResetPswActivity.actStart(this, getUserName());
                break;
            case R.id.txt_apply:
                PublicUtil.openWebView(this, String.format(BuildConfig.API_URL, URL_APPLY), "");
                break;
        }
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                firstTime = System.currentTimeMillis();
                ViewUtil.toast(R.string.app_quit_txt);
            } else {
                finish();
                ActivityManager.getInstance().finishAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void actStart(Context context, String view, String url) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constants.PUSH_VIEW, view);
        intent.putExtra(Constants.PUSH_URL, url);
        context.startActivity(intent);
    }

    public static void actStart(Context context) {
        actStart(context, null, null);
    }

}