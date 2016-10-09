package cn.com.bluemoon.delivery.sz.api.response;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.sz.bean.MsgListItemBean;

/**
 * Created by dujiande on 2016/8/11.
 */
public class UserMsgListResponse extends BaseResponse {
    private ArrayList<MsgListItemBean>  data;

    public ArrayList<MsgListItemBean> getData() {
        return data;
    }

    public void setData(ArrayList<MsgListItemBean> data) {
        this.data = data;
    }
}
