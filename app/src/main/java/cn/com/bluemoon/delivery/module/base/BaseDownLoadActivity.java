package cn.com.bluemoon.delivery.module.base;

import cn.com.bluemoon.delivery.module.base.interf.BMDownLoadListener;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.download.BMDownloadManager;

/**
 * Created by bm on 2017/2/28.
 */

public abstract class BaseDownLoadActivity extends BaseActivity implements BMDownLoadListener {

    private BMDownloadManager downloadManager;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        downloadManager = new BMDownloadManager(this, this);
        //注册广播
        downloadManager.registerReceiver();
    }

    @Override
    protected void onDestroy() {
        //注销广播
        downloadManager.unregisterReceiver();
        super.onDestroy();
    }

    //点击下载按钮（判断是否已经下载，正在下载，未下载）
    protected void downClick(String url) {
        downloadManager.downClick(url);
    }

    //下载文件方法
    protected long downFile(String url) {
        if (downloadManager != null) {
            return downloadManager.down(url);
        }
        return -1;
    }

    @Override
    public void onDownStart(long downloadId, String url, String path) {
        LogUtils.d("down start ..." + url + " id:" + downloadId + " path:" + path);
        toast("开始下载...");
    }

    @Override
    public void onLoading(long downloadId, String url) {
        LogUtils.d("on loading ..." + url + " id:" + downloadId);
        toast("正在下载...");
    }

    @Override
    public void onDownFinish(long downloadId, String url, boolean isSuccess) {
        LogUtils.d(isSuccess ? downloadId + "：down success" : downloadId + "：down fail");
        toast(isSuccess ? "下载成功" : "下载失败");
    }

    //工具类方法

    //获取下载管理器
    final protected BMDownloadManager getDownloadManager(){
        return downloadManager;
    }

}
