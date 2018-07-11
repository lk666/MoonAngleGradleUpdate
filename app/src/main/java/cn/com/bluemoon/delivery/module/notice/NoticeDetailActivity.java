package cn.com.bluemoon.delivery.module.notice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.knowledge.ResultPaperDetail;
import cn.com.bluemoon.delivery.app.api.model.message.ResultInfoDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;


public class NoticeDetailActivity extends BaseActivity {
    @BindView(R.id.txt_notice_title)
    TextView txtNoticeTitle;
    @BindView(R.id.txt_notice_date)
    TextView txtNoticeDate;
    @BindView(R.id.wv_notice)
    WebView wvNotice;
    private int mode;
    private String id;
    private CommonActionBar actionBar;
    private boolean isCollect;

    public static void startAction(Activity context, String id, int type, int requestCode) {
        Intent intent = new Intent(context, NoticeDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent() != null) {
            mode = getIntent().getIntExtra("type", -1);
            id = getIntent().getStringExtra("id");
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notice_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(mode == Constants.TYPE_KNOWLEDGE ? R.string.paper_detail_title : R
                .string.notice_detail_title);
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
                view.loadUrl(url);
                return true;
            }

        });
    }

    @Override
    public void initData() {
        switch (mode) {
            case Constants.TYPE_NOTICE:
                showWaitDialog();
                DeliveryApi.getInfoDetail(getToken(), id, getNewHandler(0, ResultInfoDetail.class));
                break;
            case Constants.TYPE_KNOWLEDGE:
                showWaitDialog();
                DeliveryApi.getPaperDetail(getToken(), id, getNewHandler(1, ResultPaperDetail
                        .class));
                break;
            default:
                break;
        }
    }

    private void setData(String str) {
        wvNotice.loadData(str, "text/html;charset=UTF-8", null);
    }

    @Override
    public void setActionBar(CommonActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        showWaitDialog();
        DeliveryApi.collectPaper(getToken(), id, !isCollect, getNewHandler(2, ResultBase.class));
    }

    private void setActionBarRightView(boolean isCollect) {
        Drawable drawableFillter;
        if (isCollect) {
            drawableFillter = getResources().getDrawable(R.mipmap.paper_detail_collected);
        } else {
            drawableFillter = getResources().getDrawable(R.mipmap.paper_detail_collect);
        }
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawablePadding(10);
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        if (actionBar.getTvRightView().getVisibility() == View.GONE) {
            actionBar.getTvRightView().setText(R.string.paper_detail_collect);
            actionBar.getTvRightView().setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                ResultInfoDetail infoDetailResult = (ResultInfoDetail) result;
                txtNoticeTitle.setText(infoDetailResult.getInfoTitle());
                txtNoticeDate.setText(String.format(getString(R.string.paper_detail_date),
                        DateUtil.getTime(infoDetailResult.getReleaseTime(), "yyyy-MM-dd " +
                                "HH:mm")));
                setData(infoDetailResult.getInfoContent());
                break;
            case 1:
                ResultPaperDetail paperDetailResult = (ResultPaperDetail) result;
                isCollect = paperDetailResult.isCollect;
                setActionBarRightView(isCollect);
                txtNoticeTitle.setText(paperDetailResult.getPaperTitle());
                txtNoticeDate.setText(String.format(getString(R.string.paper_detail_date),
                        DateUtil.getTime(paperDetailResult.getReleaseTime(), "yyyy-MM-dd " +
                                "HH:mm")));
                setData(paperDetailResult.getPaperContent());
                break;
            case 2:
                PublicUtil.showToast(result.getResponseMsg());
                isCollect = !isCollect;
                setActionBarRightView(isCollect);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActionBarBtnLeftClick() {
        back();
    }

    private void back(){
        if(wvNotice.canGoBack()){
            wvNotice.goBack();
        }else{
            this.setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

}
