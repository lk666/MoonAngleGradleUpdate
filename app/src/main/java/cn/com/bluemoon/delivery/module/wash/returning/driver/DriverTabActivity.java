package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.CornerNum;
import cn.com.bluemoon.delivery.app.api.model.wash.ResultCornerNum;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.entity.WashModeType;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.utils.LogUtils;

public class DriverTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(WaitLoadFragment.class,
                R.drawable.tab_receive_selector,
                R.string.driver_tab_wait_load));
        tabs.add(new TabState(WaitSendFragment.class,
                R.drawable.tab_received_selector,
                R.string.driver_tab_wait_send));
        tabs.add(new TabState(CarriageHistoryFragment.class,
                R.drawable.tab_received_selector,
                R.string.close_box_history));
        actionStart(context, tabs, DriverTabActivity.class);
    }

    public WashModeType getModeType(){
        return WashModeType.DRIVER_CARRIER_MODEL;
    }

}
