package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#queryCloseBoxList(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/22.
 */
public class ResultCloseBoxList extends ResultBase {

    /**
     * 封箱标签列表
     */
    private List<CloseBoxTag> tagList;

    public List<CloseBoxTag> getTagList() {
        return tagList;
    }

    public void setTagList(List<CloseBoxTag> tagList) {
        this.tagList = tagList;
    }
}
