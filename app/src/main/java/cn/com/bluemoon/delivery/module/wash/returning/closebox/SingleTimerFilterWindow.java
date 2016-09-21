package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;

/**
 * 单项时间过滤弹窗
 */
public class SingleTimerFilterWindow extends PopupWindow {

    TextView tvTimeSelect;

    private Calendar curTime;
    private Button btnConfirm;
    private Context contextt;
    private FilterListener listener;

    private CommonDatePickerDialog datePicker;

    public SingleTimerFilterWindow(Context context, FilterListener listener) {
        this.contextt = context;
        this.listener = listener;

        curTime = Calendar.getInstance();
        curTime.clear();
        curTime.setTimeInMillis(0);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(contextt);
        View view = inflater.inflate(R.layout.dialog_single_time_filter, null);

        LinearLayout llMain = (LinearLayout) view.findViewById(R.id.ll_main);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);

        setBackgroundDrawable(new ColorDrawable(contextt.getResources().getColor(R.color
                .transparent_65)));

        llMain.startAnimation(AnimationUtils.loadAnimation(contextt, R.anim.push_top_in));

        btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btnReset = (Button) view.findViewById(R.id.btn_reset);
        tvTimeSelect = (TextView) view.findViewById(R.id.tv_time_select);
        setCurTime(curTime);
        tvTimeSelect.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDatePickerDialog();
                return false;
            }
        });


        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                curTime = Calendar.getInstance();
                curTime.clear();
                curTime.setTimeInMillis(0);
                setCurTime(curTime);
            }
        });

        btnConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.onOkClick(curTime.getTimeInMillis());
                dismiss();
            }
        });
    }

    public void showPopwindow(View popStart) {
        showAsDropDown(popStart);
    }

    public interface FilterListener {
        void onOkClick(long selectedTime);
    }

    public void showDatePickerDialog() {
        long cur = curTime.getTimeInMillis();
        Calendar c = curTime;
        if (cur == 0) {
            c = Calendar.getInstance(Locale.CHINA);
        }

        if (datePicker == null) {
            datePicker = new CommonDatePickerDialog(contextt, mDateSetListener,
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setDescendantFocusability(DatePicker
                    .FOCUS_BLOCK_DESCENDANTS);
            datePicker.show();
        }

        if (!datePicker.isShowing()) {
            datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setDescendantFocusability(DatePicker
                    .FOCUS_BLOCK_DESCENDANTS);
            datePicker.show();
        }
    }

    private CommonDatePickerDialog.OnDateSetListener mDateSetListener = new
            CommonDatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int y, int m, int d) {
                    curTime.clear();
                    curTime.set(Calendar.YEAR, y);
                    curTime.set(Calendar.MONTH, m);
                    curTime.set(Calendar.DAY_OF_MONTH, d);
                    setCurTime(curTime);
                }
            };

    private void setCurTime(Calendar time) {
        String txt;
        if (time.getTimeInMillis() == 0) {
            txt = contextt.getString(R.string.pending_order_date_hint);
            btnConfirm.setEnabled(false);
        } else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat
                    ("yyyy-MM-dd");
            txt = df.format(time.getTime());
            btnConfirm.setEnabled(true);
        }
        tvTimeSelect.setText(txt);
    }
}
