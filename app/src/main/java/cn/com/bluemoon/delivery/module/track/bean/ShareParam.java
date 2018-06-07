package cn.com.bluemoon.delivery.module.track.bean;

import cn.com.bluemoon.delivery.db.entity.BaseParam;

/**
 * 平台分享数据埋点的参数类
 * Created by bm on 2017/5/9.
 */

public class ShareParam extends BaseParam {

    private String url;
    private String toTag;

    public ShareParam(String url, String toTag) {
        super();
        this.url = url;
        this.toTag = toTag;
    }

    public String getToTag() {
        return toTag;
    }

    public void setToTag(String toTag) {
        this.toTag = toTag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
