package cn.com.bluemoon.delivery.utils.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;


public class UpdateManager {

    private static final String TAG = "UpdateManager";
    private static final String NAME_START = "MoonAngel";
    private static final String NAME_END = ".apk";
    private String mSavePath;
    private String mSaveName;
    private Context mContext;
    private ProgressBar mProgress;
    private TextView txtTime;
    private CommonAlertDialog mDownloadDialog;
    private String downloadUrl;
    private UpdateCallback callback;
    private DownloadAsyncTask downloadAsyncTask;

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
                    if (downloadAsyncTask != null) {
                        downloadAsyncTask.cancel(true);
                    }
                }
            });
            mDownloadDialog = builder.create();
            mDownloadDialog.show();
            downloadAsyncTask = new DownloadAsyncTask();
            downloadAsyncTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
            onFailUpdate();
        }

    }

    private class DownloadAsyncTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    return false;
                }
                mSavePath = Constants.PATH_TEMP;
                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                LogUtils.d(TAG, "getResponseCode  result =" + conn.getResponseCode());
                if (conn.getResponseCode() >= 400) {
                    ViewUtil.toastOvertime();
                    return false;
                }
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                if (is == null) {
                    return false;
                }
                File file = new File(mSavePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File apkFile = new File(mSavePath, mSaveName);
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    int progress = (int) (((float) count / length) * 100);
                    publishProgress(progress);
                    if (numread <= 0) {
                        publishProgress(100);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!isCancelled());
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values != null && values.length > 0) {
                int progress = values[0];
                mProgress.setProgress(progress);
                txtTime.setText(String.format("%d%%", values[0]));
                if (progress >= 100) {
                    onFinishUpdate();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                onFailUpdate();
            }
        }
    }

    /**
     * 下载完成操作
     */
    private void onFinishUpdate() {
        if (mDownloadDialog != null)
            mDownloadDialog.dismiss();
        if (callback != null) {
            callback.onFinishUpdate();
        }
        installApk();
    }

    /**
     * 下载失败的操作
     */
    private void onFailUpdate() {
        if (mDownloadDialog != null)
            mDownloadDialog.dismiss();
        if (callback != null) {
            callback.onFailUpdate();
        }
    }

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

        void onFinishUpdate();
    }
}
