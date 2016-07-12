package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;

/**
 * Created by bm on 2016/7/4.
 */
public class DateTextView extends TextView{

    private CommonDatePickerDialog datePicker;
    private DateTextViewCallBack callBack;
    private long minDate;
    private long maxDate;

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

    private void init(){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack == null || !callBack.cancelClick(v)) {
                    showDatePickerDialog();
                }
            }
        });
    }

    public void setMinDate(long minDate){
        this.minDate = minDate;
    }

    public void setMaxDate(long maxDate){
        this.maxDate = maxDate;
    }

    public void setCallBack(DateTextViewCallBack callBack){
        this.callBack = callBack;
    }

    public void showDatePickerDialog() {
        String date = getText().toString();
        if(date.split("-").length!=3||(Integer.parseInt(date.substring(0,4))>2050)){
            date = DateUtil.getCurDate();
        }
        String[] strs = date.split("-");
        if(strs.length!=3){
            return;
        }
        if (datePicker == null) {
            datePicker = new CommonDatePickerDialog(getContext(), mDateSetListener,
                    Integer.valueOf(strs[0]), Integer.valueOf(strs[1])-1,
                    Integer.valueOf(strs[2]));
            if(minDate>0){
                datePicker.getDatePicker().setMinDate(minDate);
            }
            if(maxDate>0){
                datePicker.getDatePicker().setMaxDate(maxDate);
            }
            datePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            datePicker.show();
        } else {
            if (!datePicker.isShowing()) {
                datePicker.updateDate(Integer.valueOf(strs[0]), Integer.valueOf(strs[1])-1, Integer.valueOf(strs[2]));
                datePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                datePicker.show();
            }
        }
    }

    CommonDatePickerDialog.OnDateSetListener mDateSetListener = new CommonDatePickerDialog.OnDateSetListener() {



        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String mYear;
            String mMonth;
            String mDay;
            mYear = String.valueOf(year);
            if (monthOfYear <= 8) {
                mMonth = "0" + (monthOfYear + 1);
            } else {
                mMonth = String.valueOf(monthOfYear + 1);
            }
            if (dayOfMonth <= 9) {
                mDay = String.valueOf("0" + dayOfMonth);
            } else {
                mDay = String.valueOf(dayOfMonth);
            }
            setText(mYear+"-"+mMonth+"-"+mDay);
            if(callBack!=null){
                callBack.onDate(DateTextView.this,DateUtil.strToDate("yyyy-MM-dd",getText().toString()).getTime());
            }
        }
    };

    public interface DateTextViewCallBack{
        void onDate(View view,long date);
        boolean cancelClick(View view);
    }

}
