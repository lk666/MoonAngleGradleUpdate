package cn.com.bluemoon.delivery.sz.api.response;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.sz.bean.SchedualBean;

/**
 * Created by dujiande on 2016/8/11.
 */
public class UserSchDayResponse extends BaseResponse {
    private ArrayList<SchedualBean>  data;

    public ArrayList<SchedualBean> getData() {
        return data;
    }

    public void setData(ArrayList<SchedualBean> data) {
        this.data = data;
    }
}
