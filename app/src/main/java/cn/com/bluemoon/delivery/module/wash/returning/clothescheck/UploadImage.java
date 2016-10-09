package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import java.io.Serializable;
import java.util.UUID;

/**
 * 上传的异常图片
 */
public class UploadImage implements Serializable {
    /**
     * 图片名称
     */
    private String fileName;
    /**
     * 图片路径
     */
    private String imagePath;

    public UploadImage(String imagePath) {
        this.fileName = UUID.randomUUID() + ".png";
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
