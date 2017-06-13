package cn.com.bluemoon.delivery.module.base;

import android.content.Context;

import com.loopj.android.http.TextHttpResponseHandler;

import java.util.UUID;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 包含context的TextHttpResponseHandler
 * Created by lk on 2016/7/29.
 */
public abstract class WithContextTextHttpResponseHandler<T extends ResultBase> extends
        TextHttpResponseHandler {
    private Context context;
    private Class clazz;
    private int reqCode;
    private String uuid;

    public WithContextTextHttpResponseHandler(String encoding, Context context, int requestcode,
                                              Class clazz) {
        super(encoding);
        this.context = context;
        this.clazz = clazz;
        this.reqCode = requestcode;
        this.uuid = UUID.randomUUID().toString();
    }

    public Context getContext() {
        return context;
    }

    public Class getClazz() {
        return clazz;
    }

    public int getReqCode() {
        return reqCode;
    }

    public String getUuid() {
        return uuid;
    }
}
