package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by jiangyuehua on 16/8/31.
 */
public class UserInfoAndReViewInfoBean extends ResultBase implements Serializable {


    private UserInfoBean sup=null;
    private UserInfoBean user=null;


    public UserInfoBean getSup() {
        return sup;
    }

    public void setSup(UserInfoBean sup) {
        this.sup = sup;
    }

    public UserInfoBean getUser() {
        return user;
    }

    public void setUser(UserInfoBean user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserInfoAndReViewInfoBean{" +
                "sup=" + sup.toString()+
                ", user=" + user.toString() +
                '}';
    }
}
