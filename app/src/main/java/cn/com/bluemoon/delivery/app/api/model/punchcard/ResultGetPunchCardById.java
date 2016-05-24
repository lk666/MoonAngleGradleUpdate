package cn.com.bluemoon.delivery.app.api.model.punchcard;

import java.util.List;

/**
 * Created by liangjiangli on 2016/4/8.
 */
public class ResultGetPunchCardById extends ResultShowPunchCardDetail{
    private List<WorkDaily> workDailyList;
    private List<ImageBean> imgList;
    private WorkDiary workDiary;

    public List<WorkDaily> getWorkDailyList() {
        return workDailyList;
    }

    public void setWorkDailyList(List<WorkDaily> workDailyList) {
        this.workDailyList = workDailyList;
    }

    public List<ImageBean> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImageBean> imgList) {
        this.imgList = imgList;
    }

    public WorkDiary getWorkDiary() {
        return workDiary;
    }

    public void setWorkDiary(WorkDiary workDiary) {
        this.workDiary = workDiary;
    }
}
