package cn.com.bluemoon.delivery.module.storage;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.StorageTabState;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;

public class StorageTabActivity extends FragmentActivity {

	private String TAG = "StorageTabActivity";
	private static Context context;
	private LayoutInflater layoutInflater;
	private FragmentTabHost mTabHost;
	private String currentTag;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.extract_tab);
		context = this;

		ActivityManager.getInstance().pushOneActivity(this);
		layoutInflater = LayoutInflater.from(this);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.getTabWidget().setDividerDrawable(null);
			final StorageTabState[] deliveryStates = StorageTabState.values();
			currentTag = getString(deliveryStates[0].getContent());

			for (int i = 0; i < deliveryStates.length; i++) {
				TabSpec tabSpec = mTabHost.newTabSpec(getResources().getString(deliveryStates[i].getContent()))
						.setIndicator(getTabItemView(deliveryStates[i].getImage(), getResources().getString(deliveryStates[i].getContent()), i));
				mTabHost.addTab(tabSpec, deliveryStates[i].getClazz(), null);
			}

			mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
					currentTag = tabId;
				}
			});

		
	}
	
	private View getTabItemView(int resId, String content, int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(resId);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(content);
		
		return view;
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(TAG); 
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(TAG); 
	}

	public static void actionStart(Context context){
		Intent intent = new Intent(context , StorageTabActivity.class);
        context.startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}
}
