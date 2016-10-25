package cn.com.bluemoon.delivery.module.wash.returning.manager;


import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.DrawableTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.entity.WashModeType;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

public class ReturnManagerTabActivity extends BaseTabActivity {

	public static void actionStart(Context context){
		ArrayList<TabState> tabs = new ArrayList<>();
		tabs.add(new DrawableTabState(DeliveryFragment.class,R.mipmap.tab_return_delivery_selected, R.mipmap.tab_return_delivery_normal,
				R.string.manger_tab_1));
		tabs.add(new DrawableTabState(ReturnFragment.class,R.mipmap.tab_return_alreay_selected, R.mipmap.tab_return_alreay_normal,
				R.string.manger_tab_2));
		tabs.add(new DrawableTabState(SignFragment.class,R.mipmap.tab_receipt_normal, R.mipmap.tab_receipt_disable,
				R.string.manger_tab_3));
		tabs.add(new DrawableTabState(ReturnHistoryFragment.class,R.mipmap.tab_history_normal, R.mipmap.tab_history_disable,
				R.string.manger_tab_4));
		actionStart(context, tabs, ReturnManagerTabActivity.class);
	}

	@Override
	protected WashModeType getModeType() {
		return WashModeType.BACK_ORDER_MANAGE_MODEL;
	}

	@Override
	protected int getCurrentIndex() {
		return 1;
	}
}
