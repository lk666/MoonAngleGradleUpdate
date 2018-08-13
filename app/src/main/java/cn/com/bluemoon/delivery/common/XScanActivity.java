package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultScanService;
import cn.com.bluemoon.delivery.common.qrcode.ScanActivity;
import cn.com.bluemoon.delivery.module.offline.CourseSignActivity;
import cn.com.bluemoon.delivery.module.offline.StudentScanPlanActivity;
import cn.com.bluemoon.delivery.module.offline.TeacherScanPlanActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;

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
                PublicUtil.openWebView(this, url, "");
            }
            finish();
        } else if (ResultScanService.TYPE_TEXT.equals(result.getType())) {
            ResultScanService.Text text = JSON.parseObject(result.getResult(),
                    ResultScanService.Text.class);
            toast(text.getText());
            finish();
        } else if (ResultScanService.TYPE_INTERNAL.equals(result.getType())) {
            ResultScanService.Internal internal = JSON.parseObject(result.getResult(),
                    ResultScanService.Internal.class);
            if ("COURSE".equals(internal.target)) {
                //跳转课程页面
                CourseSignActivity.actStart(this, internal.data.courseCode, internal.data
                        .planCode, internal.data.userMark, internal.data.userType, 0);
            } else if ("PLAN".equals(internal.target)) {
                //跳转排课页面
                TeacherScanPlanActivity.actStart(this, internal.data.planCode, internal.data
                        .userMark, internal.data.userType, 1);
            } else if ("PLAN_USER".equals(internal.target)) {
                //跳转排课签到页面
                StudentScanPlanActivity.actionStart(this, internal.data.planCode, 2);
            }
        }

    }

}
