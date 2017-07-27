package cn.com.bluemoon.delivery.module.track.api;

import android.content.Context;

import com.loopj.android.http.TextHttpResponseHandler;

import java.util.UUID;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 包含context的TextHttpResponseHandler
 * Created by lk on 2016/7/29.
 */
public abstract class WithStatusTextHttpResponseHandler<T extends ResultBase> extends
        TextHttpResponseHandler {
    private Context context;
    private Class clazz;
    private int reqCode;
    private long reqStatus;
    private String uuid;

    public WithStatusTextHttpResponseHandler(String encoding, Context context, int reqCode,
                                             long reqTime, Class clazz) {
        super(encoding);
        this.context = context;
        this.clazz = clazz;
        this.reqCode = reqCode;
        this.reqStatus = reqTime;
        this.uuid = UUID.randomUUID().toString();
    }

    public Context getContext() {
        return context;
    }

    public long getReqString() {
        return reqStatus;
    }

    public int getReqCode() {
        return reqCode;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getUuid() {
        return uuid;
    }
}
