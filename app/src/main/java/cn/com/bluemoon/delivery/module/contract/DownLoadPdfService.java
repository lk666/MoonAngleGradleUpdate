package cn.com.bluemoon.delivery.module.contract;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import bluemoon.com.lib_x5.utils.ToastUtil;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.utils.FileUtil;

/**
 * 下载pdf，只支持单个
 */
public class DownLoadPdfService extends IntentService {
    public DownLoadPdfService() {
        super("DownLoadPdfService");
    }

    private DownloadManager downloadManager;

    private CompleteReceiver completeReceiver = new CompleteReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        downloadManager = (DownloadManager) AppContext.getInstance().getSystemService(Context
                .DOWNLOAD_SERVICE);
        //注册下载完成广播
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager
                .ACTION_DOWNLOAD_COMPLETE));
    }

    private class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (!DownUtil.containId(downloadId)) {
                // 过滤别的广播
                return;
            }
            int status = getStatusById(downloadId);
            DownloadEvent event = new DownloadEvent(downloadId);
            switch (status) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    // 下载完成后，移到下载文件夹
                    String path = DownUtil.getPathById(downloadId);
                    File f = new File(path);
                    try {
                        String des = FileUtil.getPathDown() + File.separator + f .getName();
                        FileUtil.deleteFile(DownLoadPdfService.this, new File(des));
                        FileUtil.copyFile(path, des);
                        FileUtil.deleteFile(DownLoadPdfService.this, f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    event.progress = -2;
                    EventBus.getDefault().post(event);
                    break;
                case DownloadManager.STATUS_FAILED:
                    event.progress = -3;
                    EventBus.getDefault().post(event);
                    break;
            }

            stopTimer(downloadId);
        }
    }

    /**
     * 监听进度
     */
    private void startTimer(long downloadId) {
        while (isRun && DownUtil.containId(downloadId)) {
            int[] ints = getBytesAndStatus(downloadId);
            int progress = (int) ((float) ints[0] / (float) ints[1] * (float) 100);
            DownloadEvent event = new DownloadEvent(downloadId);
            event.progress = progress;
            EventBus.getDefault().post(event);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager
                        .COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager
                        .COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * 停止监听进度
     */
    private void stopTimer(long downloadId) {
        DownUtil.removeId(downloadId);
    }

    public static class DownloadEvent {
        /**
         * 下载回调事件
         * -1-开始下载，用于记录downloadId
         * -2-下载完成
         * -3-下载失败
         * else-下载进度报告
         */
        public int progress;

        /**
         * 下载唯一标识
         */
        public long downloadId;

        public DownloadEvent(long downloadId) {
            this.downloadId = downloadId;
        }
    }

    /**
     * 根据id获取下载状态
     */
    private int getStatusById(long downloadId) {
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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String url = intent.getStringExtra("url");
        String name = intent.getStringExtra("name");
        try {
            name += url.substring(url.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        long id = DownUtil.getDownloadId(url);
        String path = DownUtil.getFilePathWithName(FileUtil.getPathTemp(), name);
        // 先判断是否正在下载
        if (id != -1 && isRunning(id)) {
            startTimer(id);
        } else {
            // 再判断是否存在文件
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }

            // 调用下载
            long downloadId = down(url, path, name, null);

            DownloadEvent event = new DownloadEvent(downloadId);

            if (downloadId != -1) {
                event.progress = -1;
                EventBus.getDefault().post(event);
                startTimer(downloadId);
            } else {
                event.progress = -3;
                EventBus.getDefault().post(event);
            }
        }
    }

    //开启后台服务下载(带文件启动类型)
    public long down(String url, String path, String fileName, String mimeType) {
        long downloadId = -1;
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
            if (!TextUtils.isEmpty(mimeType)) {
                request.setMimeType(mimeType);
            }

            request.allowScanningByMediaScanner();

            //设置通知栏进度条可见
            //            if(visibility!=-1){
            //                request.setNotificationVisibility(visibility);
            //            }

            // 开启下载，并得到对应唯一的下载id
            downloadId = downloadManager.enqueue(request);

            //缓存下载id
            DownUtil.putUrl(url, downloadId, path);
            return downloadId;
        } catch (SecurityException e) {
            e.printStackTrace();
            ToastUtil.toast(this, bluemoon.com.lib_x5.R.string.no_file_permission);
        }
        return downloadId;
    }

    //判断某个id是否正在下载
    public boolean isRunning(long downloadId) {
        int state = getStatusById(downloadId);
        return state == DownloadManager.STATUS_RUNNING;
    }

    private boolean isRun = true;

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
        unregisterReceiver(completeReceiver);
        completeReceiver = null;
        DownUtil.clear(downloadManager);
        downloadManager = null;
    }
}