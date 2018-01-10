package cn.com.bluemoon.lib_iflytek.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iflytek.cloud.SpeechError;

import cn.com.bluemoon.lib_iflytek.R;
import cn.com.bluemoon.lib_iflytek.SayManager;
import cn.com.bluemoon.lib_iflytek.interf.VoiceListener;
import cn.com.bluemoon.lib_iflytek.interf.SayingListener;

/**
 * 语音听写界面
 * Created by bm on 2017/10/20.
 */

public class VoiceView extends FrameLayout implements View.OnClickListener, SayingListener {

    //调起界面的动画时间
    public static final int ANIM_TIME = 400;
    private View viewEmpty;
    private TextView txtTips;
    private TextView txtCancel;
    private TextView txtEnd;
    private VoiceListener listener;
    private boolean isDismiss = true;

    public VoiceView(Context context) {
        super(context);
        init();
    }

    public VoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        viewEmpty = findViewById(R.id.view_empty);
        txtTips = (TextView) findViewById(R.id.txt_tips);
        txtCancel = (TextView) findViewById(R.id.txt_cancel);
        txtEnd = (TextView) findViewById(R.id.txt_end);
        viewEmpty.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtEnd.setOnClickListener(this);

        initAnimView();

        SayManager.getInstance().setListener(this);
    }

    public VoiceView setListener(VoiceListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 展示界面
     */
    public void show() {
        show(true);
    }

    private void show(boolean isAnim) {
//        if(sayManager==null){
//            sayManager = new SayManager(getContext(), this);
//        }
        if (!isDismiss) {
            return;
        }
        isDismiss = false;
        this.setVisibility(View.VISIBLE);
        if (isAnim) {
            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
            );
            animation.setDuration(ANIM_TIME);
            this.startAnimation(animation);
        }
        startAnim();
        SayManager.getInstance().startSaying(getContext());
        setTips(R.string.voice_start);
    }

    /**
     * 关闭界面
     */
    public void dismiss() {
        dismiss(true);
    }

    private void dismiss(boolean isAnim) {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        stopAnim();
        if (isAnim) {
            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f
            );
            animation.setDuration(ANIM_TIME);
            this.startAnimation(animation);
        }
        this.setVisibility(View.GONE);
    }

    /**
     * 关闭界面并返回结果
     */
    public void dismissWithResult(final boolean isSuccess, final String result) {
        dismiss();
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onResult(isSuccess, result);
                }
            }
        }, ANIM_TIME);
    }


    /**
     * 取消录音
     */
    public void cancel() {
        SayManager.getInstance().cancelSaying();
        dismissWithResult(false, "");
    }

    /**
     * 停止录音，开始识别
     */
    public void stopSaying() {
        SayManager.getInstance().stopSaying();
        setTips(R.string.voice_decode);
    }

    /**
     * 展示提示信息
     */
    public void setTips(int resId) {
        txtTips.setText(resId);
    }

    public void setTips(String tips) {
        txtTips.setText(tips);
    }

    @Override
    public void onClick(View v) {

        if (v == viewEmpty || v == txtCancel) {
            cancel();
        } else if (v == txtEnd) {
            stopSaying();
        }
    }

    @Override
    public void onVolumeChanged(int volume) {

    }

    @Override
    public void onBeginOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        setTips(R.string.voice_decode);
    }

    @Override
    public void onResult(String result) {
        dismissWithResult(true, result);
    }

    @Override
    public void onError(SpeechError error) {
        dismissWithResult(false, "");
    }

    protected int getLayoutId() {
        return R.layout.layout_voice;
    }

    private View viewCircle;

    protected void initAnimView() {
        viewCircle = findViewById(R.id.view_circle);
    }

    protected void startAnim(){
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.39f, 1, 1.39f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatCount(-1);
        scaleAnimation.setRepeatMode(ValueAnimator.REVERSE);
//        scaleAnimation.setInterpolator(new LinearOutSlowInInterpolator());
        viewCircle.startAnimation(scaleAnimation);
    }

    protected void stopAnim(){
        viewCircle.clearAnimation();
    }
}
