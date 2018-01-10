package cn.com.bluemoon.delivery.module.document;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bluemoon.com.lib_x5.utils.download.X5DownLoadListener;
import bluemoon.com.lib_x5.utils.download.X5DownUtil;
import bluemoon.com.lib_x5.utils.download.X5DownloadManager;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * PDF展示基类
 * Created by bm on 2018/1/9.
 */

public abstract class BasePDFActivity extends BaseActivity implements View.OnClickListener,
        X5DownLoadListener, OnErrorListener,OnRenderListener {

    //文档页间隙
    protected final static int SPACING = 10;
    private PDFView pdfView;
    TextView txtProgress;
    TextView txtLoad;
    TextView txtContent;
    RelativeLayout layoutProgress;

    private X5DownloadManager downloadManager;
    private long downloadId;
    private ScheduledExecutorService scheduledExecutorService;

    protected String fileUrl;
    protected String filePath;
    protected int page = 0;

    protected boolean isLoadFinish;

    public static void actStart(Context context, String fileUrl, String filePath, int page,
                                Class cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("url", fileUrl);
        intent.putExtra("path", filePath);
        intent.putExtra("page", page);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        fileUrl = getIntent().getStringExtra("url");
        filePath = getIntent().getStringExtra("path");
        page = getIntent().getIntExtra("page", 0);
    }

    @Override
    public void initView() {
        pdfView = (PDFView) findViewById(R.id.pdf_view);
        txtProgress = (TextView) findViewById(R.id.txt_progress);
        txtLoad = (TextView) findViewById(R.id.txt_load);
        txtContent = (TextView) findViewById(R.id.txt_content);
        layoutProgress = (RelativeLayout) findViewById(R.id.layout_progress);

        txtLoad.setOnClickListener(this);

        //打开文件
        if (!TextUtils.isEmpty(fileUrl) || !TextUtils.isEmpty(filePath)) {
            openFile();
        }
    }

    /**
     * 打开已有的pdf
     */
    private void openFile() {
        openFile(fileUrl, filePath, page);
    }

    /**
     * 打开文件，如果有路径，直接打开本地，否则打开网址下载
     */
    protected void openFile(String url, String path, int defaultPage) {
        if (TextUtils.isEmpty(url) && TextUtils.isEmpty(path)) {
            downResult(false);
            return;
        }
        fileUrl = url;
        filePath = path;
        page = defaultPage;
            isLoadFinish = false;
            if (!TextUtils.isEmpty(path)) {
                displayFromPath(path);
            } else if (!TextUtils.isEmpty(url) && PublicUtil.hasIntenet(this)) {
                if (downloadManager == null) {
                    downloadManager = new X5DownloadManager(this, this);
                    //注册下载广播
                    downloadManager.registerReceiver();
                }
                down(url);
            } else {
                downResult(false);
            }
    }

    private void down(String url) {
        if (downloadManager != null) {
            downloadManager.downClick(url, FileUtil.getPathDown(), false);
        }
    }

    protected void displayFromPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            downResult(false);
            return;
        }
        if (pdfView == null) {
            return;
        }
        releasePdf();
        pdfView.fromFile(file)
                .defaultPage(page)
                .enableAnnotationRendering(true)
                .onError(this)
                .spacing(SPACING) // in dp
                .onRender(this)
                .load();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    public void onDownStart(long downloadId, String url, String path) {
        this.downloadId = downloadId;
        startTimer();
    }

    @Override
    public void onLoading(long downloadId, String url, String path) {
        this.downloadId = downloadId;
        startTimer();
    }

    @Override
    public void onDownFinish(long downloadId, String url, boolean isSuccess) {
        stopTimer();
        downResult(isSuccess);
    }

    /**
     * 下载结果展示
     */
    private void downResult(boolean isSuccess) {
        if (isSuccess) {
            filePath = X5DownUtil.getFilePath(FileUtil.getPathDown(), fileUrl);
            displayFromPath(filePath);
        } else {
            filePath = null;
            ViewUtil.setViewVisibility(txtProgress, View.INVISIBLE);
            ViewUtil.setViewVisibility(txtLoad, View.VISIBLE);
            if (txtContent != null)
                txtContent.setText(R.string.load_fail);
        }
    }

    private void startTimer() {
        if (downloadManager != null) {
            ViewUtil.setViewVisibility(layoutProgress, View.VISIBLE);
            ViewUtil.setViewVisibility(txtProgress, View.VISIBLE);
            ViewUtil.setViewVisibility(txtLoad, View.GONE);
            txtProgress.setText(getString(R.string.down_progress, "0"));
            txtContent.setText(R.string.down_loading);
            if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
                scheduledExecutorService.shutdownNow();
            }
            scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    updateView();
                }
            }, 100, 500, TimeUnit.MILLISECONDS);
        }
    }

    private void stopTimer() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 1 && txtProgress != null) {
                txtProgress.setText(AppContext.getInstance().getString(R.string.down_progress,
                        msg.obj));
            }
            super.handleMessage(msg);
        }
    };

    private void updateView() {
        if (downloadManager != null && downloadId != 0) {
            int[] ints = downloadManager.getBytesAndStatus(downloadId);
            int progress = (int) ((float) ints[0] / (float) ints[1] * (float) 100);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = String.valueOf(progress);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onError(Throwable t) {
        downResult(false);
    }

    public void releasePdf() {
        if (pdfView != null && !pdfView.isRecycled()) {
            pdfView.recycle();
        }
    }

    @Override
    protected void onDestroy() {
        releasePdf();
        if (downloadManager != null) {
            //注销下载广播
            downloadManager.unregisterReceiver();
        }
        stopTimer();
        super.onDestroy();
    }

    @Override
    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
        ViewUtil.setViewVisibility(layoutProgress, View.GONE);
        isLoadFinish = true;
    }

    @Override
    public void onClick(View v) {
        if (v == txtLoad) {
            openFile();
        }
    }
}
