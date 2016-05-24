package cn.com.bluemoon.delivery.notice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.knowledge.ResultKnowledges;
import cn.com.bluemoon.delivery.app.api.model.knowledge.ResultPaperDetail;
import cn.com.bluemoon.delivery.app.api.model.message.ResultInfoDetail;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;


public class NoticeDetailActivity extends Activity {
    private String TAG = "NoticeDetailActivity";
    private NoticeDetailActivity main;
    private CommonProgressDialog progressDialog;
    private TextView txtNoticeTitle;
    private TextView txtNoticeDate;
    private WebView wvNotice;
    private int mode;
    private String id;
    private CommonActionBar actionBar;
    private boolean isCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        initCustomActionBar();
        main = this;
        progressDialog = new CommonProgressDialog(this);
        initView();
        getData();
    }


    private void initView(){
        txtNoticeTitle =(TextView) findViewById(R.id.txt_notice_title);
        txtNoticeDate =(TextView) findViewById(R.id.txt_notice_date);
        wvNotice =(WebView) findViewById(R.id.wv_notice);

        WebSettings webSetting = wvNotice.getSettings();
        webSetting.setDomStorageEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setDefaultTextEncodingName("UTF-8");



        // 是webview获取到焦点，否则获取不了焦点在ActivityGroup
        wvNotice.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wvNotice.requestFocus();
                return false;
            }
        });

        wvNotice.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                // TODO Auto-generated method stub
                super.onReceivedTitle(view, title);

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        wvNotice.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				LogUtils.d("jsConnect", "result =" + url);
                return true;
            }
        });
    }

    private void getData() {
        if(getIntent()!=null){
            mode = getIntent().getIntExtra("type",-1);
            id = getIntent().getStringExtra("id");
        }
        switch (mode){
            case Constants.TYPE_NOTICE:
                actionBar.getTitleView().setText(getString(R.string.notice_detail_title));
                if(progressDialog!=null) progressDialog.show();
                DeliveryApi.getInfoDetail(ClientStateManager.getLoginToken(main), id, noticeHandler);
                break;
            case Constants.TYPE_KNOWLEDGE:
                actionBar.getTitleView().setText(getString(R.string.paper_detail_title));
                if(progressDialog!=null) progressDialog.show();
                DeliveryApi.getPaperDetail(ClientStateManager.getLoginToken(main), id, knowledgerHandler);
                break;
            default:
                break;

        }

    }

    private void setData(String str){
        wvNotice.loadData(str,"text/html;charset=UTF-8",null);
    }


    private void initCustomActionBar() {
        actionBar = new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {
                if (progressDialog != null) progressDialog.show();
                DeliveryApi.collectPaper(ClientStateManager.getLoginToken(main),id,!isCollect,collectHandler);
            }

            @Override
            public void onBtnLeft(View v) {
                NoticeDetailActivity.this.finish();
            }

            @Override
            public void setTitle(TextView v) {

            }
        });

    }

    private void setActionBarRightView(boolean isCollect){
        Drawable drawableFillter;
        if(isCollect){
            drawableFillter=getResources().getDrawable(R.mipmap.paper_detail_collected);
        }else{
            drawableFillter=getResources().getDrawable(R.mipmap.paper_detail_collect);
        }
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawablePadding(10);
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        if(actionBar.getTvRightView().getVisibility() == View.GONE){
            actionBar.getTvRightView().setText(R.string.paper_detail_collect);
            actionBar.getTvRightView().setVisibility(View.VISIBLE);
        }
    }

    AsyncHttpResponseHandler noticeHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "getInfoDetail result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultInfoDetail infoDetailResult = JSON.parseObject(responseString,
                        ResultInfoDetail.class);
                if (infoDetailResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    txtNoticeTitle.setText(infoDetailResult.getInfoTitle());
                    txtNoticeDate.setText(String.format(getString(R.string.paper_detail_date),
                            DateUtil.getTime(infoDetailResult.getReleaseTime(), "yyyy-MM-dd HH:mm:ss")));
                    setData(infoDetailResult.getInfoContent());
                } else {
                    PublicUtil.showErrorMsg(main, infoDetailResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if (progressDialog != null)
                progressDialog.dismiss();
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler knowledgerHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "getPaperDetail result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultPaperDetail paperDetailResult = JSON.parseObject(responseString,
                        ResultPaperDetail.class);
                if (paperDetailResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    isCollect = paperDetailResult.isCollect;
                    setActionBarRightView(isCollect);
                    txtNoticeTitle.setText(paperDetailResult.getPaperTitle());
                    txtNoticeDate.setText(String.format(getString(R.string.paper_detail_date),
                            DateUtil.getTime(paperDetailResult.getReleaseTime(), "yyyy-MM-dd HH:mm:ss")));
                    setData(paperDetailResult.getPaperContent());
                } else {
                    PublicUtil.showErrorMsg(main, paperDetailResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if (progressDialog != null)
                progressDialog.dismiss();
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler collectHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "collectPaper result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultBase baseResult = JSON.parseObject(responseString,
                        ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(baseResult.getResponseMsg());
                    isCollect = !isCollect;
                    setActionBarRightView(isCollect);
                } else {
                    PublicUtil.showErrorMsg(main, baseResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if (progressDialog != null)
                progressDialog.dismiss();
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED)
            return;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

            }
        }
    }

}
