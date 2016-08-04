package cn.com.bluemoon.delivery.module.account.presenter;

import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.module.account.model.ILoginModel;
import cn.com.bluemoon.delivery.module.account.model.LoginModel;
import cn.com.bluemoon.delivery.module.account.view.interf.ILoginView;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by bm on 2016/8/1.
 */
public class LoginPresenter {

    private ILoginView loginView;
    private ILoginModel userModel;

    public LoginPresenter(ILoginView loginView){
        this.loginView = loginView;
        userModel = new LoginModel();
    }

    public void setUserName(){
        loginView.setUserName(userModel.getUserName());
    }

    public void clearData(){
        userModel.clearData();
    }

    public void login(){
        if (StringUtils.isEmpty(loginView.getUserName()) || StringUtils.isEmpty(loginView.getUserPsw())) {
            LibViewUtil.toast(AppContext.getInstance(), AppContext.getInstance().getString(R.string.register_not_empty));
            return;
        }
        loginView.showWaitDialog();
        userModel.login(loginView.getUserName(), loginView.getUserPsw(),
                loginView.getNewHandler(0, ResultToken.class));
    }

    public void saveData(String jsonString){
        userModel.saveData(jsonString, loginView.getUserName());
    }
}
