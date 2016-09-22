package cn.com.bluemoon.delivery.app.api.model.wash;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.Serializable;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#uploadExceptionImage(byte[], String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultUploadExceptionImage implements Serializable {

    /**
     * 文件路径
     */
    private String imgPath;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

}
