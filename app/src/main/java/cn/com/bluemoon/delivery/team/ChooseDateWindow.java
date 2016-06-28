/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/3/5
 */
package cn.com.bluemoon.delivery.team;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;

public class ChooseDateWindow extends PopupWindow {

	private Context mContext;
	private String mYear;
	private String mMonth;
	private String mMon;
	private String mDay;
	private CommonDatePickerDialog endDatePicker;
	private TextView txtEndDate;
	private Button okBtn;
	private View view;
	private ChooseDateListener listener;


	public ChooseDateWindow(Context context,String content,ChooseDateListener listener) {
		this.mContext = context;
		this.listener = listener;
		Init(content);
	}

	private void Init(String content) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_team_unlock_date, null);


		LinearLayout ll_popup = (LinearLayout) view
				.findViewById(R.id.layout_choose_date);

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.bg_transparent));

		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_top_in));

		((TextView) view.findViewById(R.id.txt_name)).setText(content);
		txtEndDate = (TextView) view.findViewById(R.id.txt_end_date);
		okBtn = (Button) view.findViewById(R.id.btn_confirm);
		txtEndDate.setOnClickListener(onclicker);
		okBtn.setOnClickListener(onclicker);
		view.setOnClickListener(onclicker);
		setCurDate();
	}

	OnClickListener onclicker = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == txtEndDate){
				showDatePickerDialog();
			}else if(v == okBtn){
				if(listener!=null)
				listener.callBack(Long.valueOf(getEndDateTime()));
				dismiss();
			}else if(v == view){
				dismiss();
			}
		}
	};

	public void showPopwindow(View popStart) {
		showAsDropDown(popStart);
	}

	public void showDatePickerDialog() {

		if (endDatePicker == null) {
			endDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener,
					Integer.valueOf(mYear), Integer.valueOf(mMon),
					Integer.valueOf(mDay));
			endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
			endDatePicker.show();
		} else {
			if (!endDatePicker.isShowing()) {
				if (!"".equals(txtEndDate.getText().toString())) {
					String[] value = txtEndDate.getText().toString().split("-");
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
	
	public void setCurDate() {
		Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
		mYear = String.valueOf(dateAndTime.get(Calendar.YEAR));
		mMon = String.valueOf(dateAndTime.get(Calendar.MONTH));
		mMonth = String.valueOf(dateAndTime.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(dateAndTime.get(Calendar.DAY_OF_MONTH));
		String month = mMonth;
		String day = mDay;
		if(mMonth.length()==1){
			month = "0"+mMonth;
		}
		if(mDay.length()==1){
			day = "0"+mDay;
		}
		txtEndDate.setText(mYear + "-" + month + "-" + day);
	}

	public String getEndDateTime() {
		if ("".equals(txtEndDate.getText().toString())) {
			return "0";
		} else {
			return txtEndDate.getText().toString().replaceAll("-", "") + "235959";
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
			txtEndDate.setText(getDate());
		}
	};
	
	public interface ChooseDateListener {
		public void callBack(long endTime);
	}
}
