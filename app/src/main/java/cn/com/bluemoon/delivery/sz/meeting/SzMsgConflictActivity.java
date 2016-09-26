package cn.com.bluemoon.delivery.sz.meeting;

import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.util.ViewUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class SzMsgConflictActivity extends KJActivity {

	private String TAG = SzMsgConflictActivity.class.getSimpleName();

	private CommonProgressDialog progressDialog;

	@BindView(id=R.id.conflict_tv)
	TextView conflictTv;
	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_sz_msg_conflict);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);
		String htmlStr = ViewUtil.createHtmlStr("动员会议","界面设计日程工作");
		conflictTv.setText(Html.fromHtml(htmlStr));
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.search_llt:
			//PublicUtil.showToast(aty,"you click search button");
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
				v.setText("时间安排冲突");
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
