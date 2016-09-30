package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.entity.WashModeType;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

public class DriverTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(WaitLoadFragment.class,
                R.drawable.tab_driver_wait_load,
                R.string.driver_tab_wait_load));
        tabs.add(new TabState(WaitSendFragment.class,
                R.drawable.tab_driver_wait_send,
                R.string.driver_tab_wait_send));
        tabs.add(new TabState(CarriageHistoryFragment.class,
                R.drawable.tab_history,
                R.string.close_box_history));
        actionStart(context, tabs, DriverTabActivity.class);
    }

    public WashModeType getModeType(){
        return WashModeType.DRIVER_CARRIER_MODEL;
    }

}
