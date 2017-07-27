package cn.com.bluemoon.delivery.db.entity;

/**
 * 平台分享数据埋点的参数类
 * Created by bm on 2017/5/9.
 */

public class ShareParam extends BaseParam {

    public String url;
    public String toTag;

    public ShareParam(){
        super();
    }

    public ShareParam(String url, String toTag) {
        super();
        this.url = url;
        this.toTag = toTag;
    }

}
