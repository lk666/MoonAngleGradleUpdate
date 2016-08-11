package cn.com.bluemoon.delivery.module.clothing.collect;

import cn.com.bluemoon.delivery.entity.TabState;

/**
 * 基础TabHost的数据项类，已弃用
 * Created by luokai on 2016/6/12.
 */
@Deprecated
public class OldTabState extends TabState {
    private String manager;


    public OldTabState(Class clazz, int image, int content, String manager) {
        super(clazz, image, content);
        this.manager = manager;
    }

    public String getManager() {
        return manager;
    }
}
