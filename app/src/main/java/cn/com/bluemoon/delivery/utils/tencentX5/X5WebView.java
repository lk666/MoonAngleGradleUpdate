package cn.com.bluemoon.delivery.utils.tencentX5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by allenli on 2017/4/19.
 */
public class X5WebView extends WebView {

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);//设置可执行js脚本
        webSetting.setUseWideViewPort(true);//设置网页适应手机屏幕
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setDisplayZoomControls(false);//隐藏缩放按钮
        webSetting.setAllowFileAccess(true); // 允许访问文件
        webSetting.setDomStorageEnabled(true);
        webSetting.setDatabaseEnabled(true);

        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    public X5WebView(Context arg0) {
        super(arg0);
        setBackgroundColor(85621);
    }

}
