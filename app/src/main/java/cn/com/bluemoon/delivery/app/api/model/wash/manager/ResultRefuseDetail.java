package cn.com.bluemoon.delivery.app.api.model.wash.manager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/22.
 */
public class ResultRefuseDetail extends ResultBase {

    private String refuseIssueDesc;
    private long refuseTagTime;
    private List<String> imagePaths;

    public String getRefuseIssueDesc() {
        return refuseIssueDesc;
    }

    public void setRefuseIssueDesc(String refuseIssueDesc) {
        this.refuseIssueDesc = refuseIssueDesc;
    }

    public long getRefuseTagTime() {
        return refuseTagTime;
    }

    public void setRefuseTagTime(long refuseTagTime) {
        this.refuseTagTime = refuseTagTime;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
