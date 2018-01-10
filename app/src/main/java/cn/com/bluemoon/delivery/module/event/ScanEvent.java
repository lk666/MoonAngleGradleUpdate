package cn.com.bluemoon.delivery.module.event;

/**
 * Created by bm on 2017/6/14.
 */

public class ScanEvent {
    public String code;
    public String type;

    public ScanEvent(String code, String type){
        this.code = code;
        this.type = type;
    }
}
