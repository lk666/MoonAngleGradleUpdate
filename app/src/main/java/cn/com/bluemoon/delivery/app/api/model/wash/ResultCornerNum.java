package cn.com.bluemoon.delivery.app.api.model.wash;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#queryCornerNum(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultCornerNum extends ResultBase {
    /** 角标统计数量列表 */
    private List<CornerNum> modelCountList;

    public List<CornerNum> getModelCountList() {
        return modelCountList;
    }

    public void setModelCountList(List<CornerNum> modelCountList) {
        this.modelCountList = modelCountList;
    }
}
