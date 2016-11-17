package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/11/17.
 */
public class ResultCombo extends ResultBase {
    private List<Long> cmboArray;

    public List<Long> getComboArray() {
        return cmboArray;
    }

    public void setComboArray(List<Long> cmboArray) {
        this.cmboArray = cmboArray;
    }
}
