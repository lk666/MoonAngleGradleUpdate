package cn.com.bluemoon.delivery.sz.meeting;

/**
 * Created by dujiande on 2016/8/16.
 */
public interface RequestListener {
    public void getCacheCallBack(String dataString);
    public void getHttpCallBack(String dataString);
    public void stopLoad();
}
