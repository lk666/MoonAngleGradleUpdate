package cn.com.bluemoon.delivery.sz.meeting;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import cn.com.bluemoon.delivery.R;

import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

/**
 * 添加会议安排
 * @author jiangyuehua
 * */
public class SchedualAddMeetingActivity extends KJActivity {

	@BindView(id= R.id.tv_meetingTheme)
	private TextView tv_meetingTheme;
	@BindView(id=R.id.et_meetingTheme)
	private EditText et_meetingTheme;

	@BindView(id=R.id.tv_meetingTime)
	private TextView tv_meetingTime;
	@BindView(id=R.id.et_meetingTime)
	private EditText et_meetingTime;

	@BindView(id=R.id.tv_meetinger)
	private TextView tv_meetinger;
	@BindView(id=R.id.iv_meetinger,click =true)
	private ImageView iv_meetinger;

	@BindView(id=R.id.tv_meetingerContent)
	private TextView tv_meetingerContent;

	@BindView(id=R.id.tv_meetingAddress)
	private TextView tv_meetingAddress;
	@BindView(id=R.id.et_meetingAddress)
	private EditText et_meetingAddress;

	@BindView(id=R.id.tv_meetingMore)
	private TextView tv_meetingMore;
	@BindView(id=R.id.cb_meetingMore,click = true)
	private CheckBox cb_meetingMore;

	@BindView(id=R.id.ll_meetingMore,click = true)
	private LinearLayout ll_meetingMore;

	@BindView(id=R.id.tv_meetingContentTip)
	private TextView tv_meetingContentTip;
	@BindView(id=R.id.tv_meetingContent,click = true)
	private TextView tv_meetingContent;

	@BindView(id=R.id.tv_meetingSpeaker)
	private TextView tv_meetingSpeaker;
	@BindView(id=R.id.iv_meetingSpeaker)
	private ImageView iv_meetingSpeaker;

	@BindView(id=R.id.tv_meetingRecorder)
	private TextView tv_meetingRecorder;
	@BindView(id=R.id.iv_meetingRecorder)
	private ImageView iv_meetingRecorder;

	@BindView(id=R.id.tv_meetingReView)
	private TextView tv_meetingReView;
	@BindView(id=R.id.iv_meetingReView)
	private ImageView iv_meetingReView;

	@BindView(id=R.id.tv_meetingType)
	private TextView tv_meetingType;
	@BindView(id=R.id.rb_none)
	private RadioButton rb_none;
	@BindView(id=R.id.rb_decision)
	private RadioButton rb_decision;
	@BindView(id=R.id.rb_reView)
	private RadioButton rb_reView;
	@BindView(id=R.id.rb_progress)
	private RadioButton rb_progress;
	@BindView(id=R.id.tv_meetingAdjust)
	private TextView tv_meetingAdjust;
	@BindView(id=R.id.rb_adjust)
	private RadioButton rb_adjust;
	@BindView(id=R.id.rb_unadjust)
	private RadioButton rb_unadjust;

	private Context contxt;


	@Override
	public void setRootView() {
		setContentView(R.layout.activity_schedual_add_meeting);
		contxt=SchedualAddMeetingActivity.this;
		EventBus.getDefault().register(this);
	}

	@Override
	public void initWidget() {
		super.initWidget();
		initCustomActionBar();

		if (cb_meetingMore.isChecked())
				ll_meetingMore.setVisibility(View.VISIBLE);
		else
				ll_meetingMore.setVisibility(View.INVISIBLE);


	cb_meetingMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked){
				ll_meetingMore.setVisibility(View.VISIBLE);
			}else{
				ll_meetingMore.setVisibility(View.INVISIBLE);
			}
		}
	});
	}


	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
		switch (v.getId()){
			case R.id.tv_meetingContent:
				Bundle mbBundle=new Bundle();
				mbBundle.putString("inputContent",tv_meetingContent.getText().toString());
				PageJumps.PageJumps(contxt,InputUtilActivity.class,mbBundle);
				break;
			default:
				break;
		}
	}

	 PopupWindow popupWindow=null;
	public void showPopWindow(TextView tv){

		DisplayMetrics dm=getResources().getDisplayMetrics();

		View view = LayoutInflater.from(contxt).inflate(R.layout.pop_meeting_input,null);
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

//		view.setLayoutParams(new LinearLayout.LayoutParams((int) (dm.widthPixels*0.8),
//				LinearLayout.LayoutParams.WRAP_CONTENT));

//		实例

		EditText et_popContent= (EditText) view.findViewById(R.id.et_popContent);

		popupWindow=new PopupWindow(view,
				ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());//为空
		popupWindow.setContentView(view);

		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				closePopupWindow(popupWindow);
			}
		});
		// 设置窗口半透明
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		params.alpha = 0.7f;
		this.getWindow().setAttributes(params);
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				closePopupWindow(popupWindow);
			}
		});

		int[] location=new int[2];

		tv_meetingMore.getLocationOnScreen(location);


//		dm.widthPixels
// 在底部显示
		popupWindow.showAtLocation(tv_meetingTheme, Gravity.TOP,
				(dm.widthPixels/2)-popupWindow.getContentView().getMeasuredWidth(), 0);

//		popupWindow.showAtLocation(tv_meetingMore,
//				Gravity.NO_GRAVITY,location[0],location[1]-popupWindow.getHeight());

		Log.v("pop 位置信息 ",dm.widthPixels+"："+popupWindow.getContentView().getMeasuredWidth()+":"+popupWindow.getContentView().getMeasuredHeight());
		Log.v("pop 位置信息 view",dm.widthPixels+"："+view.getMeasuredWidth()+":"+view.getMeasuredHeight());
//		30:413
		Log.v("pop 位置 ",location[0]+"："+location[1]+":"+view.getWidth());

	}

	private void closePopupWindow(PopupWindow popupWindow) {
		if (popupWindow != null) {
			WindowManager.LayoutParams params = this.getWindow()
					.getAttributes();
			params.alpha = 1f;
			this.getWindow().setAttributes(params);
			if (popupWindow!=null) {
				popupWindow.dismiss();
			}
		}
	}

	private void initCustomActionBar() {

		CommonActionBar commonActionBar=new CommonActionBar(getActionBar(),new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {
				// TODO Auto-generated method stu
			}

			@Override
			public void onBtnLeft(View v) {
				// TODO Auto-generated method stub
				finish();
			}

			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				v.setText(R.string.sz_meeting_schedual_add_meeting);
			}
		});

		TextView tv_right=commonActionBar.getTvRightView();
		tv_right.setVisibility(View.VISIBLE);
		tv_right.setText("确定");
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void getEventMessageBean(EventMessageBean messageBean){
		if (messageBean.getEventMsgAction().equals("MeetingContent"))
			tv_meetingContent.setText(messageBean.getEventMsgContent());
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
