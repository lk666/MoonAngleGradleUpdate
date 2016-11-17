package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/11/17.
 */
public class ResultUploadImg extends ResultBase {

    private String evidencePath;

    public String getEvidencePath() {
        return evidencePath;
    }

    public void setEvidencePath(String evidencePath) {
        this.evidencePath = evidencePath;
    }
}
