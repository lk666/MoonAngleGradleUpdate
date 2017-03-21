package cn.com.bluemoon.delivery.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.tencentX5.JsConnectCallBack;
import cn.com.bluemoon.delivery.utils.tencentX5.JsConnectManager;
import cn.com.bluemoon.lib.utils.LibCacheUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.utils.LibFileUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.TakePhotoPopView;

public class X5WebViewActivity extends Activity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private X5WebViewActivity aty;
    private WebView moonWebView;
    private CommonProgressDialog progressDialog;
    private String url;
    private String title;
    private Button btnRefresh;
    private View viewNowify;
    private String scanCallbackName;
    private TextView txtTitle;
    private ImageView imgBack;
    private ProgressBar pro;
    private boolean isActionBar;
    private boolean isBackByJs;
    private boolean isBackFinish;
    private Map<String, String> map;
    private String locationCallbackName;
    public LocationClient mLocationClient = null;
    private boolean isFiveAbove = false;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private TakePhotoPopView takePhotoPop;


    public static void startAction(Context context, String url, String title, boolean isActionBar,
                                   boolean isBackByJs) {
        Intent intent = new Intent(context, X5WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("actionbar", isActionBar);
        intent.putExtra("back", isBackByJs);
        context.startActivity(intent);
    }


    /**
     * TODO
     *
     * @see Activity#onCreate(Bundle)
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            url = getIntent().getStringExtra("url");
            title = getIntent().getStringExtra("title");
            isActionBar = getIntent().getBooleanExtra("actionbar", false);
            isBackByJs = getIntent().getBooleanExtra("back", false);
            isBackFinish = getIntent().getBooleanExtra("isBackFinish", false);
        }
        setContentView(R.layout.activity_x5webview);
        aty = this;
        progressDialog = new CommonProgressDialog(this);
        pro = (ProgressBar) findViewById(R.id.pro_web);
        RelativeLayout layout_title = (RelativeLayout) findViewById(R.id.layout_title);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        imgBack = (ImageView) findViewById(R.id.img_back);
        moonWebView = (WebView) findViewById(R.id.common_webview);
        btnRefresh = (Button) findViewById(R.id.btn_empty_order);
        viewNowify = findViewById(R.id.layout_no_wifi);
        map = new HashMap<>();
        if (title != null) {
            pushTitle(url, title);
        }
        if (isActionBar) {
            LibViewUtil.setViewVisibility(layout_title, View.VISIBLE);
        }
        imgBack.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        takePhotoPop = new TakePhotoPopView(this,
                Constants.TAKE_PIC_RESULT, Constants.CHOSE_PIC_RESULT, new TakePhotoPopView
                .DismissListener() {
            @Override
            public void cancelReceiveValue() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(null);
                        mFilePathCallback = null;
                    }
                } else {
                    if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(null);
                        mUploadMessage = null;
                    }
                }
            }
        });

        WebSettings webSetting = moonWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);//设置可执行js脚本
        webSetting.setUseWideViewPort(true);//设置网页适应手机屏幕
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setDisplayZoomControls(false);//隐藏缩放按钮
        webSetting.setAllowFileAccess(true); // 允许访问文件
        webSetting.setDomStorageEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        moonWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                // TODO Auto-generated method stub
                super.onReceivedTitle(view, title);
                pushTitle(view.getOriginalUrl(), title);

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//				LogUtils.d(TAG,"result ="+newProgress);
                if (isActionBar) {
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

            // android 5.0
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                isFiveAbove = true;
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                takePhotoPop.getPic(moonWebView);
                return true;
            }

            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                isFiveAbove = false;
                mUploadMessage = uploadMsg;
                takePhotoPop.getPic(moonWebView);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                isFiveAbove = false;
                mUploadMessage = uploadMsg;
                takePhotoPop.getPic(moonWebView);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String
                    capture) {
                isFiveAbove = false;
                mUploadMessage = uploadMsg;
                takePhotoPop.getPic(moonWebView);
            }
        });
        moonWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //call phone
                if (url.startsWith(WebView.SCHEME_TEL)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        LogUtils.e("call phone error");
                    } finally {
                        return true;
                    }
                }
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
                if (!isActionBar) {
                    if (null == progressDialog) {
                        progressDialog = new CommonProgressDialog(X5WebViewActivity.this);
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
                if (!isActionBar) {
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

        moonWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {

                LogUtils.d("url:" + url + "\nuserAgent:" + userAgent + "\ncontentDisposition:" +
                        contentDisposition + "\nmimetype:" + mimetype + "\ncontentLength:" +
                        contentLength);

                downClick(url, mimetype);

            }
        });

        initView();

        load(url);

    }

    private void load(String url) {
        if (!PublicUtil.hasIntenet(this)) {
            viewNowify.setVisibility(View.VISIBLE);
            return;
        }
        if (viewNowify.getVisibility() == View.VISIBLE) {
            viewNowify.setVisibility(View.GONE);
        }
        moonWebView.loadUrl(url);
    }

    private void pushTitle(String url, String title) {
        if (isActionBar && txtTitle != null) {
            map.put(url, title);
            txtTitle.setText(title);
        }
    }

    private void popTitle() {
        if (isActionBar && txtTitle != null) {
            txtTitle.setText(map.get(moonWebView.getOriginalUrl()));
        }
    }

    JsConnectCallBack callBack = new JsConnectCallBack() {
        @Override
        public void webView(WebView view, String url, String title, String callbackName) {
            PublicUtil.openWebView(aty, url, title);
        }

        @Override
        public void scan(WebView view, String title, String callbackName) {
            PublicUtil.openScanView(aty, null, title, Constants.REQUEST_SCAN);
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
        public void share(WebView view, String topic, String content, String picUrl, String url) {
            PublicUtil.share(aty, topic, content, picUrl, url);
        }

        @Override
        public void cleanCache(WebView view) {
            LibCacheUtil.cleanWebViewCache(aty);
        }

        @Override
        public void getLoaction(WebView view, String callbackName) {
            locationCallbackName = callbackName;
            if (mLocationClient != null && mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
            mLocationClient = new LocationClient(aty);
            mLocationClient.registerLocationListener(myListener);
            initLocation();
            mLocationClient.start();
        }

        @Override
        public void logout(WebView view) {
            PublicUtil.showMessageTokenExpire(aty);
        }
    };

    private void initLocation() {
        LocationClientOption mOption = new LocationClientOption();
        mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setCoorType("bd09ll");
        mOption.setOpenGps(true);
        mOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(mOption);
    }

    public BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            //Receive Location
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude = 0;
            boolean isSuccess = true;
            String gpsType;

            if (latitude == Constants.UNKNOW_VALUE) {
                latitude = 999;
                isSuccess = false;
            }

            if (longitude == Constants.UNKNOW_VALUE) {
                longitude = 999;
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果

                if (altitude != Constants.UNKNOW_VALUE) {
                    altitude = location.getAltitude();// 单位：米
                }
                gpsType = Constants.GPS_GPS;

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                gpsType = Constants.WIFI_GPS;
            } else {
                gpsType = Constants.GPRS_GPS;
            }
            String address = location.getAddrStr();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("gpsType", gpsType);
            params.put("gpsHeight", String.valueOf(altitude));
            params.put("gpsLongitude", String.valueOf(longitude));
            params.put("gpsLatitude", String.valueOf(latitude));
            if (isSuccess) {
                if (StringUtils.isNotBlank(address)) {
                    params.put("gpsAddress", address);
                } else {
                    isSuccess = false;
                    params.put("gpsAddress", "");
                }

            } else {
                params.put("gpsAddress", "");
            }
            params.put("isSuccess", isSuccess);
            String dataJson = JSONObject.toJSONString(params);

            LogUtils.d("test", dataJson);
            JsConnectManager.loadJavascript(moonWebView, locationCallbackName, dataJson);

        }

    };

    private void callback(String result) {
        if (scanCallbackName != null) {
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
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog = null;
        if (map != null) {
            map.clear();
            map = null;
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isBackByJs && viewNowify.getVisibility() == View.VISIBLE) {
                finish();
            } else if (isBackByJs) {
                JsConnectManager.keyBack(moonWebView);
            } else if (moonWebView.canGoBack()) {
                moonWebView.goBack();
                popTitle();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            if (requestCode == Constants.REQUEST_SCAN) {
                callback("CANCELED");
            }
            cancelReceiveValue();
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_SCAN:
                    if (data == null) return;
                    callback(data.getStringExtra(LibConstants.SCAN_RESULT));
                    break;
                case Constants.CHOSE_PIC_RESULT:
                    if (!isFiveAbove && mUploadMessage == null) return;
                    if (isFiveAbove && mFilePathCallback == null) return;
                    Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                    if (result == null) {
                        cancelReceiveValue();
                        return;
                    }
                    String path = LibFileUtil.getPath(this, result);
                    if (TextUtils.isEmpty(path)) {
                        cancelReceiveValue();
                        return;
                    }
                    Uri uri = Uri.fromFile(new File(path));
                    setReceiveValue(uri);
                    break;
                case Constants.TAKE_PIC_RESULT:
                    if (!isFiveAbove && mUploadMessage == null) return;
                    if (isFiveAbove && mFilePathCallback == null) return;
                    Uri uri2 = takePhotoPop.getTakeImageUri();
                    setReceiveValue(uri2);
                    break;
            }
        }
    }

    private void setReceiveValue(Uri uri) {
        LogUtils.i("UPFILE", "onActivityResult after parser uri:" + uri.toString());
        if (mFilePathCallback != null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFilePathCallback.onReceiveValue(new Uri[]{uri});
            mFilePathCallback = null;
        } else if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
        }
    }

    private void cancelReceiveValue() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
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
        } else if (v == btnRefresh) {
            load(url);
        }
    }

    protected void initView() {

    }

    //下载方法，可重写
    protected void downClick(String url, String mimeType) {
        PublicUtil.openUrl(this, url);
    }
}
  
