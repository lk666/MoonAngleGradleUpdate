package cn.com.bluemoon.delivery.app.api.model.card;

/**
 * Created by bm on 2016/3/29.
 */
public class PunchCard {
    private String punchCardType;
    private String workPlaceType;

    public String getPunchCardType() {
        return punchCardType;
    }

    public void setPunchCardType(String punchCardType) {
        this.punchCardType = punchCardType;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getPrincipalMobile() {
        return principalMobile;
    }

    public void setPrincipalMobile(String principalMobile) {
        this.principalMobile = principalMobile;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getAttendanceName() {
        return attendanceName;
    }

    public void setAttendanceName(String attendanceName) {
        this.attendanceName = attendanceName;
    }

    public String getAttendanceCode() {
        return attendanceCode;
    }

    public void setAttendanceCode(String attendanceCode) {
        this.attendanceCode = attendanceCode;
    }

    public String getWorkPlaceType() {
        return workPlaceType;
    }

    public void setWorkPlaceType(String workPlaceType) {
        this.workPlaceType = workPlaceType;
    }

    private String attendanceCode;
    private String attendanceName;
    private String principalId;
    private String principalName;
    private String principalMobile;
    private String provinceName;
    private String cityName;
    private String countyName;
    private String townName;
    private String villageName;
    private String address;
    private double longitude;
    private double latitude;
    private double altitude;
    private String punchCardId;
    private long punchInTime;
    private long punchOutTime;
    private boolean hasWorkDiary;
    private int totalBreedSalesNum;
    private int totalSalesNum;
    private int uploadImgNum;

    public boolean getHasWorkDiary() {
        return hasWorkDiary;
    }

    public void setHasWorkDiary(boolean hasWorkDiary) {
        this.hasWorkDiary = hasWorkDiary;
    }

    public int getTotalBreedSalesNum() {
        return totalBreedSalesNum;
    }

    public void setTotalBreedSalesNum(int totalBreedSalesNum) {
        this.totalBreedSalesNum = totalBreedSalesNum;
    }

    public int getTotalSalesNum() {
        return totalSalesNum;
    }

    public void setTotalSalesNum(int totalSalesNum) {
        this.totalSalesNum = totalSalesNum;
    }

    public int getUploadImgNum() {
        return uploadImgNum;
    }

    public void setUploadImgNum(int uploadImgNum) {
        this.uploadImgNum = uploadImgNum;
    }

    public long getPunchOutTime() {
        return punchOutTime;
    }

    public void setPunchOutTime(long punchOutTime) {
        this.punchOutTime = punchOutTime;
    }

    public long getPunchInTime() {
        return punchInTime;
    }

    public void setPunchInTime(long punchInTime) {
        this.punchInTime = punchInTime;
    }

    public String getPunchCardId() {
        return punchCardId;
    }

    public void setPunchCardId(String punchCardId) {
        this.punchCardId = punchCardId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
