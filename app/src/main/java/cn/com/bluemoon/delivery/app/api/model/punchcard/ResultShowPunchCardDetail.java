package cn.com.bluemoon.delivery.app.api.model.punchcard;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.WorkTask;

/**
 * Created by liangjiangli on 2016/3/30.
 */
public class ResultShowPunchCardDetail extends ResultBase {
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
