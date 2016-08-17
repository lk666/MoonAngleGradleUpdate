package cn.com.bluemoon.delivery.sz.meeting;

import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.adapter.MessageListAdapter;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.api.response.UserMsgListResponse;
import cn.com.bluemoon.delivery.sz.bean.MsgListItemBean;
import cn.com.bluemoon.delivery.sz.util.AsyncHttpClientUtil;
import cn.com.bluemoon.delivery.sz.util.FileUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class MessageConflictActivity extends KJActivity {

	private String TAG = MessageConflictActivity.class.getSimpleName();

	private CommonProgressDialog progressDialog;

	@BindView(id=R.id.conflict_tv)
	TextView conflictTv;
	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_meeting_conflict);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);
		String htmlStr = createHtmlStr("动员会议","界面设计日程工作");

		conflictTv.setText(Html.fromHtml(htmlStr));
	}

	private String createHtmlStr(String meet1,String meet2){
		String html = "";
		String meet1Html = getYellowFont(meet1);
		String meet2Html = getYellowFont(meet2);
		html = meet1Html+getBlackFont("与")+meet2Html+getBlackFont("时间安排有冲突，请根据实际情况进行调整");

		return html;
	}

	private String getYellowFont(String text){
		return "<font color=\"#FF863E\">" + text + "</font> ";
	}

	private String getBlackFont(String text){
		return "<font color=\"#464646\">" + text + "</font> ";
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
