package cn.com.bluemoon.delivery.app.api.model.wash.driver;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/20.
 */
public class ResultCarriageHistoryDetail extends ResultBase{
    /** 箱子数量 */
    private int boxNum;
    /** 封箱标签列表 */
    private List<TagBoxHistory> tagList;

    public int getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(int boxNum) {
        this.boxNum = boxNum;
    }

    public List<TagBoxHistory> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagBoxHistory> tagList) {
        this.tagList = tagList;
    }
}
