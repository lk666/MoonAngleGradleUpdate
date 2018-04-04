package cn.com.bluemoon.delivery.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import cn.com.bluemoon.lib.utils.LibFileUtil;

/**
 * Created by bm on 2016/8/5.
 */
public class FileUtil extends LibFileUtil {

    /*************************
     * file path
     *******************************/
    private static final String NAME = "BMDelivery";
    private static final String PATH_MAIN = Environment.getExternalStorageDirectory() + "/" + NAME;
    private static final String PATH_PHOTO = PATH_MAIN + "/images";
    private static final String PATH_TEMP = PATH_MAIN + "/temp";
    private static final String PATH_CACHE = PATH_MAIN + "/cache";
    private static final String PATH_CAMERA = Environment.getExternalStorageDirectory() +
            "/DCIM/Camera";
    private static final String PATH_DOWN = PATH_MAIN + "/download";

    public static String getPathCache() {
        return PATH_CACHE;
    }

    public static String getPathCamera() {
        return PATH_CAMERA;
    }

    public static String getPathMain() {
        return PATH_MAIN;
    }

    public static String getPathPhoto() {
        return PATH_PHOTO;
    }

    public static String getPathTemp() {
        return PATH_TEMP;
    }

    public static String getPathDown() {
        return PATH_DOWN;
    }

    /**
     * 递归删除目录下的文件
     */
    public static void deleteFileOrDirectory(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
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
        File f = new File(PATH_PHOTO);
        f.mkdirs();
        f = new File(PATH_TEMP);
        if (!f.mkdirs()) {
            deleteFolderFile(PATH_TEMP, false);
        }
        f = new File(PATH_CACHE);
        f.mkdirs();
        f = new File(PATH_CAMERA);
        f.mkdirs();
        f = new File(PATH_DOWN);
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

    public static void openFileDir(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.fromFile(file), "*/*");
        context.startActivity(intent);
    }

    /**
     * 复制文件（非目录）
     *
     * @param srcFile  要复制的源文件
     * @param destFile 复制到的目标文件
     * @return
     */
    public static boolean copyFile(String srcFile, String destFile) {
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
