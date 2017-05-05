package cn.com.bluemoon.delivery.ui.selectordialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;

/**
 * todo 抽象、封装
 * 时分选择控件
 * Created by lk on 2016/9/4.
 */
public class TimeSelectDialog extends Dialog {
    @Bind(R.id.btn_cancel)
    View btnCancle;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.btn_ok)
    View btnOk;
    @Bind(R.id.nwv_hour)
    ExtraTextNumberWheelView nwvHour;
    @Bind(R.id.nwv_minute)
    ExtraTextNumberWheelView nwvMinute;

    private OnButtonClickListener listener;
    private String title;

    private Calendar defaultTime;
    private Calendar curTime;

    /**
     * 构造函数
     *
     * @param title    标题
     * @param defTime  默认日期(确保正确，不做检查)
     * @param listener 点击按钮时的回调
     */
    public TimeSelectDialog(Context context, String title, long defTime, OnButtonClickListener
            listener) {
        super(context, R.style.Dialog);
        initTime(defTime);
        this.title = title;
        this.listener = listener;

        initView();
    }

    /**
     * @param defTime 默认日期(确保正确，不做检查)
     */
    private void initTime(long defTime) {
        if (0 > defTime) {
            throw new IllegalArgumentException();
        }

        this.defaultTime = Calendar.getInstance();
        // 解决系统bug，当前日期为30或31时2月可能显示30、31号
        this.defaultTime.clear();
        this.defaultTime.setTimeInMillis(defTime);

        this.curTime = Calendar.getInstance();
        this.curTime.clear();
        this.curTime.setTimeInMillis(defTime);
    }

    private void initView() {
        setContentView(R.layout.dialog_time_select);

        // 保证全屏宽，因为默认宽高为WRAP_CONTENT
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);

        getWindow().setWindowAnimations(R.style.DialogAnimation);

        ButterKnife.bind(this);

        tvTitle.setText(title);
        initSelectView();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_ok, R.id.rl_main})
    public void onClick(View view) {
        switch (view.getId()) {
            // 取消
            case R.id.btn_cancel:
                if (listener != null) {
                    dismiss();
                    listener.onCancleButtonClick();
                }
                break;
            case R.id.btn_ok:
                if (listener != null) {
                    dismiss();
                    listener.onOKButtonClick(curTime.getTimeInMillis());
                }
                break;
            case R.id.rl_main:
                this.dismiss();
                break;
        }
    }

    /**
     * 初始化选择列表控件
     */
    private void initSelectView() {
        // 时 todo 开始时间、结束时间的动态控制
        nwvHour.initData(0, 23, curTime.get(Calendar.HOUR_OF_DAY));
        nwvHour.setOnSelectListener(
                new OnSelectChangedListener() {
                    @Override
                    public void onEndSelected(int index, Object hour) {
                        curTime.set(Calendar.HOUR_OF_DAY, (int) hour);
                    }
                });

        // 分 todo 开始时间、结束时间的动态控制
        nwvMinute.initData(0, 59, curTime.get(Calendar.MINUTE));
        nwvMinute.setOnSelectListener(
                new OnSelectChangedListener() {
                    @Override
                    public void onEndSelected(int index, Object minute) {
                        curTime.set(Calendar.MINUTE, (int) minute);
                    }
                });
    }
}
