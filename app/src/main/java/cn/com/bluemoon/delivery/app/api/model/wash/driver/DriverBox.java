package cn.com.bluemoon.delivery.app.api.model.wash.driver;

import java.io.Serializable;

/**
 * Created by bm on 2016/9/20.
 */
public class DriverBox implements Serializable{
    /** 衣物箱号 */
    private String boxCode;
    /** 收衣点 */
    private int centerNum;

    public boolean isCheck;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public int getCenterNum() {
        return centerNum;
    }

    public void setCenterNum(int centerNum) {
        this.centerNum = centerNum;
    }
}
