/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/3/5
 */
package cn.com.bluemoon.delivery.order;

import java.util.Calendar;
import java.util.Locale;


import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;

public class TimerFilterWindow extends PopupWindow {

	private Context mContext;
	private String mYear;
	private String mMonth;
	private String mMon;
	private String mDay;
	private CommonDatePickerDialog startDatePicker;
	private CommonDatePickerDialog endDatePicker;
	EditText startDateChoice;
	EditText endDateChoice;
	Button okBtn;
	Button cancleBtn;
	private boolean touchStartDate = true;

	public TimerFilterWindow(Context context,
							 TimerFilterListener listener) {
		mContext = context;
		Init(listener);
	}

	private void Init(final TimerFilterListener listener) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_history_filter,null);


		LinearLayout ll_popup = (LinearLayout) view
				.findViewById(R.id.layout_history_filter);

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bg_transparent));

		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_top_in));

		startDateChoice = (EditText) view.findViewById(R.id.start_date_choice);
		endDateChoice = (EditText) view.findViewById(R.id.end_date_choice);

		okBtn = (Button) view.findViewById(R.id.btn_confirm);
		cancleBtn = (Button) view.findViewById(R.id.btn_cancle);
		startDateChoice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				touchStartDate = true;
				showDatePickerDialog();
				return false;
			}
		});

		endDateChoice.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				touchStartDate  = false;
				showDatePickerDialog();
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
		
		cancleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startDateChoice.setText("");
				endDateChoice.setText("");
			}
		});

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long startDate = Long.valueOf(getStartDateTime());
				long currentDate = getCurDate();
				long endDate = Long.valueOf(getEndDateTime());
                if (startDate == 0 && endDate > 0){
					PublicUtil.showToast(R.string.alert_message_input_start_time);
				} else if (startDate > currentDate){
					PublicUtil.showToast(R.string.alert_message_start_after_current);
				} else if (endDate > 0 && startDate > endDate) {
					PublicUtil.showToast(R.string.alert_message_start_before_end);
				} else {
					if (startDate > 0 && endDate == 0) {
						endDate = currentDate + 235959;
					}
					listener.callBack(startDate, endDate);
					dismiss();
				}
			}
		});
	}

	public void showPopwindow(View popStart) {
		showAsDropDown(popStart);
	}

	public void showDatePickerDialog() {
		setCurDate();
		if (touchStartDate && startDatePicker == null) {
			startDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener,
					Integer.valueOf(mYear), Integer.valueOf(mMon),
					Integer.valueOf(mDay));
			startDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
			startDatePicker.show();
		} else if (touchStartDate){
			if (!startDatePicker.isShowing()) {
				if (!"".equals(startDateChoice.getText().toString())) {
					String[] value = startDateChoice.getText().toString().split("-");
					startDatePicker.updateDate(Integer.valueOf(value[0]),
							Integer.valueOf(value[1]) - 1, Integer.valueOf(value[2]));
				} else {
					startDatePicker.updateDate(Integer.valueOf(mYear), Integer.valueOf(mMon), Integer.valueOf(mDay));
				}
				startDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
				startDatePicker.show();
			}
		} 
		
		if (!touchStartDate && endDatePicker == null) {
			endDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener,
					Integer.valueOf(mYear), Integer.valueOf(mMon),
					Integer.valueOf(mDay));
			endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
			endDatePicker.show();
		} else if (!touchStartDate){
			if (!endDatePicker.isShowing()) {
				if (!"".equals(endDateChoice.getText().toString())) {
					String[] value = endDateChoice.getText().toString().split("-");
					endDatePicker.updateDate(Integer.valueOf(value[0]),
							Integer.valueOf(value[1]) - 1, Integer.valueOf(value[2]));
				} else {
					endDatePicker.updateDate(Integer.valueOf(mYear), Integer.valueOf(mMon), Integer.valueOf(mDay));
				}


				endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
				endDatePicker.show();
			}
		} 
	}
	
	public long getCurDate() {
		Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
		String year = String.valueOf(dateAndTime.get(Calendar.YEAR));
		int monthOfYear = dateAndTime.get(Calendar.MONTH);
		String month = "";
		if (monthOfYear <= 8) {
			month = "0" + (monthOfYear + 1);
		} else {
			month = String.valueOf(monthOfYear + 1);
		}
		int dayOfMonth = dateAndTime.get(Calendar.DAY_OF_MONTH);
		String day = "";
		if (dayOfMonth <= 9) {
			day = String.valueOf("0" + dayOfMonth);
		} else {
			day = String.valueOf(dayOfMonth);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(year);
		sb.append(month);
		sb.append(day);
		sb.append("000000");
		return Long.valueOf(sb.toString());
	}
	
	public void setCurDate() {
		Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
		mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
		mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
		mMonth = String.valueOf(dateAndTime.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(dateAndTime.get(Calendar.DAY_OF_MONTH));
	}


	public String getStartDateTime() {
		if ("".equals(startDateChoice.getText().toString())) {
			return "0";
		} else {
			return startDateChoice.getText().toString().replaceAll("-", "") + "000000";
		}
	}
	
	public String getEndDateTime() {
		if ("".equals(endDateChoice.getText().toString())) {
			return "0";
		} else {
			return endDateChoice.getText().toString().replaceAll("-", "") + "235959";
		}
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
			if (touchStartDate) {
				startDateChoice.setText(getDate());
			} else {
				endDateChoice.setText(getDate());
			}
		}
	};
	
	public interface TimerFilterListener {
		public void callBack(long startTime, long endTime);
	}
}
