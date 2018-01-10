package cn.com.bluemoon.lib_iflytek.utils;

import android.util.Log;

public class SpeechLogUtil {

    private static final String TAG = "test";

    public static void e(String msg) {
        if (!SpeechUtil.RELEASE && msg != null) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (!SpeechUtil.RELEASE && msg != null) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (!SpeechUtil.RELEASE && msg != null) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (!SpeechUtil.RELEASE && msg != null) {
            Log.d(TAG, msg);
        }
    }

}
