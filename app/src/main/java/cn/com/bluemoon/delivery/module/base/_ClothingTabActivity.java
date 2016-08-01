package cn.com.bluemoon.delivery.module.base;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;

public class _ClothingTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(_WithOrderManageFragment.class, R.drawable
                .tab_without_order_receive_selector,
                R.string.tab_bottom_with_order_collect_manage));
        tabs.add(new TabState(_CollectClothesRecordFragment.class, R.drawable
                .tab_without_order_record_selector,
                R.string.tab_bottom_with_order_collect_record));

        actionStart(context, tabs);
    }
}
