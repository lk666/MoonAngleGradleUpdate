/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/3/5
 */
package cn.com.bluemoon.delivery.module.team;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.DateTextView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

public class ChooseDateWindow extends PopupWindow {

	private Context mContext;
	private DateTextView txtEndDate;
	private TextView txtContent;
	private Button okBtn;
	private View view;
	private ChooseDateListener listener;
	private String communityCode;
	private String bpCode;
	private long minDate;
	private long maxDate;
	private long curDate;

	public ChooseDateWindow(Context context,String bpCode,String bpName,String communityCode,ChooseDateListener listener) {
		this.mContext = context;
		this.listener = listener;
		this.bpCode = bpCode;
		this.communityCode = communityCode;
		init(StringUtil.getStringParams(bpCode, bpName));
	}

	public ChooseDateWindow(Context context,ChooseDateListener listener) {
		this.mContext = context;
		this.listener = listener;
		init(null);
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

		txtContent = ((TextView) view.findViewById(R.id.txt_name));
		txtEndDate = (DateTextView) view.findViewById(R.id.txt_end_date);
		txtContent.setText(content);
		okBtn = (Button) view.findViewById(R.id.btn_confirm);
		okBtn.setOnClickListener(onclicker);
		view.setOnClickListener(onclicker);
		txtEndDate.setCallBack(callBack);
	}

	OnClickListener onclicker = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == okBtn){
				if(listener!=null)
				listener.callBack(bpCode,communityCode,curDate);
				dismiss();
			}else if(v == view){
				dismiss();
			}
		}
	};

	DateTextView.DateTextViewCallBack callBack = new DateTextView.DateTextViewCallBack() {
		@Override
		public void onDate(View view, long returnDate) {
			curDate = returnDate;
		}
	};

	public void showPopwindow(View popStart) {
		if(minDate>0&&minDate>System.currentTimeMillis()){
			txtEndDate.setText(DateUtil.getTime(minDate));
			curDate = minDate;
		}else if(maxDate>0&&maxDate<System.currentTimeMillis()) {
			txtEndDate.setText(DateUtil.getTime(maxDate));
			curDate = maxDate;
		}else{
			txtEndDate.setText(DateUtil.getCurDate());
			curDate = System.currentTimeMillis();
		}
		showAsDropDown(popStart);
	}

	public void setData(String bpCode,String bpName,String communityCode){
		this.bpCode = bpCode;
		this.communityCode = communityCode;
		txtContent.setText(StringUtil.getStringParams(bpCode,bpName));
	}

	public void setMinDate(long minDate) {
		this.minDate = minDate;
		txtEndDate.updateMinDate(minDate);
	}

	public void setMaxDate(long maxDate) {
		this.maxDate = maxDate;
		txtEndDate.updateMaxDate(maxDate);
	}

	public long getDate() {
		return curDate;
	}

	public interface ChooseDateListener {
		public void callBack(String bpCode,String commonityCode,long endTime);
	}
}
