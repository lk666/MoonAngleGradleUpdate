package cn.com.bluemoon.delivery.utils.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.com.bluemoon.delivery.AppStartActivity;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.app.api.model.MenuCode;
import cn.com.bluemoon.delivery.app.api.model.PushItem;
import cn.com.bluemoon.delivery.app.api.model.ResultUserRight;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.WebViewActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.module.coupons.CouponsTabActivity;
import cn.com.bluemoon.delivery.module.extract.ExtractTabActivity;
import cn.com.bluemoon.delivery.module.inventory.InventoryTabActivity;
import cn.com.bluemoon.delivery.module.jobrecord.PromoteActivity;
import cn.com.bluemoon.delivery.module.notice.MessageListActivity;
import cn.com.bluemoon.delivery.module.notice.NoticeListActivity;
import cn.com.bluemoon.delivery.module.notice.PaperListActivity;
import cn.com.bluemoon.delivery.module.order.OrdersTabActivity;
import cn.com.bluemoon.delivery.module.storage.StorageTabActivity;
import cn.com.bluemoon.delivery.module.team.MyTeamActivity;
import cn.com.bluemoon.delivery.module.ticket.TicketChooseActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.NotificationUtil;

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
                    /*展示通知消息*/
                    showNotification(context, item);
                    /*更新桌面图标数字*/
                    PublicUtil.setMainAmount(context, item.getContParam().getNum());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 处理跳转逻辑
     *
     * @param context
     * @param item
     */
    private void showNotification(Context context, PushItem item) {
        if (item == null) {
            return;
        }
        String title = item.getTitle();
        String content = item.getDescription();
        String menuCode = item.getContParam().getView();
        String url = item.getContParam().getUrl();
        LogUtils.d(TAG, "updateContent:" + menuCode);
        String token = ClientStateManager.getLoginToken();

        Intent intent = new Intent();
        if (isAppRunning(context) && !TextUtils.isEmpty(menuCode) && !TextUtils.isEmpty(token)) {
            // TODO: 2016/10/24 网页跳转 
            if (Constants.PUSH_H5.equals(menuCode) && !TextUtils.isEmpty(url)) {
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", url + (!url.contains("?") ? "?" : "&") +
                        "token=" + token);
                intent.putExtra("back", false);
            }
            // TODO: 2016/10/24 原生跳转
            else if (MenuCode.dispatch.toString().equals(menuCode)) {
                intent.setClass(context, OrdersTabActivity.class);
            } else if (MenuCode.site_sign.toString().equals(menuCode)) {
                intent.setClass(context, ExtractTabActivity.class);
            } else if (MenuCode.check_in.toString().equals(menuCode)) {
                intent.setClass(context, TicketChooseActivity.class);
            } else if (MenuCode.card_coupons.toString().equals(menuCode)) {
                intent.setClass(context, CouponsTabActivity.class);
            }
            // TODO: 2016/10/19 仓库管理
            else if (MenuCode.mall_erp_delivery.toString().equals(menuCode)) {
                intent.setClass(context, InventoryTabActivity.class);
                intent.putExtra("type", InventoryTabActivity.DELIVERY_MANAGEMENT);
            } else if (MenuCode.mall_erp_receipt.toString().equals(menuCode)) {
                intent.setClass(context, InventoryTabActivity.class);
                intent.putExtra("type", InventoryTabActivity.RECEIVE_MANAGEMENT);
            } else if (MenuCode.mall_erp_stock.toString().equals(menuCode)) {
                intent.setClass(context, StorageTabActivity.class);
            }
            // TODO: 2016/10/19 知识库
            else if (MenuCode.my_news.toString().equals(menuCode)) {
                intent.setClass(context, MessageListActivity.class);
            } else if (MenuCode.my_inform.toString().equals(menuCode)) {
                intent.setClass(context, NoticeListActivity.class);
            } else if (MenuCode.knowledge_base.toString().equals(menuCode)) {
                intent.setClass(context, PaperListActivity.class);
            }
            // TODO: 2016/10/19 CEO
            else if (MenuCode.promote_file.toString().equals(menuCode)) {
                intent.setClass(context, PromoteActivity.class);
            } else if (MenuCode.my_team.toString().equals(menuCode)) {
                intent.setClass(context, MyTeamActivity.class);
            }
            // TODO: lk 2016/6/12 收衣管理是否需要？
            else if (MenuCode.receive_clothes_manager.toString().equals(menuCode)) {
                intent.setClass(context, ClothingTabActivity.class);
                intent.putExtra("type", ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE);
            } else if (MenuCode.activity_collect_clothes.toString().equals(menuCode)) {
                intent.setClass(context, ClothingTabActivity.class);
                intent.putExtra("type", ClothingTabActivity.WITHOUT_ORDER_COLLECT_MANAGE);
            } else {
                intent.setClass(context, AppStartActivity.class);
                intent.putExtra(Constants.PUSH_VIEW, menuCode);
            }
        } else {
            intent.setClass(context, MainActivity.class);
            intent.putExtra(Constants.PUSH_VIEW, menuCode);
            if(Constants.PUSH_H5.equals(menuCode) && !TextUtils.isEmpty(url)){
                intent.putExtra(Constants.PUSH_URL, url);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //展示消息通知
        NotificationUtil.simpleNotify(context,title,content,intent);
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
