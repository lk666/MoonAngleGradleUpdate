package cn.com.bluemoon.delivery.app.api.model.card;

/**
 * Created by allenli on 2017/2/21.
 */
public class PunchParam {
    private double longitude;
    private double latitude;
    private double altitude;
    private String token;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
