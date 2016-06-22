package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

/**
 * Created by allenli on 2016/6/22.
 */
public class ActivityInfo {
  private  String  activityCode	;
    private  String   activityName	;
    private  String   activitySName	;
    private  String    address	;
    private  String   city	;
    private  String  county	;
    private  String   province	;

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivitySName() {
        return activitySName;
    }

    public void setActivitySName(String activitySName) {
        this.activitySName = activitySName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    private  String  street	;
    private  String   village;
}
