package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#queryCloseBoxHistoryList(long, long, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/19.
 */
public class ResultCloseBoxHistoryList extends ResultBase {
    /**
     * 筛选封箱总数
     */
    private int inboxSum;
    /**
     * 分页时间戳(分页标志)
     */
    private long pageFlag;
    /**
     * 封箱标签历史列表
     */
    private List<TagItem> tagList;

    public int getInboxSum() {
        return inboxSum;
    }

    public void setInboxSum(int inboxSum) {
        this.inboxSum = inboxSum;
    }

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
        this.pageFlag = pageFlag;
    }

    public List<TagItem> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagItem> tagList) {
        this.tagList = tagList;
    }
}
