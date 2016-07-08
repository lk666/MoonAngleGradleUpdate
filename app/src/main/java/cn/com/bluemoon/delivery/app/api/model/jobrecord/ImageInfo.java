package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import android.graphics.Bitmap;

/**
 * Created by LIANGJIANGLI on 2016/7/1.
 */
public class ImageInfo {
    private long fileid = -1;

    public long getFileid() {
        return fileid;
    }

    public void setFileid(long fileid) {
        this.fileid = fileid;
    }

    private String filePath;
    private Bitmap bitmap;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
