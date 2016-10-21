package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * 衣物清点
 */
public class ClothesCheckTabActivity extends BaseTabActivity {

    public static void actionStart(Context context) {
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new TabState(ClothesCheckFragment.class,
                R.drawable.tab_clothes_check_selector,
                R.string.clothes_check_title));
        tabs.add(new TabState(ClothesCheckHistoryFragment.class,
                R.drawable.tab_history,
                R.string.close_box_history));
        actionStart(context, tabs, ClothesCheckTabActivity.class);
    }
}