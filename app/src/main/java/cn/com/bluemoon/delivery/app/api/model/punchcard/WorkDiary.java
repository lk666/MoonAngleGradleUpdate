package cn.com.bluemoon.delivery.app.api.model.punchcard;

/**
 * Created by liangjiangli on 2016/4/8.
 */
public class WorkDiary {
    private int workDiaryId;
    private String diaryContent;
    private int punchCardId;

    public int getWorkDiaryId() {
        return workDiaryId;
    }

    public void setWorkDiaryId(int workDiaryId) {
        this.workDiaryId = workDiaryId;
    }

    public String getDiaryContent() {
        return diaryContent;
    }

    public void setDiaryContent(String diaryContent) {
        this.diaryContent = diaryContent;
    }

    public int getPunchCardId() {
        return punchCardId;
    }

    public void setPunchCardId(int punchCardId) {
        this.punchCardId = punchCardId;
    }
}
