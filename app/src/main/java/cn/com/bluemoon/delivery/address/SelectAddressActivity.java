package cn.com.bluemoon.delivery.address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.address.ResultArea;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.entity.SubRegion;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class SelectAddressActivity extends ListActivity {
	private CommonProgressDialog progressDialog;
	List<Hashtable<String, String>> listContent;
	private static int time;
	private static List<SubRegion> subRegionList;
	private boolean selectControl = true;
	private String dcode;
	private String type;
	private long startTime;
	private String TAG = "SelectAddressActivity";


	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.activity_select_address);
		ActivityManager.getInstance().pushOneActivity(this);
		dcode = getIntent().getStringExtra("dcode");
		type = getIntent().getStringExtra("type");
		subRegionList = new ArrayList<SubRegion>();
		listContent= new ArrayList<Hashtable<String, String>>();
		if (progressDialog == null) {
			progressDialog = new CommonProgressDialog(this);
		}
		progressDialog.show();
		startTime = SystemClock.elapsedRealtime();
		
		
		if (dcode == null && type == null) {
			time = 0;
		} 
		DeliveryApi.getRegionSelect(dcode, type, getRegionHandler);
		setBackAction();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		progressDialog = null;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (selectControl) {
			selectControl = false;
			super.onListItemClick(l, v, position, id);
			Hashtable<String, String> table = (Hashtable<String, String>)this.getListAdapter().getItem(position);
			SubRegion subRegion = new SubRegion();
			subRegion.setDcode(table.get("code"));
			subRegion.setDname(table.get("name"));
			subRegionList.add(subRegion);
			
			if (++time >= 3 || (dcode != null && type != null)) {
				Intent mIntent = new Intent();
				Bundle bundle = new Bundle();  
				bundle.putSerializable("subRegionList", (Serializable) subRegionList); 
				if (dcode != null && type != null) {
					bundle.putString("type", type);
				}
				mIntent.putExtras(bundle);
				setResult(RESULT_OK,mIntent);
				this.finish();
				return;
			} 

			if (progressDialog != null) {
				progressDialog.show();
			}
			
			
			listContent= new ArrayList<Hashtable<String, String>>();
			DeliveryApi.getRegionSelect(table.get("code"), getType(time), getRegionHandler);

		}
		
	}
	
	private String getType(int time) {
		switch (time) {
		case 1:
			return "city";
		case 2:
			return "county";
		case 3:
			return "street";
		case 4:
			return "village";
		default:
			return null;
		}
	}
	
	private AsyncHttpResponseHandler getRegionHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("SelectAddressActivity", "response result = " + responseString);
			try {

				ResultArea result = JSON.parseObject(responseString, ResultArea.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					if (result.getLists() != null && result.getLists().size() > 0) {
					for (int i = 0 ;i < result.getLists().size();i++) {
						Hashtable<String, String> table = new Hashtable<String, String>();
						table.put("name", result.getLists().get(i).getDname());
						table.put("code", result.getLists().get(i).getDcode());
						listContent.add(table);
					}
					SimpleAdapter adapter = new SimpleAdapter(SelectAddressActivity.this, listContent,
							R.layout.address_select_list_item, new String[] { "name" }, new int[]{R.id.text1});
					SelectAddressActivity.this.setListAdapter(adapter);
				} else {
					LogUtils.e("SelectAddressActivity","call get region api failed.");
				}
				
				} else {
					PublicUtil.showErrorMsg(SelectAddressActivity.this, result);
				}

			} catch (Exception e) {
				PublicUtil.showToastServerBusy(SelectAddressActivity.this);
			} finally {
				dismissProgress();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
				Throwable throwable) {
			LogUtils.e("test", "getRegionHandler  ------ onFailure : statusCode = " + statusCode);
			dismissProgress();
			PublicUtil.showToastServerOvertime(SelectAddressActivity.this);
		}
	};
	
	private void dismissProgress() {
		selectControl = true;
		if (progressDialog != null) {
			long waitTime = SystemClock.elapsedRealtime() - startTime;
			if (waitTime < 500) {
				try {
					Thread.sleep(500-waitTime);
				} catch (InterruptedException e) {
				}
			}
			progressDialog.dismiss();
		}
	}
	
	private void setBackAction() {
		ImageView backAction = (ImageView)findViewById(R.id.back_action);
		backAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED, null);
				finish();
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
		    if(progressDialog!=null)
		    	progressDialog.dismiss();
		}


	public static void actionStart(Activity context, String dcode, String type) {
		Intent intent = new Intent(context, SelectAddressActivity.class);
		intent.putExtra("dcode", dcode);
		intent.putExtra("type", type);
		context.startActivityForResult(intent, 0);
	}
}
