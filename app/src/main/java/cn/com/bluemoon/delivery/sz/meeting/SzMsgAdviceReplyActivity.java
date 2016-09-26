package cn.com.bluemoon.delivery.sz.meeting;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class SzMsgAdviceReplyActivity extends KJActivity {

	private String TAG = SzMsgAdviceReplyActivity.class.getSimpleName();

	private CommonProgressDialog progressDialog;

	@BindView(id=R.id.who_tv)
	TextView replyPeopleTv;
	@BindView(id=R.id.reply_content_tv)
	TextView replyContentTv;
	@BindView(id=R.id.advice_title_tv)
	TextView adviceTitleTv;
	@BindView(id=R.id.advice_content_tv)
	TextView adviceContentTv;
	@BindView(id=R.id.meeting_detail_tv,click=true)
	TextView detailTv;
	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_sz_msg_advice_reply);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);

		String replyName = "陈三";
		replyPeopleTv.setText(replyName+"对您的建议进行了回复");
		String replyContent = "好的，我会及时跟进项目的进展";
		replyContentTv.setText(replyContent);
		adviceTitleTv.setText("您对"+replyName+"的建议：");
		String advice = "请时刻关注项目的进度，有问题及时提出来。";
		adviceContentTv.setText(advice);

	}




	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.meeting_detail_tv:
			PublicUtil.showToast(aty,"you click me");
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return false;
	}
	
	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(),new IActionBarListener() {
			
			@Override
			public void onBtnRight(View v) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onBtnLeft(View v) {
				// TODO Auto-generated method stub
				finish();
			}

			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				v.setText("建议回复提醒");
			}
		});
		
	}


	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG);

	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(TAG); 
	    if(progressDialog != null)
			progressDialog.dismiss();
	}

}
