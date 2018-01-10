package cn.com.bluemoon.lib_iflytek.interf;

import com.iflytek.cloud.SpeechError;

/**
 * 语音听写监听类
 * Created by bm on 2017/9/5.
 */

public interface SayingListener {

    void onVolumeChanged(int volume);

    void onBeginOfSpeech();

    void onEndOfSpeech();

    void onResult(String result);

    void onError(SpeechError error);

}
