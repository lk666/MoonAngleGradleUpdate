package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import android.graphics.Bitmap;

/**
 * Created by LIANGJIANGLI on 2016/7/1.
 */
public class ImageInfo {
    private String fileId;
    private String filePath;
    private Bitmap bitmap;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

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
