package cn.com.bluemoon.delivery.app.api.model.card;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/3/29.
 */
public class ResultCheckScanCode extends ResultBase {

    private PunchCard punchCard;
    private List<WorkTask> workTaskList;

    public List<WorkTask> getWorkTaskList() {
        return workTaskList;
    }

    public void setWorkTaskList(List<WorkTask> workTaskList) {
        this.workTaskList = workTaskList;
    }

    public PunchCard getPunchCard() {
        return punchCard;
    }

    public void setPunchCard(PunchCard punchCard) {
        this.punchCard = punchCard;
    }
}
