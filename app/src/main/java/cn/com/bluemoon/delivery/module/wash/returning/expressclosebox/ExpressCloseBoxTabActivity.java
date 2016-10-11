package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.content.Context;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.DrawableTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * Created by ljl on 2016/9/28.
 */
public class ExpressCloseBoxTabActivity extends BaseTabActivity{

    public static void actionStart(Context context){
        ArrayList<TabState> tabs = new ArrayList<>();
        tabs.add(new DrawableTabState(ExpressCloseBoxFragment.class,
                R.mipmap.tab_express_closebox_selected,R.mipmap.tab_express_closebox_normal,
                R.string.express_close_box_tab1));
        tabs.add(new DrawableTabState(ExpressHistoryFragment.class,
                R.mipmap.tab_history_normal,R.mipmap.tab_history_disable,
                R.string.express_close_box_tab2));
        actionStart(context, tabs, ExpressCloseBoxTabActivity.class);
    }
}
