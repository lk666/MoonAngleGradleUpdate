package cn.com.bluemoon.delivery.app.api.model.card;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/3/29.
 */
public class ResultPunchCardList extends ResultBase{
    private int totalCount;
    private List<PunchCard> punchCardList;

    public List<PunchCard> getPunchCardList() {
        return punchCardList;
    }

    public void setPunchCardList(List<PunchCard> punchCardList) {
        this.punchCardList = punchCardList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
