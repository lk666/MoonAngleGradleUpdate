package cn.com.bluemoon.delivery.sz.meeting;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;


import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

import cn.com.bluemoon.delivery.app.api.model.message.Message;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.adapter.MessageAdapter;
import cn.com.bluemoon.delivery.sz.api.response.MsgMainTypeResponse;
import cn.com.bluemoon.delivery.sz.api.response.UserSchDayResponse;
import cn.com.bluemoon.delivery.sz.bean.MainMsgCountBean;
import cn.com.bluemoon.delivery.sz.util.AssetUtil;
import cn.com.bluemoon.delivery.sz.util.FileUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;

import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.ClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class MessageActivity extends KJActivity {

	private String TAG = MessageActivity.class.getSimpleName();

	private CommonProgressDialog progressDialog;
	@BindView(id=R.id.listview_main)
	private PullToRefreshListView listView;
	@BindView(id=R.id.search_llt,click = true)
	private LinearLayout searchLlt;

	private MessageAdapter adapter;


	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_message);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);


		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getData(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}

		});
		MessageCountController.getInstance().initMsgCount();
		adapter = new MessageAdapter(aty,MessageCountController.getInstance().getMsgCountBeanArrayList());
		listView.setAdapter(adapter);
		getData(false);

	}

	public void getData(boolean isRefresh){
		MessageCountController.getInstance().getMsgMainTypeCount(aty,isRefresh, new RequestListener() {
			@Override
			public void getCacheCallBack(String dataString) {
				updateMsgMainTypeCount(dataString,false);
			}

			@Override
			public void getHttpCallBack(String dataString) {
				updateMsgMainTypeCount(dataString,true);
			}

			@Override
			public void stopLoad(){
				listView.onRefreshComplete();
			}
		});
	}

	private void updateMsgMainTypeCount(String responseString,boolean isUpdate){
		try {
			MsgMainTypeResponse response = JSON.parseObject(responseString,MsgMainTypeResponse.class);
			if(response.getResponseCode()== cn.com.bluemoon.delivery.sz.util.Constants.RESPONSE_RESULT_SUCCESS){
				//是否更新缓存
				if(isUpdate){
					FileUtil.setMainMsgCount(ClientStateManager.getUserName(),responseString);
				}
				MessageCountController.getInstance().mergeMsgCount(response.getMainTypeNews());
				adapter.refresh(MessageCountController.getInstance().getMsgCountBeanArrayList());
			}else{

				PublicUtil.showToast(response.getResponseMsg());
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
			FileUtil.deleteMainMsgCount(ClientStateManager.getUserName());
		}
	}




	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.search_llt:
			PublicUtil.showToast(aty,"you click search button");
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
				v.setText("消息");
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
