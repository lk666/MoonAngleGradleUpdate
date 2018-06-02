package cn.com.bluemoon.delivery.utils.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.wash.ResultUploadExceptionImage;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ImageInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibImageUtil;

/**
 * Created by ljl on 2016/10/15.
 */
public class UploadImageManager {
    private Context mContext;
    private List<ImageInfo> imgSuceess = new ArrayList<>();
    private List<String> paths;
    private String fileName;
    private int index = 0;
    private UploadResultListener listener;

    public UploadImageManager(Context context, List<String> imgPaths, UploadResultListener uploadResultListener) {
        this.mContext = context;
        this.paths = imgPaths;
        this.listener = uploadResultListener;
    }

    public void upload() {
        fileName = UUID.randomUUID() + ".png";
        if (paths != null && !paths.isEmpty()) {
            if (paths.contains(Constants.ICON_ADD)) {
                paths.remove(Constants.ICON_ADD);
            }
            uploadApi();
        }
    }

    public void uploadApi() {
        double size = getFileSize(paths.get(index));
        if (size == 0) {
            Toast.makeText(mContext, R.string.image_invaild_tips, Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap;
        //大于400k
        if (size > 400) {
            bitmap = LibImageUtil.getImgScale(paths.get(index),300, false);
        } else {
            bitmap = BitmapFactory.decodeFile(paths.get(index));
        }
        ReturningApi.uploadImage(FileUtil.getBytes(bitmap),
                fileName, ClientStateManager.getLoginToken(mContext), handler);
    }

    private double getFileSize(String path) {
        long s = 0;
        try {
            File f = new File(path);
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                s = fis.available();
            } else {
                // 文件管理器中已经不存在删除的图片名称，但是手机自带图片浏览器中仍然可以搜索到
                Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                media.setData(contentUri);
                AppContext.getInstance().sendBroadcast(media);
            }
        } catch (Exception e) {
        } finally {
            return (double) s / 1024;
        }
    }

    protected AsyncHttpResponseHandler handler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            index++;
            LogUtils.d("UploadImageManager", "uploadImageHandler  --> result = " + responseString);
            try {
                Object resultObj = JSON.parseObject(responseString, ResultUploadExceptionImage.class);
                if (resultObj instanceof ResultUploadExceptionImage) {
                    ResultUploadExceptionImage r = (ResultUploadExceptionImage) resultObj;
                    ImageInfo imgInfo = new ImageInfo();
                    imgInfo.setFileName(fileName);
                    imgInfo.setImagePath(r.getImgPath());
                    imgSuceess.add(imgInfo);
                    if (imgSuceess.size() < paths.size()) {
                        upload();
                    } else {
                        listener.uploadResult(true,imgSuceess);
                    }
                }
            } catch (Exception e) {
                LogUtils.e("UploadImageManager", e.getMessage());
                PublicUtil.showToastServerBusy();
                listener.uploadResult(false, imgSuceess);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e("UploadImageManager", throwable.getMessage());
            PublicUtil.showToastServerOvertime();
            listener.uploadResult(false, imgSuceess);
        }
    };

    public interface UploadResultListener {
        void uploadResult(boolean success, List<ImageInfo> imgPaths);
    }

}
