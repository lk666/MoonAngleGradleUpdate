package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.sz.bean.MeetingerChooseBean.UserInfoDetailsBean;

/**
 * Created by Wan.N
 * Date       2016/9/9
 * Desc      通过员工工号查询员工信息及直接上级信息实体
 */
public class ResultGetSuperiorAndSelfInfoBean extends ResultBase {

    private List<UserInfoDetailsBean> sup;//直接上级信息<个人信息结构>
    private List<UserInfoDetailsBean> user;//用户信息<个人信息结构>

    public ResultGetSuperiorAndSelfInfoBean() {
    }

    public List<UserInfoDetailsBean> getSup() {
        return sup;
    }

    public void setSup(List<UserInfoDetailsBean> sup) {
        this.sup = sup;
    }

    public List<UserInfoDetailsBean> getUser() {
        return user;
    }

    public void setUser(List<UserInfoDetailsBean> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ResultGetSuperiorAndSelfInfoBean{" +
                "sup=" + sup +
                ", user=" + user +
                '}';
    }
}
