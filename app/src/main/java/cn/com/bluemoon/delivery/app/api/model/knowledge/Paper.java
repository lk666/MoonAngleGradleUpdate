package cn.com.bluemoon.delivery.app.api.model.knowledge;

/**
 * Created by allenli on 2016/5/20.
 */
public class Paper {
    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    private String paperId;
    private String   paperTitle;
}
