package cn.com.bluemoon.delivery.module.wash.returning.transportreceive;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.entity.WashModeType;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * 承运收货
 */
public class TransportReceiveTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(WaitSignFragment.class,
                R.drawable.tab_wait_sign_selector,
                R.string.wait_sign_title));
        tabs.add(new TabState(SignHistoryFragment.class,
                R.drawable.tab_history,
                R.string.close_box_history));
        actionStart(context, tabs, TransportReceiveTabActivity.class);
    }

    @Override
    protected WashModeType getModeType() {
        return WashModeType.CARRIAGE_RECEIVE_MODEL;
    }
}