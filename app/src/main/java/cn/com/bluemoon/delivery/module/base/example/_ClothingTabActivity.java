package cn.com.bluemoon.delivery.module.base.example;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.ArgumentTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

public class _ClothingTabActivity extends BaseTabActivity {
    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(_WithoutOrderManageFragment.class, R.drawable
                .tab_without_order_receive_selector,
                R.string.tab_bottom_with_order_collect_manage));
        tabs.add(new ArgumentTabState(_CollectClothesRecordFragment.class, R.drawable
                .tab_without_order_record_selector,
                R.string.tab_bottom_with_order_collect_record, "WITHOUT_ORDER_COLLECT_MANAGE"));
        actionStart(context, tabs, _ClothingTabActivity.class);
    }
}
