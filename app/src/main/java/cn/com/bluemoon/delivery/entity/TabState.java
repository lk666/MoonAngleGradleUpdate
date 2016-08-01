package cn.com.bluemoon.delivery.entity;

import java.io.Serializable;

/**
 * 基础TabHost的数据项类
 * Created by luokai on 2016/6/12.
 */
public class TabState implements Serializable {
    private Class clazz;
    private int image;
    private int content;
    private String manager;


    public TabState(Class clazz, int image, int content, String manager) {
        this.clazz = clazz;
        this.image = image;
        this.content = content;
        this.manager = manager;
    }

    public String getManager() {
        return manager;
    }

    public Class getClazz() {
        return clazz;
    }

    public int getImage() {
        return image;
    }

    public int getContent() {
        return content;
    }

}
