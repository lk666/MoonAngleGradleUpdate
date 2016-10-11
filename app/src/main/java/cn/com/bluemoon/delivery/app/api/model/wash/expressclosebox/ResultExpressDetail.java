package cn.com.bluemoon.delivery.app.api.model.wash.expressclosebox;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/29.
 */
public class ResultExpressDetail extends ResultBase{
    private List<String> backOrderCodeList;

    public List<String> getBackOrderCodeList() {
        return backOrderCodeList;
    }

    public void setBackOrderCodeList(List<String> backOrderCodeList) {
        this.backOrderCodeList = backOrderCodeList;
    }
}
