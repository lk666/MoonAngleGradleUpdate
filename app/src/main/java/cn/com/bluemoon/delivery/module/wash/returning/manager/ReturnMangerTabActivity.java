package cn.com.bluemoon.delivery.module.wash.returning.manager;


import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.module.storage.StockFragment;
import cn.com.bluemoon.delivery.module.storage.WarehouseFragment;

public class ReturnMangerTabActivity extends BaseTabActivity {

	public static void actionStart(Context context){
		ArrayList<TabState> tabs = new ArrayList<>();
		tabs.add(new TabState(DeliveryFragment.class,
				R.drawable.tab_stock_selector,
				R.string.manger_tab_1));
		tabs.add(new TabState(ReturnFragment.class,
				R.drawable.tab_my_warehouse_selector,
				R.string.manger_tab_2));
		tabs.add(new TabState(SignFragment.class,
				R.drawable.tab_my_warehouse_selector,
				R.string.manger_tab_3));
		tabs.add(new TabState(ReturnHistoryFragment.class,
				R.drawable.tab_my_warehouse_selector,
				R.string.manger_tab_4));
		actionStart(context, tabs, ReturnMangerTabActivity.class);
	}

}
