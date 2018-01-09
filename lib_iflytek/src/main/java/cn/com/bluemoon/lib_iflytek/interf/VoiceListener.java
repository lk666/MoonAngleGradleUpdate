package cn.com.bluemoon.lib_iflytek.interf;

/**
 * 语音结果回调
 * Created by bm on 2017/10/24.
 */

public interface VoiceListener {
    void onResult(boolean isSuccess, String result);
}
