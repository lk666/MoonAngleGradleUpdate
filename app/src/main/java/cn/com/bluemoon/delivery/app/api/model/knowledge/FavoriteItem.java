package cn.com.bluemoon.delivery.app.api.model.knowledge;

/**
 * Created by allenli on 2016/5/20.
 */
public class FavoriteItem {
    private String paperId;
    private String paperTitle;

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    private long collectTime;

}
