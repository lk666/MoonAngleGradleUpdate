package cn.com.bluemoon.delivery.sz.meeting;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
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
import cn.com.bluemoon.delivery.sz.util.Constants;
import cn.com.bluemoon.delivery.sz.util.FileUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class SzMsgListActivity extends KJActivity {

	private String TAG = SzMsgListActivity.class.getSimpleName();

	private CommonProgressDialog progressDialog;
	@BindView(id=R.id.listview_main)
	private PullToRefreshListView listView;

	private MessageListAdapter adapter;
	private ArrayList<MsgListItemBean> datalist;

	UserMsgListResponse response = null;

	private int msgType;
	private boolean hasNews;


	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		initCustomActionBar();
		setContentView(R.layout.activity_sz_message_list);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		ActivityManager.getInstance().pushOneActivity(this);
		progressDialog = new CommonProgressDialog(aty);

		msgType = getIntent().getIntExtra("msgType",1000);
		hasNews = getIntent().getBooleanExtra("hasNews",false);

		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getHttpData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}

		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
				int readIndex = index - 1;
				MsgListItemBean itemData = (MsgListItemBean) adapter.getItem(readIndex);
				Intent intent;
				switch (msgType){
					case Constants.MAIN_MSG_WAIT_REMIND:
						break;
					case Constants.MAIN_MSG_MEETING_REMIND:
						break;
					case Constants.MAIN_MSG_ADVICE_REMIND:
						intent = new Intent(aty,SzMsgAdviceReplyActivity.class);
						startActivity(intent);
						break;
					case Constants.MAIN_MSG_CONFLICT_REMIND:
						intent = new Intent(aty,SzMsgConflictActivity.class);
						startActivity(intent);
						break;
					case Constants.MAIN_MSG_DELEGATION_REMIND:
						break;
					default:
						PublicUtil.showToast("无该信息类型");
						break;
				}
			}
		});


		datalist = new ArrayList<MsgListItemBean>();
		adapter = new MessageListAdapter(aty,datalist);
		listView.setAdapter(adapter);
		//获取历史的信息
		getHistoryMsg();
		//获取最新的未读信息
		if(hasNews){
			getHttpData(true);
		}

	}

	public void getHistoryMsg(){
		String uid = ClientStateManager.getUserName();
		try{
			String cache= FileUtil.getSubMsg(uid,msgType);
			if(!StringUtil.isEmptyString(cache)){
				UserMsgListResponse responseTemp = JSON.parseObject(cache,UserMsgListResponse.class);
				response = responseTemp;
				adapter.refresh(response.getData());
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public void getHttpData(final boolean showloading){
		String uid = ClientStateManager.getUserName();
		SzApi.userMsgList(uid,msgType,new TextHttpResponseHandler(HTTP.UTF_8) {

			public void onStart(){
				super.onStart();
				if(showloading){
					progressDialog.show();
				}

			}

			public void onFinish(){
				super.onFinish();
				if(showloading){
					progressDialog.dismiss();
				}
				listView.onRefreshComplete();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				LogUtils.d(TAG,responseString);
				updateApiData(responseString);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
								  Throwable throwable) {
				LogUtils.e(TAG, throwable.getMessage());
				AsyncHttpClientUtil.onFailure(aty,statusCode);
			}
		});
	}

	private void updateApiData(String responseString){
		try {
			UserMsgListResponse responseTemp = JSON.parseObject(responseString,UserMsgListResponse.class);
			if(responseTemp.getResponseCode()== cn.com.bluemoon.delivery.sz.util.Constants.RESPONSE_RESULT_SUCCESS){
				if(response == null){
					response = responseTemp;
				}else{
					response.getData().addAll(0,responseTemp.getData());
				}
				String jsonStr = JSON.toJSONString(response);
				FileUtil.setSubMsg(ClientStateManager.getUserName(),msgType,jsonStr);
				adapter.refresh(response.getData());
			}else{
				PublicUtil.showToast(responseTemp.getResponseMsg());
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
			FileUtil.deleteSubMsg(ClientStateManager.getUserName(),msgType);
		}
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
				v.setText("消息列表");
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
