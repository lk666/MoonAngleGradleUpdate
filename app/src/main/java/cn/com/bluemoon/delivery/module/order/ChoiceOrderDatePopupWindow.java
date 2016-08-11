package cn.com.bluemoon.delivery.module.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;
import cn.com.bluemoon.lib.view.CommonTimePickerDialog;


public class ChoiceOrderDatePopupWindow extends PopupWindow {

    private Context mContext;
    private String mYear;
    private String mMonth;
    private String mMon;
    private String mDay;
    private String mHour;
    private String mMinute;
    private CommonDatePickerDialog datePicker;
    private CommonTimePickerDialog timePicker;
    EditText dateChoice;
    EditText timeChoice;
    private long currentTime;
    Button okBtn;
    private OrderVo order;

    public ChoiceOrderDatePopupWindow(Context context, OrderVo orderVo,
                                      IOrderChoiceDateListener listener) {
        mContext = context;
        order = orderVo;
        Init(listener);
    }

    private void Init(final IOrderChoiceDateListener listener) {
        currentTime = getCurDateForLong();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_appointment_choice_date,
                null);

        TextView textOrderId = (TextView) view.findViewById(R.id.text_orderid);
        TextView textUserName = (TextView) view.findViewById(R.id.text_username);
        TextView textAddress = (TextView) view.findViewById(R.id.text_address);
        TextView textUserTel = (TextView) view.findViewById(R.id.text_usertel);

        textAddress.setText(String.format("%s%s", order.getRegion(),
                order.getAddress()));
        textOrderId.setText(order.getOrderId());
        textUserName.setText(order.getCustomerName());
        textUserTel.setText(order.getMobilePhone());

        LinearLayout ll_popup = (LinearLayout) view
                .findViewById(R.id.layout_appointment_date);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.bg_transparent));

        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_top_in));

        dateChoice = (EditText) view.findViewById(R.id.date_choice);
        timeChoice = (EditText) view.findViewById(R.id.time_choice);

        if (!StringUtils.isEmpty(order.getSubscribeTime())
                && order.getSubscribeTime().length() >= 16) {
            dateChoice.setText(order.getSubscribeTime().substring(0, 10));
            timeChoice.setText(order.getSubscribeTime().substring(11, 16));
            mYear = order.getSubscribeTime().substring(0, 4);
            mMon = String.valueOf(Integer.valueOf(order.getSubscribeTime()
                    .substring(5, 7)) - 1);
            mMonth = order.getSubscribeTime().substring(5, 7);
            mDay = order.getSubscribeTime().substring(8, 10);
            mHour = order.getSubscribeTime().substring(11, 13);
            mMinute = order.getSubscribeTime().substring(14, 16);
        } else {

            Calendar c = Calendar.getInstance();
            mYear = String.valueOf(c.get(Calendar.YEAR));
            mMon = String.valueOf(c.get(Calendar.MONTH));
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

            if (Integer.valueOf(mMonth) <= 9) {
                mMonth = "0" + mMonth;
                mMon = "0" + mMon;
            }
            if (Integer.valueOf(mDay) <= 9) {
                mDay = "0" + mDay;
            }

            dateChoice.setText(String.format("%s-%s-%s", mYear, mMonth, mDay));

        }
        okBtn = (Button) view.findViewById(R.id.btn_confirm);
        dateChoice.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDatePickerDialog();
                return false;
            }
        });

        timeChoice.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // datePicker = new DatePicker(getActivity());
                showTimePickerDialog();
                return false;
            }
        });

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                dismiss();
            }
        });

        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTime = getCurDateForLong();
               // String date = getDateTime();
                int resultCode = isCorrectTime();
                if (resultCode == 0) {

                    long time = Long.valueOf(getDateTime()) * 100;
                    listener.Choise(order, time, getFormartDateTime());

                    dismiss();
                } else if (resultCode == 1) {
                    PublicUtil.showToast(mContext, mContext
                            .getString(R.string.pending_order_appointment_date));
                } else if (resultCode == 2) {
                    PublicUtil.showToast(mContext, mContext
                            .getString(R.string.pending_order_appointment_time));
                } else if (resultCode == 3) {
                    PublicUtil.showToast(
                            mContext,
                            mContext.getString(R.string.pending_order_appointment_current));
                } else if (resultCode == 4) {
                    PublicUtil.showToast(mContext, mContext
                            .getString(R.string.pending_order_appointment_over));
                }

            }
        });
    }

    public void showPopwindow(View popStart) {

        // pop.showAtLocation(v, Gravity.TOP, 0, 0);

        showAsDropDown(popStart);
    }

    public long getCurDateForLong() {
        Calendar c = Calendar.getInstance();
        String mYear = String.valueOf(c.get(Calendar.YEAR));
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mMinute = String.valueOf(c.get(Calendar.MINUTE));

        if (Integer.valueOf(mMonth) <= 9) {
            mMonth = "0" + mMonth;
        }
        if (Integer.valueOf(mDay) <= 9) {
            mDay = "0" + mDay;
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd" +
                " HH:mm");
        String timeStr = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":"
                + mMinute;
        long time = 0;
        try {
            time = sdf.parse(timeStr).getTime();
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return time;
    }

    public void setCurDate() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        if (mYear == null) {
            mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
            mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
            mMonth = String.valueOf(dateAndTime.get(Calendar.MONTH) + 1);
            mDay = String.valueOf(dateAndTime.get(Calendar.DAY_OF_MONTH));
        }
    }

    public void setCurTime() {
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        if (mHour == null) {
            mHour = String.valueOf(dateAndTime.get(Calendar.HOUR_OF_DAY));
            mMinute = String.valueOf(dateAndTime.get(Calendar.MINUTE));
        }
    }

    public void showDatePickerDialog() {
        setCurDate();
        if (datePicker == null) {
            datePicker = new CommonDatePickerDialog(mContext, mDateSetListener,
                    Integer.valueOf(mYear), Integer.valueOf(mMon),
                    Integer.valueOf(mDay));
            datePicker.show();
        } else {
            if (!datePicker.isShowing()) {
                datePicker.updateDate(Integer.valueOf(mYear),
                        Integer.valueOf(mMonth) - 1, Integer.valueOf(mDay));
                datePicker.show();
            }
        }
    }

    public void showTimePickerDialog() {
        setCurTime();
        if (timePicker == null) {
            timePicker = new CommonTimePickerDialog(mContext, mTimeSetListener,
                    Integer.valueOf(mHour), Integer.valueOf(mMinute), true);
            timePicker.show();
        } else {
            if (!timePicker.isShowing()) {
                timePicker.updateTime(Integer.valueOf(mHour),
                        Integer.valueOf(mMinute));
                timePicker.show();
            }
        }
    }

    public int isCorrectTime() {
        if ("".equals(dateChoice.getText().toString())) {
            return 1;
        }
        if ("".equals(timeChoice.getText().toString())) {
            return 2;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeStr = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":"
                + mMinute;
        long time;
        try {
            time = sdf.parse(timeStr).getTime();
            if (time <= currentTime) {
                return 3;
            } else if (time > currentTime + 15 * 24 * 60 * 60 * 1000) {
                return 4;
            }
        } catch (ParseException e) {
            // e.printStackTrace();
        }

        return 0;
    }

    public String getDateTime() {
        return mYear + mMonth + mDay + mHour + mMinute;
    }

    public String getFormartDateTime() {
        return getDate() + " " + mHour + ":" + mMinute + ":00";
    }

    public String getDate() {

        return mYear + "-" + mMonth + "-" + mDay;
    }

    private CommonDatePickerDialog.OnDateSetListener mDateSetListener = new CommonDatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = String.valueOf(year);
            mMon = String.valueOf(monthOfYear);
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
            dateChoice.setText(getDate());
        }
    };

    private CommonTimePickerDialog.OnTimeSetListener mTimeSetListener = new CommonTimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (hourOfDay <= 9) {
                mHour = "0" + hourOfDay;
            } else {
                mHour = String.valueOf(hourOfDay);
            }
            if (minute <= 9) {
                mMinute = "0" + minute;
            } else {
                mMinute = String.valueOf(minute);
            }
            timeChoice.setText(mHour + ":" + mMinute);

        }
    };
}
