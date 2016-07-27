package cn.com.bluemoon.delivery.card;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import cn.com.bluemoon.delivery.utils.Constants;

/**
 * Created by bm on 2016/4/12.
 */
public class CommonLocation {
    private Context context;
    private LocationListener listener;

    public CommonLocation(Context context){
        this.context = context;
    }

    public void setLocationListener(LocationListener listener){
        this.listener = listener;
    }

    public void start(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.LOCATION_ACTION);
        context.registerReceiver(new LocationBroadcastReceiver(), filter);

        Intent intent = new Intent();
        intent.setClass(context, LocationSvc.class);
        context.startService(intent);
    }

    public void stop(){

    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Constants.LOCATION_ACTION)) return;
            double longitude = intent.getDoubleExtra(Constants.LONGITUDE, Constants.UNKNOW_LONGITUDE);
            double latitude = intent.getDoubleExtra(Constants.LATITUDE, Constants.UNKNOW_LATITUDE);
            if(listener!=null){
                listener.onReceiveLocation(longitude,latitude);
            }
            context.unregisterReceiver(this);
        }
    }

    public interface LocationListener{
        void onReceiveLocation(double longitude,double latitude);
    }


}
