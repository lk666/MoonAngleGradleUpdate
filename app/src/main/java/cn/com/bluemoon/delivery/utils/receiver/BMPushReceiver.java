package cn.com.bluemoon.delivery.utils.receiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.List;

import cn.com.bluemoon.delivery.AppStartActivity;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.app.api.model.PushItem;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.DownWebViewActivity;
import cn.com.bluemoon.delivery.common.WebViewActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.NotificationUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * Created by bm on 2016/10/19.
 */
public class BMPushReceiver extends PushGTReceiver {

    @Override
    protected void onClientId(Context context, String clientId) {
        if (!TextUtils.isEmpty(clientId)) {
            ClientStateManager.setClientId(clientId);
        }
    }

    @Override
    protected void onResult(Context context, String data, boolean isSuccess) {
        LogUtils.d(TAG, data);

        if (!TextUtils.isEmpty(data)) {
            try {
                PushItem item = JSON.parseObject(data, PushItem.class);
                if (item != null) {
                    /*发送通知，并更新桌面图标数字*/
                    PublicUtil.setMainAmount(context, item.getContParam().getNum(),
                            getNotification(context, item));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取带点击事件的通知
     *
     * @param context
     * @param item
     */
    private Notification getNotification(Context context, PushItem item) {
        if (item == null) {
            return null;
        }
        String title = item.getTitle();
        String content = item.getDescription();
        String menuCode = item.getContParam().getView();
        String url = item.getContParam().getUrl();
        LogUtils.d(TAG, "updateContent:" + menuCode);
        String token = ClientStateManager.getLoginToken();

        Intent intent;
        if (isAppRunning(context) && !TextUtils.isEmpty(menuCode) && !TextUtils.isEmpty(token)) {
            // 统一跳转到Main，再处理菜单跳转
            intent = MainActivity.getStartIntent(context,menuCode,url);
        } else {
            //如果程序没有启动，则跳转到欢迎页启动
            intent = AppStartActivity.getStartIntent(context,menuCode,url);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return NotificationUtil.getSimpleNotify(context, title, content, intent);
    }

    /**
     * 判断程序是否启动
     *
     * @param context
     * @return
     */
    private boolean isAppRunning(Context context) {
        boolean isAppRunning = false;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context
                    .ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
            String MY_PKG_NAME = context.getPackageName();
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity
                        .getPackageName().equals(MY_PKG_NAME)) {
                    isAppRunning = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAppRunning;
    }
}
