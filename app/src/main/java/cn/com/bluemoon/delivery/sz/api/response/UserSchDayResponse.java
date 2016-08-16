package cn.com.bluemoon.delivery.sz.api.response;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.sz.bean.SchedualCommonBean;

/**
 * Created by dujiande on 2016/8/11.
 */
public class UserSchDayResponse extends BaseResponse {
    private ArrayList<SchedualCommonBean>  data;

    public ArrayList<SchedualCommonBean> getData() {
        return data;
    }

    public void setData(ArrayList<SchedualCommonBean> data) {
        this.data = data;
    }
}
