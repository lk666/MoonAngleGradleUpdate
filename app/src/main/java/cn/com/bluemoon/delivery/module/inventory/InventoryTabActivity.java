package cn.com.bluemoon.delivery.module.inventory;


import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.ArgumentTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * 收发货界面
 */
public class InventoryTabActivity extends BaseTabActivity {

    public static String RECEIVE_MANAGEMENT = "RECEIVE_MANAGEMENT";
    public static String DELIVERY_MANAGEMENT = "DELIVERY_MANAGEMENT";

    public static void actionStart(Context context, String type) {
        ArrayList<TabState> tabs = new ArrayList<>();
        if (RECEIVE_MANAGEMENT.equals(type)) {
            tabs.add(new ArgumentTabState(SuspenseFragment.class,
                    R.drawable.tab_receive_selector,
                    R.string.tab_bottom_receive_text, RECEIVE_MANAGEMENT));
            tabs.add(new ArgumentTabState(ProcessedFragment.class,
                    R.drawable.tab_received_selector,
                    R.string.tab_bottom_received_text, RECEIVE_MANAGEMENT));
        } else {
            tabs.add(new ArgumentTabState(SuspenseFragment.class,
                    R.drawable.tab_deliver_selector,
                    R.string.tab_bottom_delivery_text, DELIVERY_MANAGEMENT));
            tabs.add(new ArgumentTabState(ProcessedFragment.class,
                    R.drawable.tab_delivered_selector,
                    R.string.tab_bottom_delivered_text, DELIVERY_MANAGEMENT));

        }
        actionStart(context, tabs, InventoryTabActivity.class);
    }
}
