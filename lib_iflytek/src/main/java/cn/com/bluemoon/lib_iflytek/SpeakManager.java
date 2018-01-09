package cn.com.bluemoon.lib_iflytek;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import cn.com.bluemoon.lib_iflytek.utils.SpeechLogUtil;
import cn.com.bluemoon.lib_iflytek.utils.SpeechUtil;

/**
 * 语音播放工具类
 * Created by bm on 2017/10/24.
 */

public class SpeakManager {
    private final static String TAG = SpeakManager.class.getSimpleName();

    private static final SpeakManager ourInstance = new SpeakManager();

    public static SpeakManager getInstance() {
        return ourInstance;
    }

    private SpeakManager() {
    }

    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    //文本内容
    private String text;
    private Context context;

    /**
     * 初始化语音合成
     *
     * @param context
     */
    public void startSpeaking(Context context, final String text) {
        if (context == null || TextUtils.isEmpty(text)) return;
        this.context = context;
        this.text = text;
        if(mTts==null){
            mTts = SpeechSynthesizer.createSynthesizer(context, new InitListener() {
                @Override
                public void onInit(int code) {
                    SpeechLogUtil.d(TAG, "init code = " + code);
                    if (code == ErrorCode.SUCCESS) {
                        setParam();
                        startSpeaking(text);
                    }

                }
            });
        }else{
            startSpeaking(text);
        }
    }

    /**
     * 初始化语音合成
     *
     * @param context
     */
    private void initSpeaking(Context context) {
        this.context = context;
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        setParam();
    }

    /**
     * 开始语音合成
     *
     * @param text
     */
    private void startSpeaking(String text) {
        if (TextUtils.isEmpty(text) || mTts == null) return;
        this.text = text;
        int code2 = mTts.startSpeaking(text, mTtsListener);
        if (code2 != ErrorCode.SUCCESS) {
            SpeechLogUtil.e(TAG, "play error,code is" + code2);
        }
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            SpeechLogUtil.d(TAG, "InitListener init() code = " + code);
            if (code == ErrorCode.SUCCESS) {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            SpeechLogUtil.d(TAG, "play start..");
        }

        @Override
        public void onSpeakPaused() {
            SpeechLogUtil.d(TAG, "play pause..");
        }

        @Override
        public void onSpeakResumed() {
            SpeechLogUtil.d(TAG, "play resume");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
            SpeechLogUtil.d(TAG, String.format(context.getString(R.string.speak_progress), String
                    .valueOf(mPercentForBuffering), String.valueOf(mPercentForPlaying)));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
            SpeechLogUtil.d(TAG, String.format(context.getString(R.string.speak_progress), String
                    .valueOf(mPercentForBuffering), String.valueOf(mPercentForPlaying)));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                SpeechLogUtil.d(TAG, context.getString(R.string.speak_finish));
            } else {
                SpeechLogUtil.d(TAG, error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 设置参数
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, SpeechUtil.PATH + "/tts.wav");
    }

    /**
     * 销毁语音合成
     */
    public void destroy() {
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
            mTts = null;
        }
    }

    /**
     * 停止语音合成
     */
    public void stopSpeaking() {
        if (null != mTts) {
            mTts.stopSpeaking();
        }
    }

    /**
     * 暂停语音合成
     */
    public void pauseSpeaking() {
        if (null != mTts) {
            mTts.pauseSpeaking();
        }
    }

    /**
     * 恢复语音合成
     */
    public void resumeSpeaking() {
        if (null != mTts) {
            mTts.resumeSpeaking();
        }
    }
}
