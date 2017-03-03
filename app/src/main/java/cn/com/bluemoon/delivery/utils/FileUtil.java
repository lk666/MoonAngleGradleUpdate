package cn.com.bluemoon.delivery.utils;

import android.text.TextUtils;

import java.io.File;

import cn.com.bluemoon.lib.utils.LibFileUtil;

/**
 * Created by bm on 2016/8/5.
 */
public class FileUtil extends LibFileUtil {

    public static void deleteFileOrDirectory(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }else if (file.isDirectory()) {
                java.io.File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i].getPath());
                }
            }
        }
    }

    /**
     * 初始化文件夹
     */
    public static void init() {
        File f = new File(Constants.PATH_PHOTO);
        f.mkdirs();
        f = new File(Constants.PATH_TEMP);
        if (!f.mkdirs()) {
            deleteFolderFile(Constants.PATH_TEMP, false);
        }
        f = new File(Constants.PATH_CACHE);
        f.mkdirs();
        f = new File(Constants.PATH_CAMERA);
        f.mkdirs();
    }
    /**
     * 递归删除目录
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
