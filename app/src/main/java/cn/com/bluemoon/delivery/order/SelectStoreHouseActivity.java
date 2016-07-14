/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/2/24
 */
package cn.com.bluemoon.delivery.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultStorehouse;
import cn.com.bluemoon.delivery.app.api.model.Storehouse;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.OrdersUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * @author Administrator
 *
 */
public class SelectStoreHouseActivity extends Activity implements OnClickListener{

	private String TAG = "SelectStoreHouseActivity";
	private CommonActionBar mActionbar;
	private LinearLayout layoutOk;
	private TextView phoneText;
	private TextView addressText;
	private List<Storehouse> storehouses;
	private ListView listView;
	private StoreHouseAdapter adapter;
	private CommonProgressDialog progressDialog;
	private Storehouse shSelect;
	private String code;
	private String dispatchId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storehouse);
		progressDialog = new CommonProgressDialog(this);
		initCustomActionBar();
		ActivityManager.getInstance().pushOneActivity(this);
		layoutOk = (LinearLayout) findViewById(R.id.layout_ok);
		phoneText = (TextView) findViewById(R.id.txt_phone);
		addressText = (TextView) findViewById(R.id.txt_address);
		listView = (ListView) findViewById(R.id.listview_warehouse);
		layoutOk.setOnClickListener(this);
		dispatchId = getIntent().getStringExtra("dispatchId");
		code = getIntent().getStringExtra("code");
		
		progressDialog.show();
		DeliveryApi.getStorehouseList(ClientStateManager.getLoginToken(this), getShHandler);

	}
	
	AsyncHttpResponseHandler getShHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				String responseString) {
			LogUtils.d("test", "getShHandler result = " + responseString);
			progressDialog.dismiss();
			boolean tag = true;
			try {
				ResultStorehouse result = JSON.parseObject(responseString,
						ResultStorehouse.class);
				if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
					storehouses = result.getItemList();
					if (storehouses.size() > 0) {
						for (int i = 0; i < storehouses.size(); i++) {
							Storehouse sh = storehouses.get(i);
							if (StringUtils.isNotBlank(code) && code.equals(sh.getStorehouseCode())) {
								sh.setSelect(true);
								storehouses.set(i, sh);
								shSelect = sh;
								tag = false;
							} else if (tag && i == storehouses.size()-1){
								shSelect = storehouses.get(0);
								shSelect.setSelect(true);
								storehouses.set(0, shSelect);
							}
						}
						if (StringUtils.isNotBlank(shSelect.getStorechargeMobileno())) {
							phoneText.setText(String.format(getString(R.string.pending_order_person_phone), shSelect.getStorechargeMobileno()));
						} else {
							phoneText.setText(getString(R.string.pending_order_person_phone_null));
						}
						addressText.setText(shSelect.getAddress());
						adapter = new StoreHouseAdapter(SelectStoreHouseActivity.this, R.layout.item_storehouse_list);
						listView.setAdapter(adapter);
					}
				} else {
					PublicUtil.showErrorMsg(SelectStoreHouseActivity.this,
							result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy(SelectStoreHouseActivity.this);
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, 
				Throwable throwable) {
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(SelectStoreHouseActivity.this);
		}
	};
	
	AsyncHttpResponseHandler saveShHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			LogUtils.d("test","saveShHandler result = " + responseString);
			progressDialog.dismiss();
			try {
				ResultStorehouse result = JSON.parseObject(responseString, ResultStorehouse.class);
				if(result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
					//PublicUtil.showToast(SelectStoreHouseActivity.this, result.getResponseMsg());
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("storehouse", shSelect);
					intent.putExtras(bundle);
					setResult(1, intent);
					finish();
				}else{
					PublicUtil.showErrorMsg(SelectStoreHouseActivity.this, result);
				}
			} catch (Exception e) {
				PublicUtil.showToastServerBusy(SelectStoreHouseActivity.this);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString,
							  Throwable throwable) {
			progressDialog.dismiss();
			PublicUtil.showToastServerOvertime(SelectStoreHouseActivity.this);
		}
	};
	
	@Override
	public void onClick(View v) {
		if (v == layoutOk) {
			if (shSelect != null) {
				progressDialog.show();
				DeliveryApi.saveStorehouse(ClientStateManager.getLoginToken(this), dispatchId, shSelect, saveShHandler);
			}
		}
	}

	private void initCustomActionBar() {
		mActionbar = new CommonActionBar(getActionBar(),
				new IActionBarListener() {

					@Override
					public void onBtnRight(View v) {

					}

					@Override
					public void onBtnLeft(View v) {
						SelectStoreHouseActivity.this.finish();
					}

					@Override
					public void setTitle(TextView v) {
						v.setText(R.string.pending_order_select_storehouse_title);
					}

				});
	}

	class StoreHouseAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private int layoutID;

		public StoreHouseAdapter(Context context, int layoutID) {
			this.mInflater = LayoutInflater.from(context);
			this.layoutID = layoutID;
		}

		@Override
		public int getCount() {
			return storehouses.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(layoutID, null);
			} 
			LinearLayout layoutStorehouse = ViewHolder.get(convertView, R.id.layout_storehouse);
			TextView txtStoreHouse = ViewHolder.get(convertView, R.id.txt_storehouse);
			final CheckBox cb = ViewHolder.get(convertView, R.id.cb_select);
			View lineDotted = ViewHolder.get(convertView, R.id.line_dotted);
			View lineSilde = ViewHolder.get(convertView, R.id.line_silde);
			//set default select
			final Storehouse sh = storehouses.get(position);
			cb.setChecked(sh.isSelect());
			if (position == storehouses.size()-1) {
				lineDotted.setVisibility(View.GONE);
				lineSilde.setVisibility(View.VISIBLE);
			} else {
				lineDotted.setVisibility(View.VISIBLE);
				lineSilde.setVisibility(View.GONE);
			}
			
			txtStoreHouse.setText(OrdersUtils.getShContent(sh));
			
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v == cb && !cb.isChecked()) {
						cb.setChecked(true);
					}
					for (int i = 0; i< storehouses.size(); i++) {
						Storehouse sh = storehouses.get(i);
						sh.setSelect(i == position);
						storehouses.set(i, sh);
					}
					shSelect = sh;
					if (StringUtils.isNotBlank(sh.getStorechargeMobileno())) {
						phoneText.setText(String.format(getString(R.string.pending_order_person_phone), sh.getStorechargeMobileno()));
					} else {
						phoneText.setText(getString(R.string.pending_order_person_phone_null));
					}
					addressText.setText(sh.getAddress());
					notifyDataSetChanged();
				}
			};
			
			cb.setOnClickListener(listener);
			layoutStorehouse.setOnClickListener(listener);

			return convertView;
		}

	}
	
	private void changeUserInfo() {
		if (shSelect  != null) {
			if (StringUtils.isNotBlank(shSelect.getStorechargeMobileno())) {
				phoneText.setText(String.format(getString(R.string.pending_order_person_phone), shSelect.getStorechargeMobileno()));
			} else {
				phoneText.setText(getString(R.string.pending_order_person_phone_null));
			}
			addressText.setText(shSelect.getAddress());
			PublicUtil.showToast(shSelect.getAddress());
		} else {
			PublicUtil.showToast("null!!!!!!!!!!!!!!");
		}
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

}
