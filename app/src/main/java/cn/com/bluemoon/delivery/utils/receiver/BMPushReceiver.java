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
            // TODO: 2016/10/24 网页跳转 
            if (Constants.PUSH_H5.equals(menuCode) && !TextUtils.isEmpty(url)) {
                intent = new Intent(context, DownWebViewActivity.class);
                intent.putExtra("url", url + (!url.contains("?") ? "?" : "&") +
                        "token=" + token);
                intent.putExtra("back", false);
            }
            /*// TODO: 2016/10/24 原生跳转
            else if (MenuCode.dispatch.toString().equals(menuCode)) {
                intent = new Intent(context, OrdersTabActivity.class);
            } else if (MenuCode.site_sign.toString().equals(menuCode)) {
                intent = new Intent(context, ExtractTabActivity.class);
            } else if (MenuCode.check_in.toString().equals(menuCode)) {
                intent = new Intent(context, TicketChooseActivity.class);
            } else if (MenuCode.card_coupons.toString().equals(menuCode)) {
                intent = new Intent(context, CouponsTabActivity.class);
            }
            // TODO: 2016/10/19 仓库管理
            else if (MenuCode.mall_erp_delivery.toString().equals(menuCode)) {
                intent = new Intent(context, InventoryTabActivity.class);
                intent.putExtra("type", InventoryTabActivity.DELIVERY_MANAGEMENT);
            } else if (MenuCode.mall_erp_receipt.toString().equals(menuCode)) {
                intent = new Intent(context, InventoryTabActivity.class);
                intent.putExtra("type", InventoryTabActivity.RECEIVE_MANAGEMENT);
            } else if (MenuCode.mall_erp_stock.toString().equals(menuCode)) {
                intent = new Intent(context, StorageTabActivity.class);
            }
            // TODO: 2016/10/19 知识库
            else if (MenuCode.my_news.toString().equals(menuCode)) {
                intent = new Intent(context, MessageListActivity.class);
            } else if (MenuCode.my_inform.toString().equals(menuCode)) {
                intent = new Intent(context, NoticeListActivity.class);
            } else if (MenuCode.knowledge_base.toString().equals(menuCode)) {
                intent = new Intent(context, PaperListActivity.class);
            }
            // TODO: 2016/10/19 CEO
            else if (MenuCode.promote_file.toString().equals(menuCode)) {
                intent = new Intent(context, PromoteActivity.class);
            } else if (MenuCode.my_team.toString().equals(menuCode)) {
                intent = new Intent(context, MyTeamActivity.class);
            }
            // TODO: lk 2016/6/12 收衣管理是否需要？
            else if (MenuCode.receive_clothes_manager.toString().equals(menuCode)) {
                intent = new Intent(context, ClothingTabActivity.class);
                intent.putExtra("type", ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE);
            } else if (MenuCode.activity_collect_clothes.toString().equals(menuCode)) {
                intent = new Intent(context, ClothingTabActivity.class);
                intent.putExtra("type", ClothingTabActivity.WITHOUT_ORDER_COLLECT_MANAGE);
            } */
            // TODO: 2016/10/25 统一跳转到Main
            else {
                intent = new Intent(context, MainActivity.class);
                intent.putExtra(Constants.PUSH_VIEW, menuCode);
            }
        } else {
            intent = new Intent(context, AppStartActivity.class);
            intent.putExtra(Constants.PUSH_VIEW, menuCode);
            if (Constants.PUSH_H5.equals(menuCode) && !TextUtils.isEmpty(url)) {
                intent.putExtra(Constants.PUSH_URL, url);
            }
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
