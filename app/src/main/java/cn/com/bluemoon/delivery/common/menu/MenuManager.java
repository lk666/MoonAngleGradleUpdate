package cn.com.bluemoon.delivery.common.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import bluemoon.com.lib_x5.utils.JsBridgeUtil;
import cn.com.bluemoon.delivery.MainActivity;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBannerList;
import cn.com.bluemoon.delivery.app.api.model.ModelNum;
import cn.com.bluemoon.delivery.app.api.model.ResultUserRight;
import cn.com.bluemoon.delivery.app.api.model.UserRight;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.coupons.CouponsTabActivity;
import cn.com.bluemoon.delivery.module.evidencecash.EvidenceCashActivity;
import cn.com.bluemoon.delivery.module.extract.ExtractTabActivity;
import cn.com.bluemoon.delivery.module.inventory.InventoryTabActivity;
import cn.com.bluemoon.delivery.module.jobrecord.PromoteActivity;
import cn.com.bluemoon.delivery.module.notice.MessageListActivity;
import cn.com.bluemoon.delivery.module.notice.NoticeListActivity;
import cn.com.bluemoon.delivery.module.notice.PaperListActivity;
import cn.com.bluemoon.delivery.module.offline.MyCoursesActivity;
import cn.com.bluemoon.delivery.module.offline.MyTrainActivity;
import cn.com.bluemoon.delivery.module.order.OrdersTabActivity;
import cn.com.bluemoon.delivery.module.ptxs60.GroupBuyListActivity;
import cn.com.bluemoon.delivery.module.storage.StorageTabActivity;
import cn.com.bluemoon.delivery.module.team.MyTeamActivity;
import cn.com.bluemoon.delivery.module.ticket.TicketChooseActivity;
import cn.com.bluemoon.delivery.module.track.TrackManager;
import cn.com.bluemoon.delivery.module.wash.appointment.AppointmentTabActivity;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.EnterpriseWashTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.CloseBoxTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.clothescheck.ClothesCheckTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.cupboard.CupboardScanActivity;
import cn.com.bluemoon.delivery.module.wash.returning.driver.DriverTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.expressclosebox.ExpressCloseBoxTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.manager.ReturnManagerTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.pack.PackTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.transportreceive.TransportReceiveTabActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 菜单管理类
 * Created by bm on 2017/11/16.
 */

public class MenuManager {

    private static MenuManager instance;


    public static MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }

    /**
     * 点击菜单处理方法
     */
    public void onClickMenu(MainActivity main, UserRight userRight) {

        if (PublicUtil.isFastDoubleClick(1000)) {
            return;
        }

        //数据埋点
        TrackManager.addMenu(userRight.getMenuId(), userRight.getUrl());

        String menuCode = userRight.getMenuCode();
        LogUtils.d("view:" + menuCode);
        try {
            if (compare(MenuCode.dispatch, menuCode)) {
                OrdersTabActivity.actionStart(main);
            } else if (compare(MenuCode.site_sign, menuCode)) {
                ExtractTabActivity.actionStart(main);
            } else if (compare(MenuCode.check_in, menuCode)) {
                ViewUtil.showActivity(main, TicketChooseActivity.class);
            } else if (compare(MenuCode.mall_erp_delivery, menuCode)) {
                InventoryTabActivity.actionStart(main, InventoryTabActivity.DELIVERY_MANAGEMENT);
            } else if (compare(MenuCode.mall_erp_receipt, menuCode)) {
                InventoryTabActivity.actionStart(main, InventoryTabActivity.RECEIVE_MANAGEMENT);
            } else if (compare(MenuCode.mall_erp_stock, menuCode)) {
                StorageTabActivity.actionStart(main);
            } else if (compare(MenuCode.punch_card, menuCode)) {
                gotoPunchCard(main);
            } else if (compare(MenuCode.card_coupons, menuCode)) {
                ViewUtil.showActivity(main, CouponsTabActivity.class);
            } else if (compare(MenuCode.my_news, menuCode)) {
                ViewUtil.showActivity(main, MessageListActivity.class);
            } else if (compare(MenuCode.my_inform, menuCode)) {
                ViewUtil.showActivity(main, NoticeListActivity.class);
            } else if (compare(MenuCode.knowledge_base, menuCode)) {
                ViewUtil.showActivity(main, PaperListActivity.class);
            } else if (compare(MenuCode.customer_service, menuCode)) {
                DialogUtil.showServiceDialog(main);
            } else if (compare(MenuCode.receive_clothes_manager, menuCode)) {
                ClothingTabActivity.actionStart(main, ClothingTabActivity
                        .WITH_ORDER_COLLECT_MANAGE);
            } else if (compare(MenuCode.activity_collect_clothes, menuCode)) {
                ClothingTabActivity.actionStart(main, ClothingTabActivity
                        .WITHOUT_ORDER_COLLECT_MANAGE);
            } else if (compare(MenuCode.promote_file, menuCode)) {
                ViewUtil.showActivity(main, PromoteActivity.class);
            } else if (compare(MenuCode.my_team, menuCode)) {
                ViewUtil.showActivity(main, MyTeamActivity.class);
            } else if (compare(MenuCode.wash_cabinet_manager, menuCode)) {
                CupboardScanActivity.actStart(main);
            } else if (compare(MenuCode.wash_transport, menuCode)) {
                DriverTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_express_close_box, menuCode)) {
                ExpressCloseBoxTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_back_order_manager, menuCode)) {
                ReturnManagerTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_transport_sign, menuCode)) {
                TransportReceiveTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_carriage_close_box, menuCode)) {
                CloseBoxTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_back_order_package, menuCode)) {
                PackTabActivity.actionStart(main);
            } else if (compare(MenuCode.wash_clothes_check, menuCode)) {
                ClothesCheckTabActivity.actionStart(main);
            } else if (compare(MenuCode.my_deposit, menuCode)) {
                ViewUtil.showActivity(main, EvidenceCashActivity.class);
            } else if (compare(MenuCode.receive_appointment_manager, menuCode)) {
                AppointmentTabActivity.actionStart(main);
            } else if (compare(MenuCode.receive_enterprise_manager, menuCode)) {
                EnterpriseWashTabActivity.actionStart(main);
            } else if (compare(MenuCode.offline_training_student, menuCode)) {
                MyTrainActivity.actionStart(main);
            } else if (compare(MenuCode.offline_training_teacher, menuCode)) {
                MyCoursesActivity.actionStart(main);
            } else if (compare(MenuCode.ptxs_60, menuCode)) {
                GroupBuyListActivity.actStart(main);
            }

            // TODO: 2017/11/16 在这里添加原生模块

            //下面是网页跳转
            else if (!TextUtils.isEmpty(userRight.getUrl())) {
                String param;
                if ("ttly".equals(userRight.getMenuCode()) || "lhq".equals(userRight.getMenuCode
                        ())) {
                    param = "userId=" + ClientStateManager.getUserName();
                } else {
                    param = "token=" + ClientStateManager.getLoginToken();
                }
                String url = userRight.getUrl() + (!userRight.getUrl().contains("?") ? "?" : "&")
                        + param;
                LogUtils.d("url==>" + url);
                PublicUtil.openWebView(main, url, JsBridgeUtil.getTitleType(url) == 0 ? null :
                        userRight.getMenuName());
            } else {
                ViewUtil.toast(R.string.main_tab_no_data);
            }
        } catch (Exception ex) {
            ViewUtil.toast(ex.getMessage());
        }
    }

    private boolean compare(MenuCode enumCode, String menuCode) {
        return enumCode.toString().equals(menuCode);
    }

    /**
     * 点击打卡菜单处理
     */
    private void gotoPunchCard(final MainActivity aty) {
        if (PublicUtil.isTipsByDay(aty)) {
            new CommonAlertDialog.Builder(aty)
                    .setMessage(R.string.card_get_location_tips)
                    .setNegativeButton(R.string.btn_setting,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    aty.startActivity(new Intent("android.settings" +
                                            ".LOCATION_SOURCE_SETTINGS"));
                                }
                            })
                    .setPositiveButton(R.string.btn_later, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aty.requestPunchCard();
                        }
                    })
                    .show();
        } else {
            aty.requestPunchCard();
        }
    }

    /**
     * 组装分组数据
     */
    public List<MenuSection> getMenuList(ResultUserRight resultUserRight) {
        List<MenuSection> list = new ArrayList<>();
        if (resultUserRight.rightsList != null && resultUserRight.rightsList.size() > 0) {
            // 用SparseArray分组，可减少for层级
            SparseArray<List<MenuSection>> menuMap = new SparseArray<>();
            for (int i = 1; i <= resultUserRight.groupCount; i++) {
                menuMap.put(i, new ArrayList<MenuSection>());
            }
            for (UserRight right : resultUserRight.rightsList) {
                List<MenuSection> value = menuMap.get(right.getGroupNum());
                if (value != null) {
                    value.add(new MenuSection(right));
                }
            }
            for (int i = 1; i <= resultUserRight.groupCount; i++) {
                List<MenuSection> item = menuMap.get(i);

                //补充空白
                int index = (4 - item.size() % 4) % 4;
                for (int j = 0; j < index; j++) {
                    UserRight right = new UserRight();
                    right.setMenuCode(MenuCode.empty.toString());
                    item.add(new MenuSection(right));
                }

                if (item.size() > 0) {
                    list.add(new MenuSection(true, item.get(0).t.getGroupName()));
                    list.addAll(item);
                }
            }
        }
        return list;
    }

    /**
     * 设置角标数据
     */
    public void setAmount(Context context, MenuAdapter menuAdapter, ResultUserRight
            resultUserRight, List<ModelNum> modelNum) {
        if (resultUserRight != null && modelNum != null) {
            int sum = 0;
            //设置菜单角标
            for (UserRight right : resultUserRight.rightsList) {
                boolean isExit = false;
                for (ModelNum num : modelNum) {
                    if (num != null && right.getMenuCode().equals(num.getMenuId())) {
                        right.setAmount(num.getNum());
                        sum += num.getNum();
                        isExit = true;
                        break;
                    }
                }
                if (!isExit) {
                    right.setAmount(0);
                }
            }
            //更新数据
            menuAdapter.notifyDataSetChanged();
            //更新桌面角标
            PublicUtil.setMainAmount(context, sum);
        }
    }


    /**
     * 推送跳转
     */
    public void jump(MainActivity aty, Intent intent) {
        String view = PublicUtil.getPushView(intent);
        String url = PublicUtil.getPushUrl(intent);
        if ((!TextUtils.isEmpty(view) && !Constants.PUSH_H5.equals(view))
                || (Constants.PUSH_H5.equals(view) && !TextUtils.isEmpty(url))) {
            UserRight userRight = new UserRight();
            userRight.setMenuId("");
            userRight.setMenuCode(view);
            userRight.setUrl(url);
            userRight.setMenuName("");
            onClickMenu(aty, userRight);
        }
    }

    /**
     * 点击banner
     */
    public void clickBanner(MainActivity aty, ResultBannerList.ListBean item) {
        if ("jump".equals(item.intentionType)||"h5_link".equals(item.intentionType)) {
            UserRight userRight = new UserRight();
            userRight.setMenuId(item.menuId == null ? "" : item.menuId);
            userRight.setMenuCode(item.menuCode);
            userRight.setUrl(item.bannerUrl == null ? "" : item.bannerUrl);
            userRight.setMenuName("");
            onClickMenu(aty, userRight);
        } else if ("link".equals(item.intentionType)) {
            PublicUtil.openWebView(aty, item.bannerUrl, "");
        }

    }

}
