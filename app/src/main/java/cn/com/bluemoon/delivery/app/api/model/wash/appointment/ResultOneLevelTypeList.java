package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 7.3至尊洗衣一级分类列表
 */
public class ResultOneLevelTypeList extends ResultBase {

    /**
     * 一级分类列表
     */
    private List<OneLevel> oneLevelList;

    public List<OneLevel> getOneLevelList() {
        return oneLevelList;
    }

    public void setOneLevelList(List<OneLevel> oneLevelList) {
        this.oneLevelList = oneLevelList;
    }
}

