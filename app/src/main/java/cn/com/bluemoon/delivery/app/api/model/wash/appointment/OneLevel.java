package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import java.io.Serializable;

/**
 * 衣物登记一级分类
 */
public class OneLevel implements Serializable {
    /**
     * 一级分类名称
     */
    private String oneLevelName;

    /**
     * 一级分类编码
     */
    private String oneLevelCode;

    public String getOneLevelCode() {
        return oneLevelCode;
    }

    public void setOneLevelCode(String oneLevelCode) {
        this.oneLevelCode = oneLevelCode;
    }

    public String getOneLevelName() {
        return oneLevelName;
    }

    public void setOneLevelName(String oneLevelName) {
        this.oneLevelName = oneLevelName;
    }
}
