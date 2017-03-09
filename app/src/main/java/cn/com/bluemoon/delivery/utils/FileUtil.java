package cn.com.bluemoon.delivery.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import android.text.TextUtils;
import cn.com.bluemoon.lib.utils.LibFileUtil;

/**
 * Created by bm on 2016/8/5.
 */
public class FileUtil extends LibFileUtil {

    /*************************
     * file path
     *******************************/
    private static final String NAME        = "BMDelivery";
    private static final String PATH_MAIN   = Environment.getExternalStorageDirectory() + "/" + NAME;
    private static final String PATH_PHOTO  = PATH_MAIN + "/images";
    private static final String PATH_TEMP   = PATH_MAIN + "/temp";
    private static final String PATH_CACHE  = PATH_MAIN + "/cache";
    private static final String PATH_CAMERA = Environment.getExternalStorageDirectory() +
            "/DCIM/Camera";
    private static final String PATH_DOWN   = PATH_MAIN + "/download";

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
     * 初始化文件夹
     */
    public static void init() {
        if (checkExternalSDExists()) {
            File f = new File(PATH_PHOTO);
            f.mkdirs();
            f = new File(PATH_TEMP);
            f.mkdirs();
            f = new File(PATH_CACHE);
            f.mkdirs();
            f = new File(PATH_CAMERA);
            f.mkdirs();
            f = new File(PATH_DOWN);
            f.mkdirs();
        }
    }

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
