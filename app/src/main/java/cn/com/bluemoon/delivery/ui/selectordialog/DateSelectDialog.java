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
 * 日期控件
 * Created by lk on 2016/9/4.
 */
public class DateSelectDialog extends Dialog {
    @Bind(R.id.btn_cancel)
    View btnCancle;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.btn_ok)
    View btnOk;
    @Bind(R.id.nwv_year)
    ExtraTextNumberWheelView nwvYear;
    @Bind(R.id.nwv_month)
    ExtraTextNumberWheelView nwvMonth;
    @Bind(R.id.nwv_date)
    ExtraTextNumberWheelView nwvDate;

    private OnButtonClickListener listener;
    private String title;

    private Calendar starTime;
    private Calendar endTime;
    private Calendar defaultTime;
    private Calendar curTime;

    /**
     * 构造函数
     *
     * @param title     标题
     * @param startTime 开始时间(确保正确，不做检查)
     * @param endTime   结束时间(确保正确，不做检查)
     * @param defTime   默认日期(确保正确，不做检查)
     * @param listener  点击按钮时的回调
     */
    public DateSelectDialog(Context context, String title, long startTime, long endTime,
                            long defTime, OnButtonClickListener listener) {
        super(context, R.style.Dialog);
        initDate(startTime, endTime, defTime);
        this.title = title;
        this.listener = listener;

        initView();
    }

    /**
     * @param startTime 开始时间(确保正确，不做检查)
     * @param endTime   结束时间(确保正确，不做检查)
     * @param defTime   默认日期(确保正确，不做检查)
     */
    private void initDate(long startTime, long endTime, long defTime) {
        if (0 > startTime || startTime > endTime || startTime > defTime || defTime > endTime) {
            throw new IllegalArgumentException();
        }

        this.starTime = Calendar.getInstance();
        // 解决系统bug，当前日期为30或31时2月可能显示30、31号
        this.starTime.clear();
        this.starTime.setTimeInMillis(startTime);

        this.endTime = Calendar.getInstance();
        this.endTime.clear();
        this.endTime.setTimeInMillis(endTime);

        this.defaultTime = Calendar.getInstance();
        this.defaultTime.clear();
        this.defaultTime.setTimeInMillis(defTime);

        curTime = Calendar.getInstance();
        this.curTime.clear();
        this.curTime.setTimeInMillis(defTime);
    }

    private void initView() {
        setContentView(R.layout.dialog_date_select);

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
        // 年
        int start = starTime.get(Calendar.YEAR);
        nwvYear.initData(start, endTime.get(Calendar.YEAR), curTime.get(Calendar.YEAR) - start);
        nwvYear.setOnSelectListener(
                new OnSelectChangedListener() {
                    @Override
                    public void onEndSelected(int index, Object year) {
                        int month = curTime.get(Calendar.MONTH);
                        int date = curTime.get(Calendar.DAY_OF_MONTH);
                        int dayCount = curTime.getActualMaximum(Calendar.DAY_OF_MONTH);

                        curTime.clear();
                        curTime.set(Calendar.YEAR, (int) year);
                        curTime.set(Calendar.MONTH, month);

                        int curDayCount = curTime.getActualMaximum(Calendar.DAY_OF_MONTH);
                        if (date > curDayCount) {
                            date = curDayCount;
                        }
                        curTime.set(Calendar.DAY_OF_MONTH, date);

                        // 月份天数不同时修改日期
                        if (curDayCount != dayCount) {
                            nwvDate.resetData(1, curDayCount, date - 1);
                        }
                    }
                }
        );

        // 月  todo 开始时间、结束时间的动态控制
        nwvMonth.initData(1, 12, curTime.get(Calendar.MONTH));
        nwvMonth.setOnSelectListener(
                new OnSelectChangedListener() {
                    @Override
                    public void onEndSelected(int index, Object month) {
                        int year = curTime.get(Calendar.YEAR);
                        int date = curTime.get(Calendar.DAY_OF_MONTH);
                        int dayCount = curTime.getActualMaximum(Calendar.DAY_OF_MONTH);

                        curTime.clear();
                        curTime.set(Calendar.YEAR, year);
                        curTime.set(Calendar.MONTH, (int) month - 1);

                        int curDayCount = curTime.getActualMaximum(Calendar.DAY_OF_MONTH);
                        if (date > curDayCount) {
                            date = curDayCount;
                        }
                        curTime.set(Calendar.DAY_OF_MONTH, date);

                        // 月份天数不同时修改日期
                        if (curDayCount != dayCount) {
                            nwvDate.resetData(1, curDayCount, date - 1);
                        }
                    }
                }
        );

        // 日 todo 开始时间、结束时间的动态控制
        nwvDate.initData(1, curTime.getActualMaximum(Calendar.DAY_OF_MONTH), curTime.get
                (Calendar.DAY_OF_MONTH) - 1);
        nwvDate.setOnSelectListener(
                new OnSelectChangedListener() {
                    @Override
                    public void onEndSelected(int index, Object date) {
                        int year = curTime.get(Calendar.YEAR);
                        int month = curTime.get(Calendar.MONTH);
                        curTime.clear();
                        curTime.set(Calendar.YEAR, year);
                        curTime.set(Calendar.MONTH, month);
                        curTime.set(Calendar.DAY_OF_MONTH, (int) date);
                    }
                });
    }

}
