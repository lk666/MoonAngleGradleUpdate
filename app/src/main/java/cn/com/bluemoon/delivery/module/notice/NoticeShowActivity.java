package cn.com.bluemoon.delivery.module.notice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.message.ResultInfoDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;


public class NoticeShowActivity extends BaseActivity {
    @BindView(R.id.txt_notice_title)
    TextView txtNoticeTitle;
    @BindView(R.id.txt_notice_date)
    TextView txtNoticeDate;
    @BindView(R.id.wv_notice)
    WebView wvNotice;
    private ArrayList<String> ids;

    private boolean isRead;

    public static void startAction(Activity context, ArrayList<String> ids) {
        Intent intent = new Intent(context, NoticeShowActivity.class);
        intent.putStringArrayListExtra("ids", ids);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent() != null) {
            ids = getIntent().getStringArrayListExtra("ids");
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notice_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.notice_detail_title);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initView() {
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
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(isClear){
                    view.clearHistory();
                    isClear = false;
                }
            }
        });
    }

    @Override
    public void initData() {
        if (ids == null || ids.isEmpty()) {
            backSuccess();
            return;
        }
        isRead = false;
        showWaitDialog();
        DeliveryApi.getInformation(getToken(), ids.get(0), getNewHandler(0, ResultInfoDetail.class));
    }

    private boolean isClear;
    private void setData(String str) {
        //每次更换通知时，清除上一个通知的webview缓存
        isClear = true;
        wvNotice.loadData(str, "text/html;charset=UTF-8", null);
    }

    private void backSuccess() {
        finish();
    }

    /**
     * 已读操作
     */
    private void read() {
        if (ids == null || ids.isEmpty()) {
            backSuccess();
            return;
        }
        showWaitDialog();
        DeliveryApi.readInfo(getToken(), ids.get(0), getNewHandler(1, ResultBase.class));
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        ViewUtil.setViewVisibility(titleBar.getImgLeftView(), View.GONE);
        titleBar.getTvRightView().setText("已读");
        ViewUtil.setViewVisibility(titleBar.getTvRightView(), View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        //  2017/8/21 调用已读接口
        if(isRead){
            read();
        }else{
            //获取详情失败的话，点击已读会再次刷新当前详情
            initData();
        }

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                //设置可以点击已读
                isRead = true;
                ResultInfoDetail infoDetailResult = (ResultInfoDetail) result;
                txtNoticeTitle.setText(infoDetailResult.getInfoTitle());
                txtNoticeDate.setText(String.format(getString(R.string.paper_detail_date),
                        DateUtil.getTime(infoDetailResult.getReleaseTime(), "yyyy-MM-dd " +
                                "HH:mm")));
                setData(infoDetailResult.getInfoContent());
                break;
            case 1:
                // 2017/8/21 处理已读结果
                ids.remove(0);
                initData();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //  2017/8/21 屏蔽掉返回键
            if(wvNotice.canGoBack()){
                wvNotice.goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
