package cn.com.bluemoon.delivery.module.wash.returning.transportreceive;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.CloseBoxFragment;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.CloseBoxHistoryFragment;

/**
 * 承运收货
 */
public class TransportReceiveTabActivity extends BaseTabActivity {

    // TODO: lk 2016/9/14 图标
    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(WaitSignFragment.class,
                R.drawable.tab_stock_selector,
                R.string.wait_sign_title));
        tabs.add(new TabState(SignHistoryFragment.class,
                R.drawable.tab_my_warehouse_selector,
                R.string.close_box_history));
        actionStart(context, tabs, TransportReceiveTabActivity.class);
    }
// TODO: lk 2016/9/23 角标 
}