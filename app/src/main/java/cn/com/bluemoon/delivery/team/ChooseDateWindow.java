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
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;

public class ChooseDateWindow extends PopupWindow {

	private Context mContext;
	private CommonDatePickerDialog endDatePicker;
	private TextView txtEndDate;
	private Button okBtn;
	private View view;
	private ChooseDateListener listener;
	private Calendar calendar;


	public ChooseDateWindow(Context context,String content,ChooseDateListener listener) {
		this.mContext = context;
		this.listener = listener;
		init(content);
	}

	private void init(String content) {
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
				listener.callBack(calendar.getTimeInMillis());
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
		String dateTxt = txtEndDate.getText().toString();
		if (dateTxt!=null||dateTxt.split("-").length==3) {
			String[] value = txtEndDate.getText().toString().split("-");
			setDate(Integer.valueOf(value[0]),Integer.valueOf(value[1]) - 1, Integer.valueOf(value[2]));
		}
		if (endDatePicker == null) {
			endDatePicker = new CommonDatePickerDialog(mContext, mDateSetListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
			endDatePicker.show();
		} else if(!endDatePicker.isShowing()){
			endDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			endDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
			endDatePicker.show();
		}
	}
	
	public void setDate(int year,int month,int day) {
		if(calendar==null){
			calendar = Calendar.getInstance(Locale.CHINA);
		}
		if(year>0&&month>-1&&day>0){
			calendar.set(year,month,day);
		}
	}

	public void setCurDate(){
		setDate(0, -1, 0);
		txtEndDate.setText(DateUtil.getTime(calendar.getTimeInMillis(), "yyyy-MM-dd"));
	}

	public long getDate() {
		return calendar.getTimeInMillis();
	}

	private CommonDatePickerDialog.OnDateSetListener mDateSetListener = new CommonDatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			setDate(year, monthOfYear, dayOfMonth);
			txtEndDate.setText(DateUtil.getTime(calendar.getTimeInMillis(), "yyyy-MM-dd"));

		}
	};
	
	public interface ChooseDateListener {
		public void callBack(long endTime);
	}
}
