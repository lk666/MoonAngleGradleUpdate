package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#queryAreaList(String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultAreaList extends ResultBase {
    /**
     * 区域列表
     */
    private List<Region> regionList;

    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }

}
