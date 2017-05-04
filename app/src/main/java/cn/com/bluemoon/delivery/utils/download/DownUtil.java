package cn.com.bluemoon.delivery.utils.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.lib.utils.MD5Util;

/**
 * Created by bm on 2017/3/1.
 */

public class DownUtil {

    private static Map<String, LoadItem> downMap = new HashMap<>();

    //添加下载项
    public static void putUrl(String url,long downloadId,String path){
        downMap.put(getMd5Url(url),new LoadItem(url,downloadId,path));
    }

    //移除下载项
    public static void removeDESUrl(String desUrl){
        downMap.remove(desUrl);
    }

    //移除下载项
    public static void removeUrl(String url){
        downMap.remove(getMd5Url(url));
    }

    //移除某个id对应的所有项
    public void removeId(long downloadId) {
        for (String key : downMap.keySet()) {
            if (downMap.get(key).getId() == downloadId) {
                downMap.remove(key);
                LogUtils.d("remove key :" + key + " and id：" + downloadId);
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

    //获取url经过Md5之后的值
    public static String getMd5Url(String url){
        return MD5Util.getMd5Lower32(url);
    }

    //根据url得到相应的文件名
    public static String getFileName(String url){
        return getMd5Url(url)+url.substring(url.lastIndexOf("."));
    }

    //根据url得到相应的下载路径
    public static String getFilePath(String url){
        return FileUtil.getPathDown() + File.separator + getFileName(url);
    }

    public static String getFilePathWithName(String fileName){
        return FileUtil.getPathDown() + File.separator + fileName;
    }
}
