package cn.com.bluemoon.delivery.module.mvptest.presenter;

import cn.com.bluemoon.delivery.module.mvptest.model.IUserModel;
import cn.com.bluemoon.delivery.module.mvptest.model.bean.User;
import cn.com.bluemoon.delivery.module.mvptest.model.UserModel;
import cn.com.bluemoon.delivery.module.mvptest.view.IUserView;

/**
 * Created by bm on 2016/8/1.
 */
public class UserPresenter {

    private IUserView userView;
    private IUserModel userModel;

    public UserPresenter(IUserView userView){
        this.userView = userView;
        userModel = new UserModel();
    }

    public void setUser(){
        User user = userModel.getUser();
        userView.setUserId(user.getUserId());
        userView.setUserName(user.getUserName());
    }

    public void upload(String id,String name){
        userModel.upLoadUser(id,name);
    }


}
