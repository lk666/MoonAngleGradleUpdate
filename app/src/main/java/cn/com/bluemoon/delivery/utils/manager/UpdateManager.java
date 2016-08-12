package cn.com.bluemoon.delivery.utils.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;


public class UpdateManager {

    private static final String TAG = "UpdateManager";
    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private static final String NAME_START = "MoonAngel";
    private static final String NAME_END = ".apk";
    private String mSavePath;
    private String mSaveName;
    private int progress;
    private boolean cancelUpdate = false;
    private Context mContext;
    private ProgressBar mProgress;
    private TextView txtTime;
    private CommonAlertDialog mDownloadDialog;
    private String downloadUrl;
    private UpdateCallback callback;
//	private boolean changeTextColor;

    private Handler mHandler = new Handler() {
        @SuppressLint("DefaultLocale")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD:
                    mProgress.setProgress(progress);
                    txtTime.setText(String.format("%d%%", progress));
                    break;
                case DOWNLOAD_FINISH:
                    mDownloadDialog.dismiss();
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Context context, String url, String version, UpdateCallback callback) {
        this.mContext = context;
        this.callback = callback;
        this.downloadUrl = url;
        this.mSaveName = NAME_START + version + NAME_END;
    }


    public void showDownloadDialog() {
        try {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.update_progress, null);
            mProgress = (ProgressBar) v.findViewById(R.id.progress_update);
            txtTime = (TextView) v.findViewById(R.id.txt_time);
            mProgress.setProgress(0);

            CommonAlertDialog.Builder builder = new CommonAlertDialog.Builder(
                    mContext);
            builder.setTitle(R.string.new_version_updating);
            builder.setView(v);
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null) {
                        callback.onCancel();
                    }
                    dialog.dismiss();
                    cancelUpdate = true;
                }
            });
            mDownloadDialog = builder.create();
            mDownloadDialog.show();
            new downloadApkThread().start();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailUpdate();
            }
        }

    }

    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    mSavePath = Constants.PATH_TEMP;
                    URL url = new URL(downloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    LogUtils.d(TAG, "getResponseCode  result =" + conn.getResponseCode());
                    if (conn.getResponseCode() >= 400) {
                        PublicUtil.showToastServerOvertime(mContext);
                        if (callback != null) {
                            callback.onFailUpdate();
                        }
                        return;
                    }
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    if (is == null) {
                        if (callback != null) {
                            callback.onFailUpdate();
                        }
                        return;
                    }
                    File file = new File(mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, mSaveName);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailUpdate();
                }
            }
        }
    }

    ;

    /**
     * install apk
     */
    private void installApk() {
        try {
            File apkfile = new File(mSavePath, mSaveName);
            if (!apkfile.exists()) {
                return;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                    "application/vnd.android.package-archive");
            mContext.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface UpdateCallback {
        void onCancel();

        void onFailUpdate();
    }
}
