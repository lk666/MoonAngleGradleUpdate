package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * 车辆承运封箱
 */
public class CloseBoxTabActivity extends BaseTabActivity {

    // TODO: lk 2016/9/14 图标
    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(CloseBoxFragment.class,
                R.drawable.tab_stock_selector,
                R.string.close_box_title));
        tabs.add(new TabState(CloseBoxHistoryFragment.class,
                R.drawable.tab_my_warehouse_selector,
                R.string.close_box_history));
        actionStart(context, tabs, CloseBoxTabActivity.class);
    }
// TODO: lk 2016/9/23 角标
}