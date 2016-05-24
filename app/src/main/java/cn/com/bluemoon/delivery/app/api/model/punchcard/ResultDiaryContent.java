package cn.com.bluemoon.delivery.app.api.model.punchcard;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2016/4/11.
 */
public class ResultDiaryContent extends ResultBase{
    private String diaryContent;

    public String getDiaryContent() {
        return diaryContent;
    }

    public void setDiaryContent(String diaryContent) {
        this.diaryContent = diaryContent;
    }
}
