package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultScanService;
import cn.com.bluemoon.delivery.common.qrcode.ScanActivity;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * 主菜单统一扫码界面，统一处理
 */
public class XScanActivity extends ScanActivity {

    public static void actStart(Activity context, Fragment fragment, String title, int
            requestCode) {
        actStart(context, fragment, XScanActivity.class, title, requestCode);
    }

    @Override
    protected void onResult(String str, String type, Bitmap barcode) {
        showWaitDialog();
        DeliveryApi.scanService(str, getToken(), getNewHandler(0, ResultScanService.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                setData((ResultScanService) result);
                break;
        }
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        finish();
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        finish();
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        finish();
    }

    /**
     * 处理服务器返回的结果
     */
    private void setData(ResultScanService result) {
        if (ResultScanService.TYPE_HTTP.equals(result.getType())) {
            ResultScanService.Http http = JSON.parseObject(result.getResult(),
                    ResultScanService.Http.class);
            String url = http.getUrl();
            String token = getToken();
            if (!TextUtils.isEmpty(url)) {
                if (!TextUtils.isEmpty(token)) {
                    if (url.contains("?")) {
                        url = url + "&token=" + token;
                    } else {
                        url = url + "?token=" + token;
                    }
                }

                PublicUtil.openWebView(this, url, null, false, false);
            }
        } else if (ResultScanService.TYPE_TEXT.equals(result.getType())) {
            ResultScanService.Text text = JSON.parseObject(result.getResult(),
                    ResultScanService.Text.class);
            toast(text.getText());
        } else if (ResultScanService.TYPE_INTERNAL.equals(result.getType())) {
            ResultScanService.Internal internal = JSON.parseObject(result.getResult(),
                    ResultScanService.Internal.class);
            if ("COURSE".equals(internal.target)) {
                // TODO: 2017/7/19 调转课程页面
            } else if ("PLAN".equals(internal.target)) {
                // TODO: 2017/7/19 调转排课页面
            }
        }

        finish();
    }
}
