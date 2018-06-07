package cn.com.bluemoon.delivery.module.track.bean;

import cn.com.bluemoon.delivery.db.entity.BaseParam;

/**
 * 菜单埋点
 * Created by bm on 2018/4/23.
 */

public class MenuParam extends BaseParam{

    private String url;

    public MenuParam(String url) {
        super();
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
