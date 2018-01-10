package cn.com.bluemoon.delivery.module.speech;

import android.app.Activity;
import android.support.v4.app.Fragment;

import cn.com.bluemoon.lib_iflytek.BaseVoiceActivity;

public class VoiceActivity extends BaseVoiceActivity {

    public static void actStart(Activity aty, int requestCode) {
        actStart(aty,requestCode,VoiceActivity.class);
    }

    public static void actStart(Fragment fragment, int requestCode) {
        actStart(fragment,requestCode,VoiceActivity.class);
    }

}
