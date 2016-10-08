package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.entity.WashModeType;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.CloseBoxHistoryFragment;

/**
 * Created by allenli on 2016/9/28.
 */
public class PackTabActivity extends BaseTabActivity {

    //
    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(PackFragment.class,
                R.drawable.tab_pack_selector,
                R.string.title_pack));
        tabs.add(new TabState(WaitInboxFragment.class,
                R.drawable.tab_box_selector,
                R.string.title_box));
        tabs.add(new TabState(CloseBoxHistoryFragment.class,
                R.drawable.tab_history,
                R.string.title_history));
        actionStart(context, tabs, PackTabActivity.class);
    }

    @Override
    protected WashModeType getModeType() {
        return WashModeType.CABINET_BACK_ORDER_MODEL;
    }
}
