package cn.com.bluemoon.delivery.sz.bean.taskManager;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by jiangyuehua on 16/8/31.
 */
public class UserInfoListBean extends ResultBase implements Serializable {


    List<UserInfoBean> data=null;

    public List<UserInfoBean> getData() {
        return data;
    }

    public void setData(List<UserInfoBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserInfoListBean{" +
                "data=" + data +
                '}';
    }
}
