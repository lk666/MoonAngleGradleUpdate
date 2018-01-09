package cn.com.bluemoon.lib_iflytek;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.cloud.SpeechError;

import cn.com.bluemoon.lib_iflytek.interf.SayingListener;
import cn.com.bluemoon.lib_iflytek.utils.SpeechUtil;
import cn.com.bluemoon.lib_iflytek.view.VoiceChangeView;
import cn.com.bluemoon.lib_iflytek.view.VoiceDecodeView;

/**
 * 语音搜索页
 * Created by bm on 2017/8/31.
 */

public abstract class BaseVoiceFragment extends Fragment implements View.OnTouchListener,
        SayingListener {

    private FrameLayout layoutCenter;
    private LinearLayout layoutBottom;
    private TextView txtTips;
    private VoiceChangeView layoutVoice;
    private ImageView imgTips;
    private VoiceDecodeView viewDecode;

    private static final int CODE_DOWN = 0;
    private static final int CODE_MOVE = 1;
    private static final int CODE_RESULT = 2;
    private static final int CODE_CANCEL = 3;
    private static final int CODE_ERROR = 4;
    private static final int CODE_SUCCESS = 5;
    private static final int CODE_FINISH = 6;

    private float maxMoveY;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_speak, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    public void initView(View contentView) {
        //显示讯飞提示
        contentView.findViewById(R.id.txt_hint).setVisibility(SpeechUtil.RELEASE ? View.GONE :
                View.VISIBLE);

        layoutCenter = (FrameLayout) contentView.findViewById(R.id.layout_center);
        layoutBottom = (LinearLayout) contentView.findViewById(R.id.layout_bottom);
        txtTips = (TextView) contentView.findViewById(R.id.txt_tips);
        layoutVoice = (VoiceChangeView) contentView.findViewById(R.id.layout_voice);
        imgTips = (ImageView) contentView.findViewById(R.id.img_tips);
        viewDecode = (VoiceDecodeView) contentView.findViewById(R.id.view_decode);

        maxMoveY = SpeechUtil.dip2px(getActivity(), 40);
        layoutBottom.setOnTouchListener(this);
        SayManager.getInstance().setListener(this);
    }

    @Override
    public void onDestroyView() {
        SayManager.getInstance().destroy();
        super.onDestroyView();
    }

    float downY = 0;
    long downTime = 0;
    boolean isSuccess;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v != layoutBottom) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (SpeechUtil.checkRecordPermission(getActivity())) {
                    downY = event.getY();
                    downTime = System.currentTimeMillis();
                    handler.sendEmptyMessage(CODE_DOWN);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (layoutCenter.getVisibility() == View.VISIBLE) {
                    boolean isCanRecord = downY - event.getY() < maxMoveY;
                    //如果从取消状态和录音状态切换的时候，发送改变
                    if (isSuccess != isCanRecord) {
                        handler.sendMessage(getMsg(CODE_MOVE, isCanRecord));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (layoutCenter.getVisibility() == View.VISIBLE) {
                    handler.sendEmptyMessage(isSuccess ? CODE_RESULT : CODE_CANCEL);
                }
                break;
        }
        return true;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDetached()) {
                return;
            }
            switch (msg.what) {
                case CODE_DOWN:
                    //显示语音动画界面
                    isSuccess = true;
                    layoutBottom.setBackgroundColor(0xffe6eaef);
                    SpeechUtil.setViewVisibility(layoutCenter, View.VISIBLE);
                    //开始录音
                    SayManager.getInstance().startSaying(getActivity());
                    break;
                case CODE_MOVE:
                    //设置是否取消语音
                    isSuccess = (Boolean) msg.obj;
                    txtTips.setText(isSuccess ? R.string.search_speak_normal : R.string
                            .search_speak_cancel);
                    imgTips.setImageResource(isSuccess ? R.mipmap.ic_voice_input : R.mipmap
                            .ic_voice_input_cancel);
                    SpeechUtil.setViewVisibility(layoutVoice, isSuccess ? View.VISIBLE : View.GONE);
                    break;
                case CODE_CANCEL:
                    // 2017/8/31 语音识别取消
                    SayManager.getInstance().cancelSaying();
                    onCancel();
                    handler.sendEmptyMessage(CODE_FINISH);
                    break;
                case CODE_RESULT:
                    //进行语音识别
                    SayManager.getInstance().stopSaying();
                    txtTips.setText(R.string.search_speak_img);
                    SpeechUtil.setViewVisibility(layoutVoice, View.GONE);
                    SpeechUtil.setViewVisibility(imgTips, View.GONE);
                    //启动识别动画
                    viewDecode.setAnim(true);
                    SpeechUtil.setViewVisibility(viewDecode, View.VISIBLE);
                    //暂时锁定按钮的点击触摸事件
                    layoutBottom.setEnabled(false);
                    break;
                case CODE_ERROR:
                    String error = (String) msg.obj;
                    if (TextUtils.isEmpty(error)) {
                        error = getString(R.string.search_speak_error);
                    }
                    //设置识别不到语音
                    txtTips.setText(error);
                    SpeechUtil.setViewVisibility(layoutVoice, View.GONE);
                    SpeechUtil.setViewVisibility(viewDecode, View.GONE);
                    SpeechUtil.setViewVisibility(imgTips, View.VISIBLE);
                    imgTips.setImageResource(R.mipmap.ic_voice_input_unfound);
                    onError(error);
                    handler.sendEmptyMessageDelayed(CODE_FINISH, 500);
                    break;
                case CODE_SUCCESS:
                    //识别成功
                    txtTips.setText(R.string.search_speak_success);
                    String text = (String) msg.obj;
                    onSuccess(text);
                    handler.sendEmptyMessageDelayed(CODE_FINISH, 200);
                    break;
                case CODE_FINISH:
                    layoutBottom.setBackgroundColor(0xfff2f4f7);
                    //隐藏语音识别的界面
                    SpeechUtil.setViewVisibility(layoutCenter, View.GONE);
                    //还原初始界面
                    SpeechUtil.setViewVisibility(layoutVoice, View.VISIBLE);
                    //停止识别动画
                    viewDecode.setAnim(false);
                    SpeechUtil.setViewVisibility(viewDecode, View.GONE);
                    SpeechUtil.setViewVisibility(imgTips, View.VISIBLE);
                    imgTips.setImageResource(R.mipmap.ic_voice_input);
                    txtTips.setText(R.string.search_speak_normal);
                    isSuccess = false;
                    //解除按钮的点击触摸事件
                    layoutBottom.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onVolumeChanged(int volume) {
        layoutVoice.setVoiceLevel(volume);
    }

    @Override
    public void onBeginOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    final public void onResult(String result) {
        // 2017/8/31  语音识别成功
        handler.sendMessageDelayed(getMsg(CODE_SUCCESS, result), 500);
    }

    @Override
    final public void onError(SpeechError error) {
        // 2017/8/31  语音识别错误
        handler.sendMessageDelayed(getMsg(CODE_ERROR, error == null ? "" : error
                .getErrorDescription()), 500);
    }

    private Message getMsg(int code, Object obj) {
        Message msg = new Message();
        msg.what = code;
        msg.obj = obj;
        return msg;
    }

    //识别结果
    protected void onCancel() {

    }

    protected void onError(String error) {

    }

    protected abstract void onSuccess(String result);

}
