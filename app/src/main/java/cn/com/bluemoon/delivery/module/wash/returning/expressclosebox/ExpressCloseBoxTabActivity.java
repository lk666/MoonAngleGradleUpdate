package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * Created by ljl on 2016/9/28.
 */
public class ExpressCloseBoxTabActivity extends BaseTabActivity{

    public static void actionStart(Context context){
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(ExpressCloseBoxFragment.class,
                R.drawable.tab_stock_selector,
                R.string.express_close_box_tab1));
        tabs.add(new TabState(ExpressHistoryFragment.class,
                R.drawable.tab_my_warehouse_selector,
                R.string.express_close_box_tab2));
        actionStart(context, tabs, ExpressCloseBoxTabActivity.class);
    }
}
