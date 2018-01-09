package cn.com.bluemoon.delivery.common.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.bluemoon.delivery.module.event.ScanEvent;
import cn.com.bluemoon.delivery.module.event.ScanResultEvent;


public class ContinuityScanActivity extends ScanActivity {

    public static void actStart(Activity context, String title, int requestCode) {
        actStart(context,null,ContinuityScanActivity.class, title, requestCode);
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        if(!TextUtils.isEmpty(str)){
            EventBus.getDefault().post(new ScanEvent(str, type));
        }
        showWaitDialog(false);
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScanResultEvent event){
        if(event.isClose){
            hideWaitDialog();
            onActionBarBtnLeftClick();
        }else{
            startDelay(500);
        }
    }
}
