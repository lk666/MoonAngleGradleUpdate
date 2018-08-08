package cn.com.bluemoon.delivery.utils.manager;

import android.content.Context;

import java.io.File;

import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.lib.utils.LibCacheUtil;

public class CacheManager extends LibCacheUtil {


	public static String getBlueMoonCacheSize(Context context) throws Exception {
		long size = 0;
		File fileCache = context.getCacheDir();
		File fileWebView = context.getDir("webview", Context.MODE_PRIVATE);
		File fileTemp = new File(FileUtil.getPathTemp());
		File fileCache2 = new File(FileUtil.getPathCache());
		File fileDown = new File(FileUtil.getPathDown());
		if (fileCache.exists()) {
			size = getFolderSize(fileCache);
		}
		if (fileWebView.exists()) {
			size = size + getFolderSize(fileWebView);
		}
		if (fileTemp.exists()) {
			size = size + getFolderSize(fileTemp);
		}
		if (fileCache2.exists()) {
			size = size + getFolderSize(fileCache2);
		}
		if (fileDown.exists()) {
			size = size + getFolderSize(fileDown);
		}
		return getFormatSize(size);
	}

	public static void clearBlueMoonCacheSize(Context context) {
		deleteFolderFile(context.getCacheDir().getAbsolutePath(), false);
		cleanWebViewCache(context);
		deleteFolderFile(FileUtil.getPathTemp(), false);
		deleteFolderFile(FileUtil.getPathCache(), false);
		deleteFolderFile(FileUtil.getPathDown(), false);
	}

}
