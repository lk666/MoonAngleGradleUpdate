package cn.com.bluemoon.delivery.app.api.model.knowledge;

import java.util.List;

/**
 * Created by allenli on 2016/5/20.
 */
public class Knowledge {
    private String catFirstId;
    private String catFirstName;
    private String imgOn;
    private String imgOut;
    private List<CartItem> catList;

    public String getCatFirstId() {
        return catFirstId;
    }

    public void setCatFirstId(String catFirstId) {
        this.catFirstId = catFirstId;
    }

    public String getCatFirstName() {
        return catFirstName;
    }

    public void setCatFirstName(String catFirstName) {
        this.catFirstName = catFirstName;
    }

    public String getImgOn() {
        return imgOn;
    }

    public void setImgOn(String imgOn) {
        this.imgOn = imgOn;
    }

    public String getImgOut() {
        return imgOut;
    }

    public void setImgOut(String imgOut) {
        this.imgOut = imgOut;
    }

    public List<CartItem> getCatList() {
        return catList;
    }

    public void setCatList(List<CartItem> catList) {
        this.catList = catList;
    }
}
