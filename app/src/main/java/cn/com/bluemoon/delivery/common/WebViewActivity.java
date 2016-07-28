package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.CacheManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.callback.JsConnectCallBack;
import cn.com.bluemoon.lib.utils.JsConnectManager;
import cn.com.bluemoon.lib.utils.LibCacheUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class WebViewActivity extends Activity {
	private String TAG="WebViewActivity";
	private WebViewActivity aty;
	private WebView moonWebView;
	private CommonProgressDialog progressDialog;
	private String url;
	private String title;
	private Button btnRefresh;
	private View viewNowify;
	private String scanCallbackName;
	private RelativeLayout layout_title;
	private TextView txtTitle;
	private ImageView imgBack;
	private ProgressBar pro;
	private boolean isActionBar;
	private boolean isProgress;
	private boolean isBackByJs;
	private boolean isBackFinish;
	private boolean isClearCache;
	private Map<String,String> map;
	/**
	 * TODO 
	 * @see Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  
		// TODO Auto-generated method stub  
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		aty = this;
		ActivityManager.getInstance().pushOneActivity(this);
		map = new HashMap<>();
		progressDialog = new CommonProgressDialog(this);
		pro = (ProgressBar) findViewById(R.id.pro_web);
		layout_title = (RelativeLayout) findViewById(R.id.layout_title);
		txtTitle = (TextView) findViewById(R.id.txt_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		moonWebView = (WebView) findViewById(R.id.common_webview);
		btnRefresh = (Button)findViewById(R.id.btn_empty_order);
		viewNowify = findViewById(R.id.layout_no_wifi);
		if(getIntent()!=null){
			url = getIntent().getStringExtra("url");
			title = getIntent().getStringExtra("title");
			if(title!=null) pushTitle(url,title);
			isActionBar = getIntent().getBooleanExtra("actionbar",false);
			isProgress = getIntent().getBooleanExtra("progress",false);
			isBackByJs = getIntent().getBooleanExtra("back",false);
			isBackFinish = getIntent().getBooleanExtra("isBackFinish",false);
			isClearCache = getIntent().getBooleanExtra("isClearCache",false);
			if(isActionBar){
				if (layout_title.getVisibility() == View.GONE)
					layout_title.setVisibility(View.VISIBLE);
			}
		}
		imgBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isBackFinish || (isBackByJs && viewNowify.getVisibility() == View.VISIBLE)) {
					finish();
				} else if (isBackByJs) {
					JsConnectManager.keyBack(moonWebView);
				} else if (moonWebView.canGoBack()) {
					moonWebView.goBack();
					popTitle();
				} else {
					finish();
				}
			}
		});
		WebSettings webSetting = moonWebView.getSettings();
		webSetting.setDomStorageEnabled(true);
		webSetting.setDatabaseEnabled(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSetting.setAppCacheEnabled(false);
		moonWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(view, title);
				pushTitle(view.getOriginalUrl(),title);

			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
//				LogUtils.d(TAG,"result ="+newProgress);
				if (isProgress) {
					if (newProgress < 100) {
						if (pro.getVisibility() == View.GONE)
							pro.setVisibility(View.VISIBLE);
						pro.setProgress(newProgress);
					} else {
						if (pro.getVisibility() == View.VISIBLE)
							pro.setVisibility(View.GONE);
					}
				}
			}
		});
		moonWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				LogUtils.d("jsConnect", "result =" + url);
				return PublicUtil.jsConnect(view, url, callBack);
			}

			/**
			 * TODO 
			 * @see WebViewClient#onPageStarted(WebView, String, Bitmap)
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				// TODO Auto-generated method stub  
				super.onPageStarted(view, url, favicon);
				if (!isProgress) {
					if (null == progressDialog) {
						progressDialog = new CommonProgressDialog(WebViewActivity.this);
					}
					progressDialog.show();
				}
			}

			/**
			 * TODO 
			 * @see WebViewClient#onPageFinished(WebView, String)
			 */
			@Override
			public void onPageFinished(WebView view, String url) {

				// TODO Auto-generated method stub  
				super.onPageFinished(view, url);
				if (!isProgress) {
					if (null != progressDialog) {
						progressDialog.dismiss();
					}
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
										String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				viewNowify.setVisibility(View.VISIBLE);
			}

		});
		load(url);
		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				load(url);
			}
		});

	}
	
	private void load(String url){
		if(!PublicUtil.hasIntenet(this)){
			viewNowify.setVisibility(View.VISIBLE);
			return;
		}
		if(viewNowify.getVisibility()==View.VISIBLE){
			viewNowify.setVisibility(View.GONE);
		}
		moonWebView.loadUrl(url);
	}

	private void pushTitle(String url,String title){
		map.put(url, title);
		txtTitle.setText(title);
	}

	private void popTitle(){
		String title = map.get(moonWebView.getOriginalUrl());
		if(title!=null){
			txtTitle.setText(title);
		}

	}

	JsConnectCallBack callBack = new JsConnectCallBack() {
		@Override
		public void webView(WebView view, String url, String title, String callbackName) {
			PublicUtil.openWebView(aty,url,title);
		}

		@Override
		public void scan(WebView view,String title, String callbackName) {
			PublicUtil.openScanCard(aty, null,title, Constants.REQUEST_SCAN);
			scanCallbackName = callbackName;
		}

		@Override
		public void closeWebView(WebView view, String callbackName) {
			aty.finish();
		}

		@Override
		public String getAppInfo() {
			return PublicUtil.getAppInfo();
		}

		@Override
		public String getCacheSize(WebView view) {
			return LibCacheUtil.getWebViewCacheSize(aty);
		}

		@Override
		public void cleanCache(WebView view) {
			LibCacheUtil.cleanWebViewCache(aty);
		}
	};

	private void callback(String result){
		if(scanCallbackName!=null){
			JsConnectManager.loadJavascript(moonWebView, scanCallbackName, result);
		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		progressDialog = null;
		if(map!=null){
			map.clear();
			map = null;
		}
		if(isClearCache){
			CacheManager.cleanWebViewCache(aty);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0){
			if(isBackByJs&&viewNowify.getVisibility()==View.VISIBLE){
				finish();
			}else if(isBackByJs){
				JsConnectManager.keyBack(moonWebView);
			}else if(moonWebView.canGoBack()){
				moonWebView.goBack();
				popTitle();
			}else{
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_CANCELED){
			if(requestCode==Constants.REQUEST_SCAN){
				callback("CANCELED");
			}
			return;
		}
		if(resultCode == RESULT_OK){
			switch (requestCode){
				case Constants.REQUEST_SCAN:
					if(data==null) return;
					callback(data.getStringExtra(LibConstants.SCAN_RESULT));
					break;
			}
		}
	}
}
  
