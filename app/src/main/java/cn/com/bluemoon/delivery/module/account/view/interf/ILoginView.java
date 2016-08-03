package cn.com.bluemoon.delivery.module.account.view.interf;

import cn.com.bluemoon.delivery.module.base.interf.BaseMainInterface;

/**
 * Created by bm on 2016/8/1.
 */
public interface ILoginView extends BaseMainInterface {

    String getUserName();
    String getUserPsw();
    void setUserName(String name);
    void toMainActivity();

}
