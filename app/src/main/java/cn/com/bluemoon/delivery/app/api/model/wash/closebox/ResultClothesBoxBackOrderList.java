package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#queryClothesBoxBackOrderList(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultClothesBoxBackOrderList extends ResultBase {
    /**
     * 还衣单号列表
     */
    private List<String> backOrderList;

    public List<String> getBackOrderList() {
        return backOrderList;
    }

    public void setBackOrderList(List<String> backOrderList) {
        this.backOrderList = backOrderList;
    }

}
