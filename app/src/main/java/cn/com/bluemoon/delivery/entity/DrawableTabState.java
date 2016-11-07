package cn.com.bluemoon.delivery.entity;

/**
 * 代码定义
 * Created by ljl on 2016/10/11.
 */
public class DrawableTabState extends TabState {
    private int imgNormal;
    private int imgSelected;

    public DrawableTabState(Class clazz, int imgSelected, int imgNormal, int content) {
        super(clazz, 0, content);
        this.imgNormal = imgNormal;
        this.imgSelected = imgSelected;
    }


    public int getImgSelected() {
        return imgSelected;
    }

    public void setImgSelected(int imgSelected) {
        this.imgSelected = imgSelected;
    }

    public int getImgNormal() {
        return imgNormal;
    }

    public void setImgNormal(int imgNormal) {
        this.imgNormal = imgNormal;
    }
}
