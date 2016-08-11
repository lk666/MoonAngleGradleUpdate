package cn.com.bluemoon.delivery.module.storage;


import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

public class StorageTabActivity extends BaseTabActivity {

	public static void actionStart(Context context){
		ArrayList<TabState> tabs = new ArrayList<>();
		tabs.add(new TabState(StockFragment.class,
				R.drawable.tab_stock_selector,
				R.string.tab_bottom_my_stock_text));
		tabs.add(new TabState(WarehouseFragment.class,
				R.drawable.tab_my_warehouse_selector,
				R.string.tab_bottom_my_warehouse_text));
		actionStart(context, tabs, StorageTabActivity.class);
	}

}
