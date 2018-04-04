package cn.com.bluemoon.delivery.module.contract;

import android.app.DownloadManager;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import bluemoon.com.lib_x5.utils.download.X5LoadItem;
import bluemoon.com.lib_x5.utils.download.X5MIME;

/**
 * 记录DownloadManager正在下载的任务
 */

public class DownUtil {

    private static Map<String, X5LoadItem> downMap = new HashMap<>();

    //添加下载项
    public static void putUrl(String url, long downloadId, String path) {
        downMap.put(getMd5Url(url), new X5LoadItem(url, downloadId, path));
    }

    //移除下载项
    public static void removeDESUrl(String desUrl) {
        downMap.remove(desUrl);
    }

    //移除下载项
    public static void removeUrl(String url) {
        downMap.remove(getMd5Url(url));
    }

    //移除某个id对应的所有项
    public static void removeId(long downloadId) {
        for (String key : downMap.keySet()) {
            if (downMap.get(key).getId() == downloadId) {
                downMap.remove(key);
            }
        }
    }

    //根据url获取下载id，没有在下载队列则返回-1
    public static long getDownloadId(String url) {
        String desUrl = DownUtil.getMd5Url(url);
        if (downMap.containsKey(desUrl)) {
            return downMap.get(desUrl).getId();
        }
        return -1;
    }

    //根据id获取加密url
    public static String getMd5UrlById(long downloadId) {
        for (String key : downMap.keySet()) {
            if (downMap.get(key).getId() == downloadId) {
                return key;
            }
        }
        return "";
    }

    //根据id获取原始url
    public static String getUrlById(long downloadId) {
        for (String key : downMap.keySet()) {
            if (downMap.get(key).getId() == downloadId) {
                return downMap.get(key).getUrl();
            }
        }
        return "";
    }

    //根据id获取文件地址
    public static String getPathById(long downloadId) {
        for (String key : downMap.keySet()) {
            if (downMap.get(key).getId() == downloadId) {
                return downMap.get(key).getFilePath();
            }
        }
        return "";
    }

    //获取url经过Md5之后的值
    public static String getMd5Url(String url) {
        return getMd5Lower32(url);
    }

    public static String getFilePathWithName(String pathDir, String fileName) {
        return pathDir + File.separator + fileName;
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     */
    private static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
    /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < X5MIME.MIME_MapTable.length; i++) {
            //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(X5MIME.MIME_MapTable[i][0])) {
                type = X5MIME.MIME_MapTable[i][1];
            }
        }
        return type;
    }

    public static String getMd5Lower32(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean containId(long downloadId) {
        for (String key : downMap.keySet()) {
            if (downMap.get(key).getId() == downloadId) {
                return true;
            }
        }
        return false;
    }

    public static void clear(DownloadManager downloadManager) {
        for (String key : downMap.keySet()) {
            downloadManager.remove(downMap.get(key).getId());
        }
        downMap.clear();
    }
}
