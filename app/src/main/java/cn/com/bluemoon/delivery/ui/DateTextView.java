package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;

/**
 * Created by bm on 2016/7/4.
 */
public class DateTextView extends TextView {

    private CommonDatePickerDialog datePicker;
    private DateTextViewCallBack callBack;
    private Calendar calendar;
    private String format = "yyyy-MM-dd";

    public DateTextView(Context context) {
        super(context);
        init();
    }

    public DateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        updateDatePicker();
        setOnClickListener(listener);
    }

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showDatePickerDialog();
        }
    };

    public void setDate(int year, int month, int day) {
        if (calendar == null || year <= 0 || month < 0 || day <= 0) {
            calendar = Calendar.getInstance(Locale.CHINA);
        } else {
            calendar.set(year, month, day);
        }
    }

    public void setDate(Date date) {
        if (calendar == null) {
            calendar = Calendar.getInstance(Locale.CHINA);
        }
        calendar.setTime(date);
    }

    private void updateDatePicker(){
        setDate(0,-1,0);
        datePicker = new CommonDatePickerDialog(getContext(), mDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public long getDate(){
        return calendar.getTimeInMillis();
    }

    public void updateMinDate(long minDate) {
        if(datePicker.getDatePicker().getMinDate()==0){
            datePicker.getDatePicker().setMinDate(minDate);
        }else if(minDate!=datePicker.getDatePicker().getMinDate()){
            updateDatePicker();
            datePicker.getDatePicker().setMinDate(minDate);
        }
    }

    public void updateMaxDate(long maxDate) {
        if(datePicker.getDatePicker().getMinDate()== 0) {
            datePicker.getDatePicker().setMaxDate(maxDate);
        }else if(maxDate!=datePicker.getDatePicker().getMaxDate()){
            updateDatePicker();
            datePicker.getDatePicker().setMaxDate(maxDate);
        }
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setCallBack(DateTextViewCallBack callBack) {
        this.callBack = callBack;
    }

    public CommonDatePickerDialog getDatePicker(){
        return datePicker;
    }

    public void showDatePickerDialog() {
        Date date = DateUtil.strToDate(format, getText().toString());
        setDate(date);
        if (calendar.get(Calendar.YEAR) > 2050) {
            calendar = Calendar.getInstance(Locale.CHINA);
        }
        if(!datePicker.isShowing()){
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
        }
        datePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        datePicker.show();

    }

    CommonDatePickerDialog.OnDateSetListener mDateSetListener = new CommonDatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            setDate(year, monthOfYear, dayOfMonth);
            setText(DateUtil.getTime(calendar.getTimeInMillis(), format));
            if (callBack != null) {
                callBack.onDate(DateTextView.this, calendar.getTimeInMillis());
            }
        }
    };

    public interface DateTextViewCallBack {
        void onDate(View view, long date);
    }

}
