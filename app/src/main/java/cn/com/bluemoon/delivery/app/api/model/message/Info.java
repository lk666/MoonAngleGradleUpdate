package cn.com.bluemoon.delivery.app.api.model.message;

/**
 * Created by allenli on 2016/5/20.
 */
public class Info {
    private String infoId;
    private String infoTitle;
    private long releaseTime;

 /*   public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }*/

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public boolean isRead;

}
