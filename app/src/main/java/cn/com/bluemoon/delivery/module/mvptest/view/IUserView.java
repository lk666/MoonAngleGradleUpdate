package cn.com.bluemoon.delivery.module.mvptest.view;

import cn.com.bluemoon.delivery.module.base.interf.BaseMainInterface;

/**
 * Created by bm on 2016/8/1.
 */
public interface IUserView extends BaseMainInterface {

    void setUserName(String name);

    void setUserId(String id);

    void toLoginView();

}
