package cn.com.bluemoon.delivery.module.document;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
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
        X5DownLoadListener, OnErrorListener, OnRenderListener, OnDrawListener {

    //文档页间隙
    protected final static int SPACING = 10;
    protected PDFView pdfView;
    protected TextView txtProgress;
    protected TextView txtLoad;
    protected TextView txtContent;
    protected RelativeLayout layoutProgress;

    private X5DownloadManager downloadManager;
    private long downloadId = -1;
    private ScheduledExecutorService scheduledExecutorService;

    protected List<String> imgUrls;
    protected String fileUrl;
    protected String filePath;
    protected int page = 0;

    protected boolean isLoadFinish;

    private int index = -1;

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
        if (imgUrls != null && !imgUrls.isEmpty()) {
            openFile(imgUrls);
        } else {
            openFile(fileUrl, filePath, page);
        }
    }

    /**
     * 通过图片列表生成pdf打开
     *
     * @param imgUrls
     */
    protected void openFile(List<String> imgUrls) {
        if (imgUrls == null || imgUrls.isEmpty()) {
            downResult(false);
            return;
        }
        //pdf命名是根据第一张图片地址生成，如果pdf存在，则直接打开，否则去下载图片
        String path = getPdfPath(imgUrls);
        if(X5DownUtil.checkFilePathExists(path)){
            openFile(null,path,0);
            return;
        }
        showDownView();
        this.imgUrls = imgUrls;
        index = 0;
        if (downloadManager == null) {
            downloadManager = new X5DownloadManager(this, this);
            //注册下载广播
            downloadManager.registerReceiver();
        }
        down(imgUrls.get(0));
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

    /**
     * 是否用图片拼凑的pdf
     */
    protected boolean isImageType() {
        return false;
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
                .onDraw(this)
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
        // TODO: 2018/1/22 ID 相等时才执行操作，防止接收到其他页面的下载结果，这里需要优化
        if(this.downloadId==downloadId){
            this.downloadId = -1;//下载完成之后需要重置id
            stopTimer();
            downResult(isSuccess);
        }
    }

    /**
     * 下载结果展示
     */
    private void downResult(boolean isSuccess) {
        if (isSuccess) {
            if (isImageType() && imgUrls != null) {
                if (index < 0) {
                    downResult(false);
                } else if (index == imgUrls.size() - 1) {
                    createPDF(imgUrls);
                } else {
                    int progress = (int) ((float) (index + 1) / (float) imgUrls.size() * (float)
                            100);
                    txtProgress.setText(getString(R.string.down_progress, String.valueOf
                            (progress)));
                    index++;
                    down(imgUrls.get(index));

                }
            } else {
                filePath = X5DownUtil.getFilePath(FileUtil.getPathDown(), fileUrl);
                displayFromPath(filePath);
            }
        } else {
            filePath = null;
            ViewUtil.setViewVisibility(txtProgress, View.INVISIBLE);
            ViewUtil.setViewVisibility(txtLoad, View.VISIBLE);
            if (txtContent != null)
                txtContent.setText(R.string.load_fail);
        }
    }

    private void showDownView() {
        ViewUtil.setViewVisibility(layoutProgress, View.VISIBLE);
        ViewUtil.setViewVisibility(txtProgress, View.VISIBLE);
        ViewUtil.setViewVisibility(txtLoad, View.GONE);
        txtProgress.setText(getString(R.string.down_progress, "0"));
        txtContent.setText(R.string.down_loading);
    }

    private void startTimer() {
        if (isImageType()) {
            return;
        }
        if (downloadManager != null) {
            showDownView();
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
        if (isImageType()) {
            return;
        }
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

    /**
     * 获取pdf页数
     */
    protected int getPageCount() {
        if (pdfView != null) {
            return pdfView.getPageCount();
        }
        return 0;
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
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void onClick(View v) {
        if (v == txtLoad) {
            openFile();
        }
    }


    /**
     * 图片生成pdf
     */
    private void createPDF(List<String> imgUrls) {
        if (imgUrls == null || imgUrls.isEmpty()) {
            return;
        }
        //根据第一张图片路径生成pdf
        String pdfPath = getPdfPath(imgUrls);
        try {
            Image img = Image.getInstance(X5DownUtil.getFilePath(FileUtil.getPathDown(),
                    imgUrls.get(0)));
//            //页面大小
            Rectangle rect = new Rectangle(img.getWidth(), img.getHeight());
            //页面背景色
//            rect.setBackgroundColor(BaseColor.GRAY);
            Document document = new Document(rect);
            document.setMargins(0.0F, 0.0F, 0.0F, 0.0F);
            FileOutputStream outputStream = new FileOutputStream(pdfPath);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(img);
            for (int i = 1; i < imgUrls.size(); i++) {
                document.newPage();
                Image imgI = Image.getInstance(X5DownUtil.getFilePath(FileUtil.getPathDown(),
                        imgUrls.get(i)));
                document.add(imgI);
            }
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        openFile(null, pdfPath, 0);
    }

    /**
     * 根据第一张图片地址生成pdf命名。
     */
    public String getPdfPath(List<String> imgUrls) {
        if (imgUrls == null || imgUrls.isEmpty()) {
            return null;
        }
        //根据第一张图片路径生成pdf
        return FileUtil.getPathDown() + File.separator + X5DownUtil.getMd5Url(imgUrls.get(0)) + "" +
                ".pdf";
    }
}
