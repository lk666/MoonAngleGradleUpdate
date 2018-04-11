package cn.com.bluemoon.delivery.module.notice;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;

import bluemoon.com.lib_x5.utils.JsUtil;
import bluemoon.com.lib_x5.widget.WebViewActionBar;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.WebViewActivity;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * Created by liangjiangli on 2018/4/9.
 */

public class NoticeNewShowActivity extends WebViewActivity {

    private ArrayList<String> ids;

    private boolean isRead;

    /**
     * 网页界面启动方法
     *
     * @param context    调用的类
     * @param title      网页标题 title为null时标题隐藏
     */
    public static void startAction(Context context, String title, ArrayList<String> ids) {
        Intent intent = getStartIntent(context, null, title, false,
                null, null, NoticeNewShowActivity.class);
        intent.putStringArrayListExtra("ids", ids);
        context.startActivity(intent);
    }


    @Override
    public void setActionBar(WebViewActionBar titleBar) {
        super.setActionBar(titleBar);
        ViewUtil.setViewVisibility(titleBar.getImgLeftView(), View.GONE);
        titleBar.getTvRightView().setText("已读");
        ViewUtil.setViewVisibility(titleBar.getTvRightView(), View.VISIBLE);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        if (getIntent() != null) {
            ids = getIntent().getStringArrayListExtra("ids");
        }
    }

    @Override
    public void initData() {
        if (isResetWebBack()) {
            JsUtil.addJsWebBack(this, webView);
        }
        if (ids == null || ids.isEmpty()) {
            backSuccess();
            return;
        }
        isRead = false;
        String url = String.format(BuildConfig.H5_DOMAIN,"FE/angue/noticeDetail/"+ids.get(0))+"?isRequire=true";
        webView.loadUrl(url);
    }

    @Override
    public void pageLoadFinish() {
        super.pageLoadFinish();
        isRead = true;
    }

    @Override
    public void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        //  2017/8/21 调用已读接口
        if(isRead){
            read();
        }else{
            //获取详情失败的话，点击已读会再次刷新当前详情
            initData();
        }
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
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 1:
                // 2017/8/21 处理已读结果
                ids.remove(0);
                initData();
                break;
        }
    }

    private void backSuccess() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //  2017/8/21 屏蔽掉返回键
            if(webView.canGoBack()){
                webView.goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
