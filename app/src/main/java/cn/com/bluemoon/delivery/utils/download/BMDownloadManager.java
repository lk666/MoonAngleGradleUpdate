package cn.com.bluemoon.delivery.utils.download;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.BMDownLoadListener;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * 后台下载管理工具类
 * Created by bm on 2017/2/28.
 */

public class BMDownloadManager {

    private static DownloadManager downloadManager;
    private CompleteReceiver completeReceiver;
    private Context context;
    private BMDownLoadListener listener;

    public BMDownloadManager(Context context) {
        this.context = context;
        init();
    }

    /**
     * 需要监听下载状态必须在Activity中
     * oncreate（）方法中添加广播接收器，调用registerReceiver(Context context)方法
     * ondestory（）方法中注销广播接收器，调用unregisterReceiver(Context context)方法
     */
    public BMDownloadManager(Context context, BMDownLoadListener listener) {
        this.context = context;
        this.listener = listener;
        init();
    }

    //初始化
    public void init() {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private CompleteReceiver getCompleteReceiver() {
        if (completeReceiver == null) {
            completeReceiver = new CompleteReceiver();
        }
        return completeReceiver;
    }

    //注册广播接收器
    public void registerReceiver() {
        if (context != null) {
            CompleteReceiver receiver = getCompleteReceiver();
            context.registerReceiver(receiver, new IntentFilter(DownloadManager
                    .ACTION_DOWNLOAD_COMPLETE));
        }
    }

    //注销广播接收器
    public void unregisterReceiver() {
        if (context != null) {
            CompleteReceiver receiver = getCompleteReceiver();
            context.unregisterReceiver(receiver);
        }
    }

    //点击下载，根据路径获取文件启动类型
    public void downClick(String url) {
        downClick(url, null);
    }

    //点击下载按钮
    public void downClick(String url, String mimeType) {

        long id = DownUtil.getDownloadId(url);
        String path = DownUtil.getFilePath(url);
        //先判断是否正在下载
        if (id != -1 && isRunning(id)) {
            if (listener != null) {
                listener.onLoading(id, url);
            }
            return;
        }
        //在判断是否存在
        if (FileUtil.checkFilePathExists(path)) {
            FileUtil.openFile(context, path, mimeType);
            return;
        }
        //都不满足再调用下载
        down(url, mimeType);
    }

    //开启后台服务下载，默认路径和文件名和文件启动类型
    public long down(String url) {
        return down(url, null);
    }

    //开启后台服务下载，默认路径和文件名
    public long down(String url, String mimeType) {
        String fileName = DownUtil.getFileName(url);
        String path = DownUtil.getFilePathWithName(fileName);
        return down(url, path, fileName, mimeType);
    }

    //开启后台服务下载(带文件启动类型)
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public long down(String url, String path, String fileName, String mimeType) {
        try {

            //设置路径
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            //设置描述文字
            request.setDescription(path);
            //设置标题
            request.setTitle(fileName);

            //设置下载路径
            request.setDestinationUri(Uri.fromFile(new File(path)));

            //设置文件启动类型
            if (TextUtils.isEmpty(mimeType)) {
                request.setMimeType(mimeType);
            }

            request.allowScanningByMediaScanner();

            //设置通知栏进度条可见
            request.setNotificationVisibility(DownloadManager.Request
                    .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


            // 开启下载，并得到对应唯一的下载id
            long downloadId = downloadManager.enqueue(request);

            if (listener != null) {
                listener.onDownStart(downloadId, url, path);
            }
            //缓存下载id
            DownUtil.putUrl(url, downloadId, path);
            return downloadId;

        } catch (SecurityException e) {
            e.printStackTrace();
            ViewUtil.toast(R.string.no_file_permission);

            if (listener != null) {
                listener.onDownFinish(-1, url, false);
            }
        }
        return -1;
    }

    //判断某个url是否正在下载
    public boolean isRunning(String url) {
        long downloadId = DownUtil.getDownloadId(url);
        return isRunning(downloadId);
    }

    //判断某个id是否正在下载
    public boolean isRunning(long downloadId) {
        int state = getStatusById(downloadId);
        return state == DownloadManager.STATUS_RUNNING;
    }

    //根据id获取下载状态
    public int getStatusById(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        int status = 0;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            return status;
        }
    }

    //下载完成广播接收
    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (listener != null) {
                int status = getStatusById(downloadId);

                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        listener.onDownFinish(downloadId, DownUtil.getUrlById(downloadId), true);
                        break;
                    case DownloadManager.STATUS_FAILED:
                        listener.onDownFinish(downloadId, DownUtil.getUrlById(downloadId), false);
                        break;
                }
            }
            DownUtil.removeDESUrl(DownUtil.getMd5UrlById(downloadId));
        }
    }

}
