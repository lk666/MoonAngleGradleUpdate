package cn.com.bluemoon.delivery.app.api.model.card;

import java.io.Serializable;

/**
 * Created by bm on 2016/3/30.
 */
public class Workplace implements Serializable{
    private String workplaceCode;
    private String workplaceName;
    private String provinceName;
    private String cityName;
    private String countyName;
    private String address;
    private boolean isSelect;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getWorkplaceCode() {
        return workplaceCode;
    }

    public void setWorkplaceCode(String workplaceCode) {
        this.workplaceCode = workplaceCode;
    }

    public String getWorkplaceName() {
        return workplaceName;
    }

    public void setWorkplaceName(String workplaceName) {
        this.workplaceName = workplaceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}
