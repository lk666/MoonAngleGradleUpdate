package cn.com.bluemoon.delivery.app.api.model;

import java.util.List;

/**
 * Created by bm on 2018/7/6.
 */

public class ResultBannerList extends ResultBase{

    public List<ListBean> list;

    public static class ListBean {

        public String bannerCode;
        public String bannerImg;
        public String bannerUrl;
        public String intentionType;
        public int sort;
    }
}
