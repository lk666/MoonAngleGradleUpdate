package cn.com.bluemoon.delivery.utils;

import android.content.Context;

import java.io.File;

import cn.com.bluemoon.lib.utils.LibCacheUtil;

public class CacheManager extends LibCacheUtil {


	public static String getBlueMoonCacheSize(Context context) throws Exception {
		long size = 0;
		File file_cache = context.getCacheDir();
		File file_webview = context.getDir("webview", Context.MODE_PRIVATE);
		File file_temp = new File(Constants.PATH_TEMP);
		if (file_cache.exists()) {
			size = getFolderSize(file_cache);
		}
		if (file_webview.exists()) {
			size = size + getFolderSize(file_webview);
		}
		if (file_temp.exists()) {
			size = size + getFolderSize(file_temp);
		}
		return getFormatSize(size);
	}

	public static void clearBlueMoonCacheSize(Context context) {
		deleteFolderFile(context.getCacheDir().getAbsolutePath(), false);
		cleanWebViewCache(context);
		deleteFolderFile(Constants.PATH_TEMP, false);
	}

}
