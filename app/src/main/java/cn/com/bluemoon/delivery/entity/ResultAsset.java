package cn.com.bluemoon.delivery.entity;

import bluemoon.com.lib_x5.bean.BaseParam;
import cn.com.bluemoon.cardocr.lib.bean.AssetTagInfo;

/**
 * Created by bm on 2018/3/15.
 */

public class ResultAsset extends BaseParam {
    public AssetTagInfo data;

    public ResultAsset(AssetTagInfo data){
        this.data = data;
        setResult(true);
    }
}
