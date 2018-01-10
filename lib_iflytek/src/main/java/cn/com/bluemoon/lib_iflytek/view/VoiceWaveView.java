package cn.com.bluemoon.lib_iflytek.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.com.bluemoon.lib_iflytek.R;

/**
 * 语音听写界面
 * Created by bm on 2017/10/20.
 */

public class VoiceWaveView extends VoiceView {

    public VoiceWaveView(Context context) {
        super(context);
    }

    public VoiceWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected int getLayoutId() {
        return R.layout.layout_voice_wave;
    }

    private WaveView viewAnim;

    protected void initAnimView() {
        viewAnim = (WaveView) findViewById(R.id.wave_view);
        viewAnim.setInitialRadius(60);
        viewAnim.setColor(0x6679d4ff);
    }

    protected void startAnim(){
        viewAnim.start();
    }

    protected void stopAnim(){
        viewAnim.stop();
    }

}
