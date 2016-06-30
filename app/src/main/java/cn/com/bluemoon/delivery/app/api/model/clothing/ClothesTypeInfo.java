package cn.com.bluemoon.delivery.app.api.model.clothing;


/**
 * 衣物类型
 * Created by lk on 2016/6/21.
 */
public class ClothesTypeInfo {
    /**
     * 衣物类型名称
     */
    private String typeName;
    /**
     * 衣物类型编号
     */
    private String typeCode;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
