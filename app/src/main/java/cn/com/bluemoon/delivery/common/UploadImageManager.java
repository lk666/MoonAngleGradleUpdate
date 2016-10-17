package cn.com.bluemoon.delivery.common;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.wash.ResultUploadExceptionImage;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ImageInfo;
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
            if (paths.contains("000000")) {
                paths.remove("000000");
            }
            uploadApi();
        }
    }

    public void uploadApi() {
        ReturningApi.uploadImage(FileUtil.getBytes(LibImageUtil.getImgScale(paths.get(index),300, false)),
                fileName, ClientStateManager.getLoginToken(mContext), handler);
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
        abstract void uploadResult(boolean success, List<ImageInfo> imgPaths);
    }

}
