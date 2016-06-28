package cn.com.bluemoon.delivery.app.api.model.team;

/**
 * Created by bm on 2016/6/23.
 */
public class ServiceArea {
    /** 单元楼所在楼座 */
    private String balcony;
    /** 单元楼/商业中心/区划编码 */
    private String bpCode;
    /** 小区编码 */
    private String bpCode1;
    /** 单元楼/商业中心/区划编码 */
    private String bpName;
    /** 小区名称 */
    private String bpName1;
    /** 城市名称 */
    private String cityName;
    /** 区县名称 */
    private String countyName;
    /** 省份名称 */
    private String provinceName;
    /** 乡镇/街道名称 */
    private String streetName;
    /** 户数 */
    private int totalFamily;
    /** 社区/村名称 */
    private String villageName;
    /** 苑/园/区名称 */
    private String yuanGarden;

    public String getBalcony() {
        return balcony;
    }
    public void setBalcony(String balcony) {
        this.balcony = balcony;
    }
    public String getBpCode() {
        return bpCode;
    }
    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }
    public String getBpCode1() {
        return bpCode1;
    }
    public void setBpCode1(String bpCode1) {
        this.bpCode1 = bpCode1;
    }
    public String getBpName() {
        return bpName;
    }
    public void setBpName(String bpName) {
        this.bpName = bpName;
    }
    public String getBpName1() {
        return bpName1;
    }
    public void setBpName1(String bpName1) {
        this.bpName1 = bpName1;
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
    public String getProvinceName() {
        return provinceName;
    }
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    public String getStreetName() {
        return streetName;
    }
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
    public int getTotalFamily() {
        return totalFamily;
    }
    public void setTotalFamily(int totalFamily) {
        this.totalFamily = totalFamily;
    }
    public String getVillageName() {
        return villageName;
    }
    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }
    public String getYuanGarden() {
        return yuanGarden;
    }
    public void setYuanGarden(String yuanGarden) {
        this.yuanGarden = yuanGarden;
    }

}
