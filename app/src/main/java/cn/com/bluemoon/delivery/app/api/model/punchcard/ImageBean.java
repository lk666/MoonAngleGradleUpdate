package cn.com.bluemoon.delivery.app.api.model.punchcard;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by liangjiangli on 2016/3/30.
 */
public class ImageBean implements Serializable {
    private String imgId;
    private String imgPath;
    private Bitmap bitmap;
    public ImageBean (){
    }
    public ImageBean (Bitmap bm) {
        bitmap = bm;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
