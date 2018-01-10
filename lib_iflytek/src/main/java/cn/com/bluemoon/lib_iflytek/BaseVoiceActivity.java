package cn.com.bluemoon.lib_iflytek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import cn.com.bluemoon.lib_iflytek.interf.VoiceListener;
import cn.com.bluemoon.lib_iflytek.utils.SpeechUtil;
import cn.com.bluemoon.lib_iflytek.view.VoiceView;

/**
 * 讯飞语音识别界面的统一Activity，
 * 返回结果统一key值为“result” ，intent.putExtra("result", text);
 */
public abstract class BaseVoiceActivity extends Activity implements VoiceListener {

    private String result = "";
    protected VoiceView voiceView;

    public static void actStart(Activity aty, int requestCode, Class cls) {
        Intent intent = new Intent(aty, cls);
        aty.startActivityForResult(intent, requestCode);
    }

    public static void actStart(Fragment fragment, int requestCode, Class cls) {
        Intent intent = new Intent(fragment.getActivity(), cls);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        voiceView = (VoiceView) findViewById(getVoiceViewId());
        voiceView.setListener(this);
        initView();
        voiceView.show();

    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onResult(boolean isSuccess, String result) {
        this.result = result;
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            voiceView.dismissWithResult(false, "");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        SayManager.getInstance().destroy();
        super.onDestroy();
    }

    protected int getLayoutId(){
        return R.layout.activity_voice;
    }

    protected int getVoiceViewId(){
        return R.id.view_voice;
    }

    protected void initView(){
        SpeechUtil.initTop(this);
    }
}
