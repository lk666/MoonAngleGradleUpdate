package cn.com.bluemoon.delivery.app.api.model.wash;

/**
 * Created by bm on 2016/9/22.
 */
public class CornerNum {
    /** 角标类型 */
    private String type;
    /** 角标统计值 */
    private int typeCount;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getTypeCount() {
        return typeCount;
    }
    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }
}
