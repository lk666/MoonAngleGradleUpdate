package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

/**
 * {@link ResultOuterOrderInfo#orderDetail}数据类
 * Created by lk on 2016/6/27.
 */
public class OuterOrderDetail {

    /**
     * 数量
     */
    private int count;
    /**
     * 衣物类型编号
     */
    private String typeCode;
    /**
     * 衣物类型名称
     */
    private String typeName;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
