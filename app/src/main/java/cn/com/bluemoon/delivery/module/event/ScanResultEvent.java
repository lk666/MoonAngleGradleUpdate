package cn.com.bluemoon.delivery.module.event;

/**
 * Created by bm on 2017/6/20.
 */

public class ScanResultEvent {
    public boolean isSuccess;
    public boolean isClose;

    public ScanResultEvent(boolean isSuccess, boolean isClose){
        this.isClose = isClose;
        this.isSuccess = isSuccess;
    }
}
