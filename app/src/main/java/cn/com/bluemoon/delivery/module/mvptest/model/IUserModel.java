package cn.com.bluemoon.delivery.module.mvptest.model;

import cn.com.bluemoon.delivery.module.mvptest.model.bean.User;

/**
 * Created by bm on 2016/8/1.
 */
public interface IUserModel {

    User getUser();

    void upLoadUser(String id,String name);

}
