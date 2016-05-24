package cn.com.bluemoon.delivery.app.api.model.punchcard;

import android.graphics.Bitmap;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2016/4/11.
 */
public class ResultImage extends ResultBase{
    private List<ImageBean> imgList;

    public List<ImageBean> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImageBean> imgList) {
        this.imgList = imgList;
    }
}
