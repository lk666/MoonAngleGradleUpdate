package cn.com.bluemoon.delivery.entity;

import java.io.Serializable;

/**
 * getArguments（）用的基础TabHost的数据项类
 * Created by luokai on 2016/6/12.
 */
public class ArgumentTabState extends TabState {
    public Serializable getBundleData() {
        return bundleData;
    }

    private Serializable bundleData;

    public ArgumentTabState(Class clazz, int image, int content, Serializable bundleData) {
        super(clazz, image, content);
        this.bundleData = bundleData;
    }

}
