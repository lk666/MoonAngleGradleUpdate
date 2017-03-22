package cn.com.bluemoon.delivery.module.extract;


import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.HistoryOrderType;
import cn.com.bluemoon.delivery.entity.DrawableTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.module.order.HistoryFragment;

public class ExtractTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        HistoryFragment.ordertype = HistoryOrderType.pickup;
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new DrawableTabState(ScanFragment.class, R.mipmap.tab_extract_scan_normal, R
                .mipmap.tab_extract_scan_diable,
                R.string.tab_bottom_scan_code_text));
        tabs.add(new DrawableTabState(TakeFragment.class, R.mipmap.tab_extract_take_normal, R
                .mipmap.tab_extract_take_disable,
                R.string.tab_bottom_take_text));
        tabs.add(new DrawableTabState(HistoryFragment.class, R.mipmap.tab_history_normal, R
                .mipmap.tab_history_disable,
                R.string.tab_bottom_show_history_text));
        actionStart(context, tabs, ExtractTabActivity.class);
    }

}
