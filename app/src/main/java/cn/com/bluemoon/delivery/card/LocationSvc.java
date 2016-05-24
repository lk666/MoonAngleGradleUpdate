package cn.com.bluemoon.delivery.card;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * @desc 定位服务
 */
public class LocationSvc extends Service implements LocationListener {

	private static final String TAG = "LocationSvc";
	private LocationManager locationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,this);
		}else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
		}else{
			send(null);
		}
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "Get the current position \n" + location);
		send(location);
	}

	private void send(Location location){
		Intent intent = new Intent();
		intent.setAction(Constants.LOCATION_ACTION);
		if(location!=null){
			intent.putExtra(Constants.LONGITUDE, location.getLongitude());
			intent.putExtra(Constants.LATITUDE, location.getLatitude());
		}else{
			intent.putExtra(Constants.LONGITUDE, Constants.UNKNOW_LONGITUDE);
			intent.putExtra(Constants.LATITUDE,  Constants.UNKNOW_LATITUDE);
		}
		sendBroadcast(intent);
		locationManager.removeUpdates(this);
		stopSelf();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
