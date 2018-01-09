package cn.com.bluemoon.lib_iflytek;

/**
 * 讯飞语音识别界面的统一Activity，
 * 返回结果统一key值为“result” ，intent.putExtra("result", text);
 */
public abstract class BaseVoiceWaveActivity extends BaseVoiceActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_voice_wave;
    }

    @Override
    protected int getVoiceViewId() {
        return R.id.view_voice_wave;
    }
}
