package cn.com.bluemoon.delivery.utils;

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

}
