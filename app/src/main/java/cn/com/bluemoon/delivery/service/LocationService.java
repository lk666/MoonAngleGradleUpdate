package cn.com.bluemoon.delivery.service;

import java.util.List;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;

import android.content.Context;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * 
 * @author baidu
 *
 */
public class LocationService {
	private LocationClient client = null;
	private LocationClientOption mOption,DIYoption;
	private Object  objLock = new Object();
	public static final Double UNKNOW_VALUE = 4.9E-324;
	private Context mContext;
	
	public BDLocationListener myListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			double altitude = 0;
			
			if (latitude == UNKNOW_VALUE) {
				latitude = 999;
			}
			
			if (longitude == UNKNOW_VALUE) {
				longitude = 999;
			}
		
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                
            	if (altitude != UNKNOW_VALUE) {
            		altitude = location.getAltitude();
    			}
 
            } 
            ClientStateManager.setLatitude(mContext, String.valueOf(latitude));
            ClientStateManager.setLongitude(mContext, String.valueOf(longitude));
            ClientStateManager.setAltitude(mContext, String.valueOf(altitude));
            LogUtils.d("test", "Every 5 minutes, location succefully!");
			
		}
		
	};

	/***
	 * 
	 * @param locationContext
	 */
	public LocationService(Context locationContext){
		synchronized (objLock) {
			if(client == null){
				mContext = locationContext;
				client = new LocationClient(locationContext);
				client.setLocOption(getDefaultLocationClientOption());
			}
		}
	}
	
	/***
	 * 
	 * @param listener
	 * @return
	 */
	
	public boolean registerListener(){
		boolean isSuccess = false;
		if(myListener != null){
			client.registerLocationListener(myListener);
			isSuccess = true;
		}
		return  isSuccess;
	}
	
	public void unregisterListener(){
		if(myListener != null){
			client.unRegisterLocationListener(myListener);
		}
	}
	
	/***
	 * 
	 * @param option
	 * @return isSuccessSetOption
	 */
	public boolean setLocationOption(LocationClientOption option){
		boolean isSuccess = false;
		if(option != null){
			if(client.isStarted())
				client.stop();
			DIYoption = option;
			client.setLocOption(option);
		}
		return isSuccess;
	}
	
	public LocationClientOption getOption(){
		return DIYoption;
	}
	/***
	 * 
	 * @return DefaultLocationClientOption
	 */
	public LocationClientOption getDefaultLocationClientOption(){
		if(mOption == null){
			mOption = new LocationClientOption();
			mOption.setLocationMode(LocationMode.Battery_Saving);
			mOption.setCoorType("bd09ll");
			mOption.setScanSpan(5*60*1000);
			mOption.setOpenGps(false);
			/*mOption.setLocationNotify(false);
		    mOption.setIsNeedAddress(true);
		    mOption.setIsNeedLocationDescribe(true);
		    mOption.setNeedDeviceDirect(false);
		    mOption.setLocationNotify(false);
		    mOption.setIgnoreKillProcess(true);
		    mOption.setIsNeedLocationDescribe(true);
		    mOption.setIsNeedLocationPoiList(true);
		    mOption.SetIgnoreCacheException(false);
		 */
		}
		return mOption;
	}
	
	public void start(){
		synchronized (objLock) {
			if(client != null && !client.isStarted()){
				client.start();
			}
		}
	}
	public void stop(){
		synchronized (objLock) {
			if(client != null && client.isStarted()){
				client.stop();
			}
		}
	}
	
}
