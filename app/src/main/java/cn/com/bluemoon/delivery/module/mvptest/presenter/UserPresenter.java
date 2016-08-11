package cn.com.bluemoon.delivery.module.mvptest.presenter;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultUser;
import cn.com.bluemoon.delivery.module.mvptest.model.IUserModel;
import cn.com.bluemoon.delivery.module.mvptest.model.UserModel;
import cn.com.bluemoon.delivery.module.mvptest.view.IUserView;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * Created by bm on 2016/8/1.
 */
public class UserPresenter {

    private IUserView userView;
    private IUserModel userModel;

    public UserPresenter(IUserView userView) {
        this.userView = userView;
        userModel = new UserModel();
    }

    public void getUser() {
        userModel.getUser(userView.getNewHandler(0, ResultUser.class));
    }

    public void logout() {
        userModel.logout(userView.getNewHandler(1, ResultBase.class));
    }

    public void success(int requestCode,ResultBase result) {
        switch (requestCode) {
            case 0:
                ResultUser resultUser = (ResultUser) result;
                userView.setUserId(resultUser.getUser().getAccount());
                userView.setUserName(resultUser.getUser().getRealName());
                break;
            case 1:
                ViewUtil.toast(result.getResponseMsg());
                userView.toLoginView();
                break;
        }

    }

}
