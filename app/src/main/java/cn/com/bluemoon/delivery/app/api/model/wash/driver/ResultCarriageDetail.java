package cn.com.bluemoon.delivery.app.api.model.wash.driver;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/20.
 */
public class ResultCarriageDetail extends ResultBase implements Serializable{
    /** 衣物箱列表 */
    private List<DriverBox> boxList;
    /** 应收箱数 */
    private int boxNum;

    public List<DriverBox> getBoxList() {
        return boxList;
    }
    public void setBoxList(List<DriverBox> boxList) {
        this.boxList = boxList;
    }
    public int getBoxNum() {
        return boxNum;
    }
    public void setBoxNum(int boxNum) {
        this.boxNum = boxNum;
    }
}
