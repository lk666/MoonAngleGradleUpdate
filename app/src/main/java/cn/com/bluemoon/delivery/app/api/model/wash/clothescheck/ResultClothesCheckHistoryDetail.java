package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.6衣物历史清点详情服务器返回json
 * Created by lk on 2016/9/14.
 */
public class ResultClothesCheckHistoryDetail extends ResultBase {
    /**
     * 衣物明细
     */
    private List<Clothes> clothesList;

    /**
     * 异常衣物图片
     */
    private List<String> imagePathList;
    /**
     * 问题描叙
     */
    private String issueDesc;

    /**
     * 清点时间
     */
    private long opTime;

    public long getOpTime() {
        return opTime;
    }

    public void setOpTime(long opTime) {
        this.opTime = opTime;
    }

    public List<Clothes> getClothesList() {
        return clothesList;
    }

    public void setClothesList(List<Clothes> clothesList) {
        this.clothesList = clothesList;
    }

    public List<String> getImagePathList() {
        return imagePathList;
    }

    public void setImagePathList(List<String> imagePathList) {
        this.imagePathList = imagePathList;
    }

    public String getIssueDesc() {
        return issueDesc;
    }

    public void setIssueDesc(String issueDesc) {
        this.issueDesc = issueDesc;
    }

}
