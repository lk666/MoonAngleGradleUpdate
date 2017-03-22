package cn.com.bluemoon.delivery.common;

import android.content.Context;
import android.content.Intent;

import cn.com.bluemoon.delivery.module.base.interf.BMDownLoadListener;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.download.BMDownloadManager;

public class DownWebViewActivity extends X5WebViewActivity implements BMDownLoadListener{

    private BMDownloadManager downloadManager;

    public static void startAction(Context context, String url, String title, boolean isActionBar,
                                   boolean isBackByJs) {
        Intent intent = new Intent(context, DownWebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("actionbar", isActionBar);
        intent.putExtra("back", isBackByJs);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
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

    //重写点击下载（判断是否已经下载，正在下载，未下载）
    @Override
    protected void downClick(String url,String mimeType) {
        downloadManager.downClick(url,mimeType);
    }

    @Override
    public void onDownStart(long downloadId, String url, String path) {
        LogUtils.d("down start ..." + url + " id:" + downloadId + " path:" + path);
        ViewUtil.toast("开始下载...");
    }

    @Override
    public void onLoading(long downloadId, String url) {
        LogUtils.d("on loading ..." + url + " id:" + downloadId);
        ViewUtil.toast("正在下载...");
    }

    @Override
    public void onDownFinish(long downloadId, String url, boolean isSuccess) {
        LogUtils.d(isSuccess ? downloadId + "：down success" : downloadId + "：down fail");
        ViewUtil.toast(isSuccess ? "下载成功" : "下载失败");
    }

}
