package cn.com.bluemoon.lib_iflytek;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.com.bluemoon.lib_iflytek.utils.JsonParser;
import cn.com.bluemoon.lib_iflytek.interf.SayingListener;
import cn.com.bluemoon.lib_iflytek.utils.SpeechLogUtil;
import cn.com.bluemoon.lib_iflytek.utils.SpeechUtil;

/**
 * 语音听写工具类
 * Created by bm on 2017/6/15.
 */

public class SayManager {
    private final static String TAG = SayManager.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    private SayingListener listener;

    private static SayManager instance;

    public SayManager() {

    }

    public static SayManager getInstance(){
        if(instance==null){
            instance = new SayManager();
        }
        return instance;
    }

    public void setListener(SayingListener listener){
        this.listener = listener;
    }

    /**
     * 开始语音听写，调用之前必须先调用setListener，设置监听
     */
    public void startSaying(Context context){
        if(mIat==null){
            mIat = SpeechRecognizer.createRecognizer(context, new InitListener() {
                @Override
                public void onInit(int code) {
                    SpeechLogUtil.d(TAG,"init code "+code);
                    if (code == ErrorCode.SUCCESS) {
                        SpeechLogUtil.d(TAG, "voice init success");
                        setParam();
                        startSaying();
                    } else {
                        SpeechLogUtil.d(TAG, "voice init fail");
                        if (listener != null) {
                            listener.onError(null);
                        }
                    }
                }
            });
        }else{
            startSaying();
        }
    }


    /**
     * 初始化语音听写
     */
    private void initSay(Context context) {
        mIat = SpeechRecognizer.createRecognizer(context, new InitListener() {
            @Override
            public void onInit(int code) {
                SpeechLogUtil.d(TAG,"init code "+code);
                if (code == ErrorCode.SUCCESS) {
                    SpeechLogUtil.d(TAG, "voice init success");
                    setParam();
                } else {
                    SpeechLogUtil.d(TAG, "voice init fail");
                }
            }
        });
    }

    /**
     * 开始语音听写
     */
    private void startSaying() {
        if (mIat == null) {
            return;
        }
        mIatResults.clear();
        int code = mIat.startListening(mRecognizerListener);
        if (code == ErrorCode.SUCCESS) {
            setParam();
            SpeechLogUtil.d(TAG, "voice start success");
        } else {
            SpeechLogUtil.d(TAG, "voice start fail");
        }
    }

    /**
     * 停止语音听写
     */
    public void stopSaying() {
        if (null != mIat) {
            mIat.stopListening();
            SpeechLogUtil.d(TAG, "voice stop");
        }
    }

    /**
     * 取消语音听写
     */
    public void cancelSaying() {
        if (null != mIat) {
            mIat.cancel();
            SpeechLogUtil.d(TAG, "voice cancel");
        }
    }

    /**
     * 销毁语音合成
     */
    public void destroy() {
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
            mIat = null;
        }
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            LogUtil.d(TAG, "当前正在说话，音量大小：" + volume);
            if (listener != null) {
                listener.onVolumeChanged(volume/2);
            }
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            SpeechLogUtil.d(TAG, "voice start");
            if (listener != null) {
                listener.onBeginOfSpeech();
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            SpeechLogUtil.d(TAG, "voice end");
            if (listener != null) {
                listener.onEndOfSpeech();
            }
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
//            LogUtil.d(TAG, recognizerResult.getResultString());
            SpeechLogUtil.d(TAG, "isLast:" + isLast);
            printResult(recognizerResult);
            if (isLast) {
                //将所有识别到的语音合成返回
                StringBuffer resultBuffer = new StringBuffer();
                for (String key : mIatResults.keySet()) {
                    resultBuffer.append(mIatResults.get(key));
                }
                if (listener != null) {
                    listener.onResult(resultBuffer.toString());
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            SpeechLogUtil.d(TAG, "error code：" + speechError.getErrorCode() + "==" + speechError
                    .getPlainDescription(true));
            if (listener != null) {
                listener.onError(speechError);
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		SpeechLogUtil.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 设置参数
     */
    private void setParam() {
        if (mIat == null) return;

//        //网络超时时间，默认为20000（毫秒）
//        mIat.setParameter(SpeechConstant.NET_TIMEOUT,"20000");
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式  值范围：{ "json", "xml", "plain" }
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言 简体中文：zh_cn（默认） 美式英文：en_us
//        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域  普通话：mandarin(默认)粤 语：cantonese 四川话：lmz
//        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        //仅在允许VAD时，VAD_BOS, VAD_EOS才会起作用，且各监听的音量变化回调,才会有音量 检测值返回
        //默认值：1(是) 值范围：{ null, 0, 1 }
//        mIat.setParameter(SpeechConstant.VAD_ENABLE, "1");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理，
        // 默认值：听写5000，其他4000 值范围：[1000, 10000]
//        mIat.setParameter(SpeechConstant.VAD_BOS, "5000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        // 默认值：听写1800，其他700 值范围：[0, 10000]
//        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, SpeechUtil.PATH);
    }

    /**
     * 识别结果解析
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        SpeechLogUtil.d(TAG, "text:" + text);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
    }

}
