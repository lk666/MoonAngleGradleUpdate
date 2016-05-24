package cn.com.bluemoon.delivery.entity;

import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagView;

/**
 * Created by bm on 2016/3/29.
 */
public class TagItem {
    public Tag tag;
    public TagView tagView;
    public TagItem(Tag tag,TagView tagView){
        this.tag = tag;
        this.tagView = tagView;
    }
}
