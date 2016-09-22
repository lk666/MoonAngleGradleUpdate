package cn.com.bluemoon.delivery.module.wash.returning.manager.model;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/22.
 */
public class ResultRefuseDetail extends ResultBase {

    private String refuseIssueDesc;
    private int refuseTagTime;
    private List<String> imagePaths;

    public String getRefuseIssueDesc() {
        return refuseIssueDesc;
    }

    public void setRefuseIssueDesc(String refuseIssueDesc) {
        this.refuseIssueDesc = refuseIssueDesc;
    }

    public int getRefuseTagTime() {
        return refuseTagTime;
    }

    public void setRefuseTagTime(int refuseTagTime) {
        this.refuseTagTime = refuseTagTime;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
