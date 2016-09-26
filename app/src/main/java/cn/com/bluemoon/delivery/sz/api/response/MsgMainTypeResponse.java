package cn.com.bluemoon.delivery.sz.api.response;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.sz.bean.MainMsgCountBean;

/**
 * Created by dujiande on 2016/8/11.
 */
public class MsgMainTypeResponse extends BaseResponse {
    private ArrayList<MainMsgCountBean>  mainTypeNews;

    public ArrayList<MainMsgCountBean> getMainTypeNews() {
        return mainTypeNews;
    }

    public void setMainTypeNews(ArrayList<MainMsgCountBean> mainTypeNews) {
        this.mainTypeNews = mainTypeNews;
    }
}
