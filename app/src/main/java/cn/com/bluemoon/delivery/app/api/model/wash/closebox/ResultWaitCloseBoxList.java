package cn.com.bluemoon.delivery.app.api.model.wash.closebox;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.ReturningApi#queryWaitCloseBoxList(long, String, boolean, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultWaitCloseBoxList extends ResultBase {
    /**
     * 数据列表
     */
    private List<BoxItem> inboxList;

    /**
     * 待封箱数量
     */
    private int waitInboxCount;

    /**
     * 待封箱总数
     */
    private int inboxSum;
    /**
     * 分页时间戳(分页标志)
     */
    private long pageFlag;

    public int getWaitInboxCount() {
        return waitInboxCount;
    }

    public void setWaitInboxCount(int waitInboxCount) {
        this.waitInboxCount = waitInboxCount;
    }

    public List<BoxItem> getInboxList() {
        return inboxList;
    }

    public void setInboxList(List<BoxItem> inboxList) {
        this.inboxList = inboxList;
    }

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
}
