package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

/**
 * {@link ResultStartCollectInfos#orderDetail}数据类
 * Created by lk on 2016/6/21.
 */
public class OrderDetail {

    /**
     * 衣物类型名称
     */
    private String typeName;
    /**
     * 应收数量
     */
    private int receivableCount;
    /**
     * 实收数量
     */
    private int actualCount;
    /**
     * 类型编号
     */
    private String typeCode;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


    public int getReceivableCount() {
        return receivableCount;
    }

    public void setReceivableCount(int receivableCount) {
        this.receivableCount = receivableCount;
    }


    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }


    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

}
