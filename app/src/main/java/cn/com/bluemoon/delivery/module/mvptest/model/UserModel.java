package cn.com.bluemoon.delivery.module.mvptest.model;

import cn.com.bluemoon.delivery.MenuFragment;
import cn.com.bluemoon.delivery.module.mvptest.model.bean.User;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * Created by bm on 2016/8/1.
 */
public class UserModel implements IUserModel{


    @Override
    public User getUser() {
        User user = new User();
        user.setUserId(MenuFragment.user.getAccount());
        user.setUserName(MenuFragment.user.getRealName());
        return user;
    }

    @Override
    public void upLoadUser(String id,String name) {
        PublicUtil.showToast("upload:"+id+"=="+name);
    }
}
